package org.master.unitoo.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.project.MavenProject;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.collection.CollectResult;
import org.eclipse.aether.collection.DependencyCollectionContext;
import org.eclipse.aether.collection.DependencySelector;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyFilter;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.DependencyRequest;
import org.master.unitoo.core.api.components.IExternalService;

/**
 *
 */
@Mojo(name = "scan", defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public class UniTooScanner extends AbstractMojo {

    public static final String COMPONENTS_FILE_NAME = "components";

    @Component
    protected MavenProject project;

    @Component
    private RepositorySystem repoSystem;

    @Parameter(defaultValue = "${project.remoteProjectRepositories}", readonly = true)
    private List<RemoteRepository> projectRepos;

    @Parameter(defaultValue = "${repositorySystemSession}", readonly = true)
    private RepositorySystemSession repoSession;

    @Parameter(defaultValue = "${project.build.directory}/unitoo", property = "output", required = true)
    private File output;

    @Parameter(defaultValue = "${project.build.directory}/classes", property = "source", required = true)
    private File source;

    public void findClassFiles(File root, String path, List<String> classes) {
        File[] files = root.listFiles();
        if (files != null) {
            for (File file : files) {
                String fname = file.getName();
                if (file.isDirectory()) {
                    findClassFiles(file, path.isEmpty() ? fname : path + "." + fname, classes);
                } else if (fname.endsWith(".class")) {
                    fname = fname.substring(0, fname.length() - 6);
                    fname = path.isEmpty() ? fname : path + "." + fname;
                    classes.add(fname);
                }
            }
        }
    }

    private void scanDependencyURLs(RepositorySystemSession session, Dependency dependency, Map<String, URL> urls, List<String> classes) throws Exception {
        if (("compile".equals(dependency.getScope()) || "provided".equals(dependency.getScope())) && !dependency.isOptional() && !urls.containsKey(dependency.toString())) {
            CollectRequest collectRequest = new CollectRequest();
            collectRequest.setRoot(dependency);
            collectRequest.setRepositories(projectRepos);

            CollectResult result = repoSystem.collectDependencies(session, collectRequest);
            DependencyNode root = result.getRoot();
            if (root != null) {
                DependencyRequest dependencyRequest = new DependencyRequest();
                dependencyRequest.setRoot(root);
                dependencyRequest.setFilter(new DependencyFilter() {
                    @Override
                    public boolean accept(DependencyNode dn, List<DependencyNode> list) {
                        return !dn.getDependency().isOptional();
                    }
                });

                try {
                    repoSystem.resolveDependencies(session, dependencyRequest);

                    urls.put(root.getDependency().toString(), root.getArtifact().getFile().toURI().toURL());
                    scanJarClasses(root.getArtifact().getFile(), classes);
                    List<DependencyNode> childs = root.getChildren();
                    for (DependencyNode node : childs) {
                        scanDependencyURLs(session, node.getDependency(), urls, classes);
                    }
                } catch (Exception e) {
                    getLog().warn("Can't load dependency: " + dependency.getArtifact().getArtifactId());
                }
            }
        }
    }

    private void scanJarClasses(File file, List<String> classes) throws IOException {
        if (!file.isDirectory()) {
            try {
                JarFile jar = new JarFile(file);
                Manifest manifest = jar.getManifest();
                if ("library".equals(manifest.getMainAttributes().getValue("UniToo"))) {
                    Enumeration<JarEntry> entries = jar.entries();
                    while (entries.hasMoreElements()) {
                        JarEntry entry = entries.nextElement();
                        if (entry.getName().endsWith(".class") && !entry.isDirectory()) {
                            StringBuilder buf = new StringBuilder();
                            for (String part : entry.getName().split("/")) {
                                if (buf.length() != 0) {
                                    buf.append(".");
                                }
                                buf.append(part);
                                if (part.endsWith(".class")) {
                                    buf.setLength(buf.length() - ".class".length());
                                }
                            }

                            classes.add(buf.toString());
                        }
                    }
                }
            } catch (IOException e) {
                throw new IOException("Cant' access file: " + file.getName(), e);
            }
        }
    }

    @Override
    public void execute() throws MojoExecutionException {
        if (source == null) {
            getLog().warn("Compiled classes folder not found!");
        } else {
            output.mkdirs();
            ClassLoader original = Thread.currentThread().getContextClassLoader();
            try {
                ArrayList<String> classes = new ArrayList<>();
                findClassFiles(source, "", classes);
                Set<Artifact> artifacts = project.getDependencyArtifacts();
                Map<String, URL> urls = new HashMap<>();
                DefaultRepositorySystemSession session = new DefaultRepositorySystemSession(repoSession);
                session.setDependencySelector(new DependencySelector() {
                    @Override
                    public boolean selectDependency(Dependency dependency) {
                        return !dependency.isOptional() && "compile".equals(dependency.getScope());
                    }

                    @Override
                    public DependencySelector deriveChildSelector(DependencyCollectionContext dcc) {
                        return this;
                    }
                });

                for (Artifact artifact : artifacts) {
                    if ("compile".equals(artifact.getScope()) || "provided".equals(artifact.getScope())) {
                        Dependency dependency = new Dependency(new DefaultArtifact(artifact.getGroupId(), artifact.getArtifactId(), artifact.getClassifier(), "jar", artifact.getVersion()), "compile");
                        scanDependencyURLs(session, dependency, urls, classes);
                    }
                }

                urls.put("classes", source.toURI().toURL());
                Properties components = new Properties();

                URL[] urlpack = (URL[]) urls.values().toArray(new URL[urls.size()]);
                getLog().info(Arrays.toString(urlpack));
                ClassLoader loader = new URLClassLoader(urlpack, original);
                Thread.currentThread().setContextClassLoader(loader);
                getLog().info("Scanning components...");
                int cnt = 0;
                for (String className : classes) {
                    Class clazz;
                    try {
                        clazz = loader.loadClass(className);
                        if (IExternalService.class.isAssignableFrom(clazz) && clazz.isInterface()) {
                            org.master.unitoo.core.api.annotation.Component annotation = (org.master.unitoo.core.api.annotation.Component) clazz.getAnnotation(org.master.unitoo.core.api.annotation.Component.class);
                            if (annotation != null) {
                                getLog().info("External: " + clazz.getName());
                                components.setProperty(clazz.getName(), "");
                                cnt++;
                            }
                        } else if (!Modifier.isAbstract(clazz.getModifiers())) {
                            org.master.unitoo.core.api.annotation.Component annotation = (org.master.unitoo.core.api.annotation.Component) clazz.getAnnotation(org.master.unitoo.core.api.annotation.Component.class);
                            if (annotation != null) {
                                getLog().info("Component: " + clazz.getName());
                                components.setProperty(clazz.getName(), "");
                                cnt++;
                            }
                        }
                    } catch (ClassNotFoundException t) {
                        getLog().warn("Can't load class: " + className);
                    }
                }
                getLog().info("done: " + cnt + " components found");

                if (cnt > 0) {
                    File file = new File(output, COMPONENTS_FILE_NAME + ".properties");
                    try (FileOutputStream fos = new FileOutputStream(file)) {
                        components.store(fos, "UniToo components");
                    }
                }

            } catch (Exception t) {
                throw new MojoExecutionException("UniToo ", t);
            } finally {
                Thread.currentThread().setContextClassLoader(original);
            }
        }
    }

}
