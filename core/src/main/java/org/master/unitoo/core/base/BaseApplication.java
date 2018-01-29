/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.base;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.master.sqlonfly.core.SQLEngine;
import org.master.sqlonfly.impl.SqlDefaultDataParams;
import org.master.sqlonfly.impl.SqlDefaultDataRow;
import org.master.sqlonfly.impl.SqlDefaultDataTable;
import org.master.sqlonfly.impl.SqlDefaultDirectTable;
import org.master.sqlonfly.interfaces.IConnectionProvider;
import org.master.sqlonfly.interfaces.ISQLBatch;
import org.master.sqlonfly.interfaces.ISQLData;
import org.master.sqlonfly.interfaces.ISQLDataParams;
import org.master.sqlonfly.interfaces.ISQLDataRow;
import org.master.sqlonfly.interfaces.ISQLDataTable;
import org.master.sqlonfly.interfaces.ISQLDirectTable;
import org.master.unitoo.core.UniToo;
import org.master.unitoo.core.api.IApplication;
import org.master.unitoo.core.api.IApplicationDefaults;
import org.master.unitoo.core.api.IAutowired;
import org.master.unitoo.core.api.IBootInfo;
import org.master.unitoo.core.api.IBootListener;
import org.master.unitoo.core.api.IBusinessField;
import org.master.unitoo.core.api.IBusinessObject;
import org.master.unitoo.core.api.ICodedEnum;
import org.master.unitoo.core.api.IComponent;
import org.master.unitoo.core.api.IControllerMethod;
import org.master.unitoo.core.api.ICustomizedComponent;
import org.master.unitoo.core.api.IExternalValuesManager;
import org.master.unitoo.core.api.IInterfacedComponent;
import org.master.unitoo.core.api.components.IFormatter;
import org.master.unitoo.core.api.ILogger;
import org.master.unitoo.core.api.IRunnableComponent;
import org.master.unitoo.core.api.util.ISession;
import org.master.unitoo.core.api.IStoppableComponent;
import org.master.unitoo.core.api.components.ILoggerFactory;
import org.master.unitoo.core.api.annotation.Component;
import org.master.unitoo.core.api.annotation.Logger;
import org.master.unitoo.core.api.components.IBackgroundTask;
import org.master.unitoo.core.errors.FieldInitException;
import org.master.unitoo.core.errors.SettingParseException;
import org.master.unitoo.core.errors.TypeConvertExpection;
import org.master.unitoo.core.errors.XMLException;
import org.master.unitoo.core.server.BootLog;
import org.master.unitoo.core.server.LogCache;
import org.master.unitoo.core.server.MainLog;
import org.master.unitoo.core.server.SysOutLogger;
import org.master.unitoo.core.server.SystemFormatter;
import org.master.unitoo.core.types.ComponentContext;
import org.master.unitoo.core.types.ComponentType;
import org.master.unitoo.core.api.synthetic.DateTime;
import org.master.unitoo.core.types.RunnableState;
import org.master.unitoo.core.server.ServerConfig;
import org.master.unitoo.core.types.ThreadType;
import org.master.unitoo.core.types.Time;
import org.master.unitoo.core.utils.EnumUtils;
import org.master.unitoo.settings.SysSettings;
import org.w3c.dom.Document;
import org.master.unitoo.core.api.annotation.Attribute;
import org.master.unitoo.core.api.annotation.GlobalParam;
import org.master.unitoo.core.api.components.IConfigManager;
import org.master.unitoo.core.api.components.ISecurity;
import org.master.unitoo.core.api.components.I18nManager;
import org.master.unitoo.core.api.components.IGlossary;
import org.master.unitoo.core.api.components.IGlossaryManager;
import org.master.unitoo.core.api.components.ILanguage;
import org.master.unitoo.core.api.components.IStoredGlossary;
import org.master.unitoo.core.api.util.IUser;
import org.master.unitoo.core.errors.AccessDenied;
import org.master.unitoo.core.errors.AttributeGetException;
import org.master.unitoo.core.errors.ComponentLoadException;
import org.master.unitoo.core.errors.InvalidSession;
import org.master.unitoo.core.errors.MethodNotFound;
import org.master.unitoo.core.errors.NoSecurityException;
import org.master.unitoo.core.errors.SystemException;
import org.master.unitoo.core.errors.UnitooException;
import org.master.unitoo.core.impl.ApplicationDefaults;
import org.master.unitoo.core.impl.EmptyConfigManager;
import org.master.unitoo.core.impl.EmptyGlossaryManager;
import org.master.unitoo.core.impl.EmptyI18nManager;
import org.master.unitoo.core.impl.SystemErrorHandler;
import org.master.unitoo.core.server.AccessLog;
import org.master.unitoo.core.server.BootFormatter;
import org.master.unitoo.core.types.MethodType;
import org.master.unitoo.core.types.RequestMethod;
import org.master.unitoo.core.server.Setting;
import org.master.unitoo.core.types.BinaryFormat;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.master.unitoo.core.api.IProcess;
import org.master.unitoo.core.api.IProcessContext;
import org.master.unitoo.core.api.IProcessSnapshot;
import org.master.unitoo.core.api.components.mappers.HTML_CONTENT;
import org.master.unitoo.core.api.components.mappers.JSON_CONTENT;
import org.master.unitoo.core.api.components.mappers.XML_CONTENT;
import org.master.unitoo.core.types.Decision;
import org.master.unitoo.core.types.ThreadState;

/**
 *
 * @author Andrey
 */
public abstract class BaseApplication implements IApplication {

    private ISecurity security;

    private String appName;
    private String appVersion;
    private ApplicationDefaults defaults;
    private IFormatter bootFormatter;
    private ILogger log = new SysOutLogger();
    private ILogger accessLog = new SysOutLogger();
    private SysSettings sysPack;
    private ServletContext webContext;
    private ServerConfig settings;
    private ThreadPoolExecutor executor;
    private Timer timer;
    private SQLEngine sqlEngine;
    private final ConcurrentMap<String, IComponent> componentIndex = new ConcurrentHashMap<>();
    private final EnumMap<ComponentType, ConcurrentMap<String, IComponent>> components = new EnumMap<>(ComponentType.class);
    private final WeakHashMap<Thread, ThreadInfo> threads = new WeakHashMap();
    private final ConcurrentMap<String, IControllerMethod> mappings = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, ILanguage> languages = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, BusinessFieldList> businessFields = new ConcurrentHashMap<>();

    protected abstract Class<? extends IGlossaryManager> glossaryManager();

    protected abstract Class<? extends IConfigManager> configManager();

    protected abstract Class<? extends I18nManager> i18nManager();

    protected abstract void prepareDefaults(ApplicationDefaults defaults);

    @Override
    public IApplicationDefaults defaults() {
        return defaults;
    }

    @Override
    public ILanguage language(String code) {
        return code == null ? null : languages.get(code);
    }

    @Override
    public <T> T component(Class<T> clazz) {
        return (T) componentIndex.get(clazz.getName());
    }

    @Override
    public IComponent component(ComponentType type, String name) {
        IComponent item = componentIndex.get(name);
        return item == null || item.type() != type ? null : item;
    }

    private void registerComponent(Class clazz, GlobalParams gp) {
        IComponent component;
        try {
            if (IComponent.class.isAssignableFrom(clazz)) {

                ComponentType type = ComponentType.valueOf(clazz, Modifier.isAbstract(clazz.getModifiers()));
                if (type != null) {

                    if (type.proxy() != null) {
                        component = (IComponent) type.proxy().newInstance();
                        componentIndex.put(clazz.getName(), (IComponent) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, (InvocationHandler) component));
                    } else {
                        component = (IComponent) clazz.newInstance();
                        componentIndex.put(component.getClass().getName(), component);
                    }

                    components.get(type).put(component.getClass().getName(), component);

                    if (component instanceof IInterfacedComponent) {
                        for (Class iface : ((IInterfacedComponent) component).interfaces()) {
                            componentIndex.put(iface.getName(), component);
                        }
                    }

                    if (component instanceof ILanguage) {
                        ILanguage lang = (ILanguage) component;
                        languages.put(lang.code(), lang);
                    }

                    ComponentContext ctxComponent = createComponentContext(clazz);
                    component.init(ctxComponent);
                    scanComponentBefore(component, component.getClass(), ctxComponent, true, gp);
                } else {
                    log().warning("Unknown component type, or incorrect abstract modifier for: " + clazz.getName());
                }
            }
        } catch (IllegalAccessException | InstantiationException e) {
            log().error("Failed to create component:" + clazz.getName(), e);
        }
    }

    @SuppressWarnings({"TooBroadCatch", "UseSpecificCatch"})
    private void loadComponents(ServletContext context) throws NamingException {
        bootFormatter = new BootFormatter();

        Component appComponent = (Component) getClass().getAnnotation(Component.class);
        if (appComponent != null) {
            appName = appComponent.value();
            String version = appComponent.version();
            if (version == null || version.isEmpty()) {
                version = getPackVersion(getClass(), context);
            }
            if (version == null || version.isEmpty()) {
                version = "1.0.0";
            }
            appVersion = version;
        } else {
            appName = "unitoo";
            appVersion = "0.0.1";
        }

        GlobalParams gp = new GlobalParams(this);
        InputStream in = context.getResourceAsStream("/WEB-INF/unitoo/components.properties");
        if (in != null) {
            Properties props = new Properties();
            try {
                props.load(in);
                for (String item : props.stringPropertyNames()) {
                    try {
                        Class clazz = getClass().getClassLoader().loadClass(item);
                        registerComponent(clazz, gp);
                    } catch (Throwable e) {
                        log().error("Failed to load class: " + item, e);
                    }
                }
                in.close();
            } catch (IOException ex) {
                log().error(ex);
            }
        }

        defaults = new ApplicationDefaults(this);
        defaults.setFormatter(SystemFormatter.class);
        defaults.setErrorHandler(SystemErrorHandler.class);
        defaults.addContent(HTML_CONTENT.class);
        defaults.addContent(JSON_CONTENT.class);
        defaults.addContent(XML_CONTENT.class);
        sysPack = component(SysSettings.class);

    }

    private ComponentContext createComponentContext(Class clazz) {
        Component annComponent = (Component) clazz.getAnnotation(Component.class);
        Logger annLogger = (Logger) clazz.getAnnotation(Logger.class);

        String descr;
        String url = null;
        String version = null;
        String info;
        Class<? extends ILoggerFactory> logger = null;

        if (annComponent != null) {
            url = annComponent.url();
            descr = annComponent.value();
            version = annComponent.version();
            info = annComponent.info();
        } else {
            descr = clazz.getSimpleName();
            info = clazz.getName();
        }

        if (annLogger != null) {
            logger = annLogger.value() == BaseLogger.class ? null : annLogger.value();
        }

        ComponentContext ctxComponent = new ComponentContext(
                descr,
                version,
                info,
                url,
                this,
                settings,
                logger,
                new BootInfo(),
                clazz
        );

        return ctxComponent;
    }

    public String printVersion(String v) {
        return v == null || v.trim().isEmpty() ? "" : " v." + v;
    }

    @Override
    public void execute(IBackgroundTask task, long delay) {
        if (delay <= 0) {
            executor.execute(task);
        } else {
            timer.executeAfter(task, delay);
        }
    }

    private void startThreads() {
        executor = new ThreadPoolExecutor(
                sysPack.threadsMin.val(),
                sysPack.threadsMax.val(),
                sysPack.threadsKeep.val(),
                TimeUnit.SECONDS,
                new SynchronousQueue(),
                new ThreadFactory() {
            private final AtomicInteger idgen = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                int id = idgen.incrementAndGet();
                Thread th = new Thread(Thread.currentThread().getThreadGroup(), r, "unitoo-bg-" + id);
                ThreadInfo ti = new ThreadInfo(ThreadType.Background, BaseApplication.this);
                threads.put(th, ti);
                return th;
            }
        },
                new RejectedExecutionHandler() {

            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                if (r instanceof IRunnableComponent) {
                    ((IRunnableComponent) r).reject();
                }
            }
        });

        timer = new Timer(executor);
        Thread timerThread = new Thread(Thread.currentThread().getThreadGroup(), timer, "unitoo-timer");
        ThreadInfo ti = new ThreadInfo(ThreadType.System, this);
        threads.put(timerThread, ti);
        timerThread.start();
    }

    @Override
    public IProcess process() {
        return process(Thread.currentThread());
    }

    @Override
    public IProcess process(Thread thread) {
        ThreadInfo ti = threads.get(thread);
        if (ti == null) {
            ti = new ThreadInfo(thread.getName().contains("http") ? ThreadType.HTTP : ThreadType.System, this);
            threads.put(thread, ti);
        }
        return ti;
    }

    @Override
    @SuppressWarnings({"TooBroadCatch", "UseSpecificCatch"})
    public void contextInitialized(ServletContextEvent sce) {
        try {
            log = new LogCache(new SysOutLogger());

            long startTime = System.currentTimeMillis();
            ServletContext context = sce.getServletContext();

            for (ComponentType type : ComponentType.values()) {
                components.put(type, new ConcurrentHashMap<String, IComponent>());
            }
            settings = new ServerConfig(this);

            loadComponents(context);
            prepareDefaults(defaults);
            initComponents(context);

            long ms = System.currentTimeMillis() - startTime;
            log().info("");
            log().info("");
            log().info("");
            log().info("Application '" + appName() + "'" + printVersion(version()) + " started in " + ms + "ms");

        } catch (Throwable t) {
            log().error(t);
            ((LogCache) log).flush();
        }
    }

    private String getPackVersion(Class clazz, ServletContext context) {
        String v = clazz.getPackage().getImplementationVersion();
        if (v == null) {
            Properties prop = new Properties();
            try {
                InputStream in = context.getResourceAsStream("/META-INF/MANIFEST.MF");
                if (in != null) {
                    prop.load(in);
                    v = prop.getProperty("Implementation-Version");
                } else {
                    v = null;
                }
            } catch (IOException e) {
                v = null;
            }
        }
        return v;
    }

    private void initDefaultServlet() {
        webContext
                .addServlet("unitoo", new MainServlet(this))
                .addMapping("/");
    }

    @SuppressWarnings({"TooBroadCatch", "UseSpecificCatch"})
    private void initComponents(ServletContext context) throws UnitooException {
        this.webContext = context;
        try {
            settings.reinit(sysPack.instanceId, InetAddress.getLocalHost().getHostName());
        } catch (UnknownHostException e) {
            settings.reinit(sysPack.instanceId, "localhost");
        }

        sqlEngine = new SQLEngine(new IConnectionProvider() {
            @Override
            public String getDialect(String aliase) {
                return "Default";
            }

            @Override
            public Connection takeConnection(String aliase) throws SQLException {
                try {
                    Context initContext = new InitialContext();
                    Context envContext = (Context) initContext.lookup("java:/comp/env");
                    DataSource ds = (DataSource) envContext.lookup(appName + "." + aliase);
                    if (ds == null) {
                        throw new NameNotFoundException("Database aliase '" + appName + "." + aliase + "' not found");
                    }
                    return ds.getConnection();
                } catch (Throwable t) {
                    throw new SQLException(t);
                }
            }

            @Override
            public void releaseConnection(Connection connection) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace(System.err);
                }
            }

            @Override
            public <T> T convertValue(Object value, Class<T> convertTo) throws Exception {
                return convert(value, convertTo);
            }

            @Override
            public Object prepareValue(Object value) throws Exception {
                return value;
            }

            @Override
            public void beforeBatchExecute(ISQLBatch batch, String methodName) {
            }

            @Override
            public void afterBatchExecute(ISQLBatch batch, String methodName) throws InterruptedException {
                boolean haswarn = false;
                StringBuilder buf = new StringBuilder();
                buf
                        .append(">>> ")
                        .append(methodName)
                        .append(" on ")
                        .append(batch.getConnectionName())
                        .append("\n");

                SQLWarning warning = batch.getWarnings();
                while (warning != null) {
                    haswarn = true;
                    buf.append("[").append(warning.getErrorCode()).append("] ").append(warning.getMessage()).append("\n");
                    buf.append(batch.getConnectionName()).append("\n");
                    warning = warning.getNextWarning();
                }
                buf.append("\n");

                if (haswarn) {
                    log().warning(buf.toString());
                }
            }

            @Override
            @SuppressWarnings("UseSpecificCatch")
            public ISQLDataTable createTable(Class<? extends ISQLDataTable> tableClass) {
                if (tableClass != null && tableClass.equals(ISQLDirectTable.class)) {
                    return new SqlDefaultDirectTable();
                } else if (tableClass == null || tableClass.equals(ISQLDataTable.class)) {
                    return new SqlDefaultDataTable();
                } else {
                    try {
                        Constructor<? extends ISQLDataTable> constructor = tableClass.getConstructor();
                        constructor.setAccessible(true);
                        return constructor.newInstance();
                    } catch (Throwable e) {
                        log().error(e);
                        return null;
                    }
                }
            }

            @Override
            @SuppressWarnings("TooBroadCatch")
            public ISQLDataRow createRow(Class<? extends ISQLDataRow> rowClass) {
                if (rowClass == null || rowClass.equals(ISQLDataRow.class)) {
                    return new SqlDefaultDataRow();
                } else {
                    try {
                        return rowClass.newInstance();
                    } catch (InstantiationException | IllegalAccessException e) {
                        log().error(e);
                        return null;
                    }
                }
            }

            @Override
            public Class resolve(ISQLBatch.DataTypes type) {
                return type.getJavaClass();
            }

            @Override
            @SuppressWarnings("UseSpecificCatch")
            public ISQLDataParams createParams(Class<? extends ISQLDataParams> paramsClass) {
                if (paramsClass == null || paramsClass.equals(ISQLDataParams.class)) {
                    return new SqlDefaultDataParams();
                } else {
                    try {
                        return paramsClass.newInstance();
                    } catch (Throwable e) {
                        log().error(e);
                        return null;
                    }
                }
            }

            @Override
            public void debug(String msg) {
                // System.out.println(msg); //FOR DEBUGGING
            }

            @Override
            public void debugQuery(String batchName, String string) {
                log().debug(batchName + "\n" + string + "\n------------------------------------------------------------------\n\n");
            }

            @Override
            public <T> T createObject(Class<T> clazz, ISQLData data) throws Exception {
                T object = clazz.newInstance();
                if (object instanceof IBusinessObject) {
                    IBusinessObject bo = (IBusinessObject) object;
                    for (IBusinessField field : businessFields(bo.getClass())) {
                        if (data.exists(field.name())) {
                            Object value = convert(data.getObject(field.name()), field.type());
                            field.set(value, bo);
                        }
                    }
                }
                return object;
            }
        });

        Class<? extends IConfigManager> cfgManClass = configManager();
        cfgManClass = cfgManClass == null ? EmptyConfigManager.class : cfgManClass;
        log().info("");
        log().info("\tloading config manager: " + cfgManClass.getName());
        IConfigManager cfgManager = component(cfgManClass);
        if (cfgManager != null) {
            log().info("\t" + cfgManager.name() + printVersion(cfgManager.version()));
            log().info("\t\tsetting up...");
            scanComponentAfter(cfgManager, cfgManager.getClass());
        } else {
            log().info("\tno component found? terminating...");
            throw new ComponentLoadException(cfgManClass.getName());
        }

        initSettings(context, cfgManager);
        startThreads();

        ILogger mainLog = null;
        ILogger bootLog = null;
        ArrayList<ICustomizedComponent> inits = new ArrayList<>();
        Iterable<IComponent> items = components(IComponent.class);
        for (IComponent component : items) {

            if (component == cfgManager) {
                continue;
            }

            log().info("");
            log().info("\t" + component.name() + printVersion(component.version()));
            log().info("\t\tsetting up...");
            scanComponentAfter(component, component.getClass());

            if (component.boot().success()) {
                if (component instanceof ICustomizedComponent) {
                    if (component.type().getBootOrder() == UniToo.BOOT_PRIORITY_INIT) {
                        ((ICustomizedComponent) component).prepare();

                        if (component instanceof IRunnableComponent) {
                            IRunnableComponent rc = (IRunnableComponent) component;

                            if (rc.getClass() == BootLog.class) {
                                BootLog bl = (BootLog) rc;
                                File file = new File(bl.path(), bl.file());
                                file.delete();
                            }

                            log().info("\t\tstarting...");
                            rc.start();
                            log().info("\t\tactivated");

                            if (rc.getClass() == MainLog.class) {
                                mainLog = ((MainLog) rc).log("log.core");
                            } else if (rc.getClass() == AccessLog.class) {
                                accessLog = ((AccessLog) rc).log("log.access");
                            } else if (rc.getClass() == BootLog.class) {
                                bootLog = ((BootLog) rc).log("log.boot");
                            }
                        }
                    } else {
                        inits.add((ICustomizedComponent) component);
                    }
                }
            } else {
                log().info("\t\tfailed, see errors:");
                for (Throwable t : component.boot().errors()) {
                    log().error(t);
                }
            }
        }

        Class<? extends I18nManager> i18nClass = i18nManager();
        i18nClass = i18nClass == null ? EmptyI18nManager.class : i18nClass;
        I18nManager i18nManager = component(i18nClass);
        log().info("");
        log().info("\tloading languages, using: " + i18nClass.getName());
        for (ILanguage language : languages.values()) {
            log().info("\t\t" + language.code());
            language.init(i18nManager);
        }

        Class<? extends IGlossaryManager> glossaryManagerClass = glossaryManager();
        glossaryManagerClass = glossaryManagerClass == null ? EmptyGlossaryManager.class : glossaryManagerClass;
        IGlossaryManager glossaryManager = component(glossaryManagerClass);
        log().info("");
        log().info("\tinitializing stored glossaries, using: " + glossaryManagerClass.getName() + "...");
        int gl = 0;
        Iterable<IGlossary> glossaries = components(IGlossary.class);
        for (IGlossary glossary : glossaries) {
            if (glossary instanceof IStoredGlossary) {
                ((IStoredGlossary) glossary).init(glossaryManager);
                gl++;
            }
        }
        log().info("\tStored glossaries initialized: " + gl);

        if (bootLog != null) {
            bootLog.info("UniToo application loaded: " + appName() + " " + printVersion(version()));
            bootLog.info("Unitoo core version: " + UniToo.class.getPackage().getImplementationVersion());
            bootLog.info("Core initialization complete, activating components:");
            ((LogCache) log).setTarget(bootLog);
            ((LogCache) log).flush();
            log = bootLog;
        }

        Collections.sort(inits, new Comparator<ICustomizedComponent>() {
            @Override
            public int compare(ICustomizedComponent o1, ICustomizedComponent o2) {
                if (o1.type().getBootOrder() > o2.type().getBootOrder()) {
                    return 1;
                } else if (o1.type().getBootOrder() < o2.type().getBootOrder()) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });

        for (ICustomizedComponent component : inits) {
            if (component == cfgManager || component.type().getBootOrder() == UniToo.BOOT_PRIORITY_INIT) {
                continue;
            }

            log().info("");
            log().info("\t" + component.name() + printVersion(component.version()));
            try {
                log().info("\t\tsetting up...");
                scanComponentAfter(component, component.getClass());
                if (component.boot().success()) {
                    component.prepare();
                    if (component instanceof IRunnableComponent) {
                        log().info("\t\tstarting...");
                        ((IRunnableComponent) component).start();
                        log().info("\t\tactivated");
                    }
                } else {
                    log().info("\t\tfailed, see errors:");
                    for (Throwable t : component.boot().errors()) {
                        log().error(t);
                    }
                }
            } catch (Throwable t) {
                log().error("activation failed", t);
            }
        }
        log().info("");

        settings.bootComplete();
        cfgManager.bootComplete();
        i18nManager.bootComplete();
        glossaryManager.bootComplete();

        for (IComponent component : items) {
            if (component instanceof IBootListener) {
                if (!(component instanceof IExternalValuesManager)) {
                    ((IBootListener) component).bootComplete();
                }
            }
        }

        if (mainLog != null) {
            log = mainLog;
        }

        initDefaultServlet();
    }

    @Override
    public Iterable<ILanguage> languages() {
        return languages.values();
    }

    @Override
    public void register(IControllerMethod method) {
        mappings.put(method.mapping(), method);
    }

    private void preloadSettings(InputStream in, Properties props) {
        if (in != null) {
            try {
                props.load(in);

                for (String key : props.stringPropertyNames()) {
                    Setting setting = settings.get(key);
                    if (setting != null) {
                        try {
                            settings.reinit(setting, parse(bootFormatter, props.getProperty(key), setting.type()));
                        } catch (TypeConvertExpection e) {
                            SettingParseException error = new SettingParseException("package", key, e);
                            if (setting.component() != null) {
                                setting.component().boot().addBootError(error);
                            } else {
                                log().error(e);
                            }
                        }
                    }
                }
                in.close();
            } catch (IOException ex) {
                log().error(ex);
            }
        }
    }

    private void initSettings(ServletContext context, IConfigManager manager) throws UnitooException {
        InputStream in = context.getResourceAsStream("/WEB-INF/unitoo/server.properties");
        preloadSettings(in, new Properties());
        settings.init(manager, defaults.formatter().settings());
    }

    @Override
    @SuppressWarnings("SleepWhileInLoop")
    public void contextDestroyed(ServletContextEvent sce) {
        log().info("Shutdown signal detected!");
        log().info("Stopping background events generation...");
        if (timer != null) {
            timer.stop();
        }

        log().info("Stopping components:");
        ArrayList<IStoppableComponent> runs = new ArrayList<>();
        Iterable<IComponent> items = components(IComponent.class);
        for (IComponent component : items) {
            if (component instanceof IStoppableComponent) {
                log().info("");
                log().info("\t" + component.name());
                try {
                    ((IStoppableComponent) component).stop();
                    runs.add((IStoppableComponent) component);
                } catch (Throwable t) {
                    log().error("\t\tstopping failed", t);
                }
            }
        }

        log().info("Stopping background events processing...");
        if (executor != null) {
            executor.shutdown();
        }

        while (runs.size() > 0) {
            Iterator<IStoppableComponent> it = runs.iterator();
            int time = 0;
            while (it.hasNext()) {
                time++;
                IStoppableComponent run = it.next();
                if (run.state() == RunnableState.Stopped) {
                    it.remove();
                } else {
                    switch (time) {
                        case 50:
                            log().info("Runnable " + run.name() + " waiting for stop...");
                            break;
                        case 100:
                            log().warning("Runnable " + run.name() + " killing...");
                            run.kill();
                            break;
                        case 600:
                            log().warning("Runnable " + run.name() + " failed to stop");
                            break;
                        default:
                            break;
                    }
                }
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                log().warning("Main application thread has been killed");
            }
        }

        log().info("Destroying components:");
        items = components(IComponent.class);
        for (IComponent component : items) {
            log().info("");
            log().info("\t" + component.name());
            try {
                component.destroy();
            } catch (Throwable t) {
                log().error("\t\tcomponent not destroyed properly", t);
            }
        }

        log().info("System shutdowned");
    }

    @Override
    public <T extends IComponent> Iterable<T> components(Class<T> clazz) {
        if (IComponent.class == clazz) {
            ArrayList<IComponent> items = new ArrayList<>();
            for (ComponentType type : ComponentType.values()) {
                items.addAll(components.get(type).values());
            }
            return (Iterable<T>) items;
        } else {
            ComponentType type = ComponentType.valueOf(clazz);
            if (type == null) {
                return null;
            } else {
                return (Iterable<T>) components.get(type).values();
            }
        }
    }

    @Override
    public String serverId() {
        return sysPack.instanceId.val();
    }

    @Override
    public String appName() {
        return appName;
    }

    @Override
    public String version() {
        return appVersion;
    }

    @Override
    public String homeFolder() {
        return sysPack.home.val();
    }

    public byte[] strToByte(String value, BinaryFormat fmt) {
        switch (fmt) {
            case BASE64:
                return new byte[0];
            case HEX:
                return new byte[0];
            default:
                return new byte[0];
        }
    }

    private String byteToStr(InputStream stream, BinaryFormat fmt) {
        switch (fmt) {
            case BASE64:
                return "Base64";
            case HEX:
                return "HEX";
            default:
                return "";
        }
    }

    @Override
    public String format(Object value) {
        return format(defaults.formatter(), value);
    }

    @Override
    public String format(IFormatter formatter, Object value) {

        if (value == null) {
            return null;
        } else if (value instanceof String) {
            if (process().context().escape()) {
                value = org.apache.commons.text.StringEscapeUtils.escapeHtml4((String) value);
            }
            if (process().context().trim()) {
                value = ((String) value).trim();
            }
            return (String) value;

        } else if (value instanceof InputStream) {
            return byteToStr((InputStream) value, formatter.binary());

        } else if (value instanceof byte[]) {
            return byteToStr(new ByteArrayInputStream((byte[]) value), formatter.binary());

        } else if (value.getClass().isArray()) {
            StringBuilder buf = new StringBuilder();
            for (Object item : (Object[]) value) {
                String v = format(formatter, item);
                buf.append(v == null ? "" : v).append(formatter.list());
            }
            return buf.toString();
        } else if (value instanceof Document) {
            try {
                Transformer transformer = formatter.xml().transformer();
                StringWriter writer = new StringWriter();
                StreamResult result = new StreamResult(writer);
                transformer.transform(new DOMSource((Document) value), result);
                return writer.toString();
            } catch (TransformerException | XMLException e) {
                log().error(e);
                return null;
            }
        } else if (value instanceof Time) {
            return formatter.time().format((Date) value);

        } else if (value instanceof DateTime) {
            return formatter.datetime().format((Date) value);

        } else if (value instanceof Date) {
            return formatter.date().format((Date) value);

        } else if (value instanceof ICodedEnum) {
            return format(formatter, ((ICodedEnum) value).code());

        } else if (value instanceof Enum) {
            return ((Enum) value).name();

        } else if (value instanceof Number) {
            if (value instanceof Long || value instanceof Integer || value instanceof Byte || value instanceof Short) {
                return formatter.integer().format(value);
            } else {
                return formatter.decimal().format(value);
            }
        } else if (long.class.isAssignableFrom(value.getClass())) {
            return formatter.integer().format(Long.valueOf((long) value));

        } else if (int.class.isAssignableFrom(value.getClass())) {
            return formatter.integer().format(Integer.valueOf((int) value));

        } else if (byte.class.isAssignableFrom(value.getClass())) {
            return formatter.integer().format(Byte.valueOf((byte) value));

        } else if (short.class.isAssignableFrom(value.getClass())) {
            return formatter.integer().format(Short.valueOf((short) value));

        } else {
            return value.toString();
        }
    }

    @Override
    public <T> T parse(String value, Class<T> clazz) throws TypeConvertExpection {
        return parse(defaults.formatter(), value, clazz);
    }

    @Override
    @SuppressWarnings("UnnecessaryBoxing")
    public <T> T parse(IFormatter formatter, String value, Class<T> clazz) throws TypeConvertExpection {
        try {
            if (value == null || value.isEmpty()) {
                return null;

            } else if (InputStream.class.isAssignableFrom(clazz)) {
                return (T) new ByteArrayInputStream(strToByte(value, formatter.binary()));

            } else if (byte[].class.isAssignableFrom(clazz)) {
                return (T) strToByte(value, formatter.binary());

            } else if (clazz.isArray()) {
                String[] items = value.split("\\" + formatter.list());
                int len = items.length;
                if (items[len - 1].trim().isEmpty()) {
                    len = len - 1;
                }

                Class cmp = clazz.getComponentType();
                Object[] array = (Object[]) Array.newInstance(cmp, len);
                for (int i = 0; i < len; i++) {
                    array[i] = parse(formatter, items[i], cmp);
                }

                return (T) array;

            } else if (String.class.isAssignableFrom(clazz)) {
                if (process().context().escape()) {
                    value = org.apache.commons.text.StringEscapeUtils.escapeHtml4(value);
                }
                if (process().context().trim()) {
                    value = value.trim();
                }
                return (T) value;

            } else if (Character.class.isAssignableFrom(clazz)) {
                return value.isEmpty() ? null : (T) Character.valueOf(value.charAt(0));

            } else if (Time.class.isAssignableFrom(clazz)) {
                return (T) new Time(formatter.time().parse(value).getTime());

            } else if (DateTime.class.isAssignableFrom(clazz)) {
                return (T) new DateTime(formatter.datetime().parse(value).getTime());

            } else if (Date.class.isAssignableFrom(clazz)) {
                return (T) formatter.date().parse(value);

            } else if (Boolean.class.isAssignableFrom(clazz)) {
                if ("true".equals(value) || "Y".equals(value) || "1".equals(value)) {
                    return (T) Boolean.TRUE;
                } else {
                    return (T) Boolean.FALSE;
                }

            } else if (Number.class.isAssignableFrom(clazz)) {
                if (Integer.class.isAssignableFrom(clazz)) {
                    return (T) Integer.valueOf(formatter.integer().parse(value).intValue());
                } else if (Long.class.isAssignableFrom(clazz)) {
                    return (T) Long.valueOf(formatter.integer().parse(value).longValue());
                } else if (Byte.class.isAssignableFrom(clazz)) {
                    return (T) Byte.valueOf(formatter.integer().parse(value).byteValue());
                } else if (Short.class.isAssignableFrom(clazz)) {
                    return (T) Short.valueOf(formatter.integer().parse(value).shortValue());
                } else if (BigDecimal.class.isAssignableFrom(clazz)) {
                    return (T) BigDecimal.valueOf(formatter.decimal().parse(value).doubleValue());
                } else {
                    return (T) Double.valueOf(formatter.decimal().parse(value).doubleValue());
                }
            } else if (Document.class.isAssignableFrom(clazz)) {
                return (T) formatter.xml().builder().parse(new InputSource(new StringReader(value)));

            } else if (ICodedEnum.class.isAssignableFrom(clazz)) {
                T v = (T) EnumUtils.Get(
                        parse(formatter, value, EnumUtils.Type((Class<? extends ICodedEnum>) clazz)),
                        (Class<? extends ICodedEnum>) clazz);
                if (v == null) {
                    throw new ParseException("Invalid coded enum '" + clazz.getName() + "' item: " + value, 0);
                }
                return v;

            } else if (clazz.isEnum()) {
                T v = (T) EnumUtils.Get(value, (Class<? extends Enum>) clazz);
                if (v == null) {
                    throw new ParseException("Invalid enum '" + clazz.getName() + "' item: " + value, 0);
                }
                return v;

            } else {
                return (T) value;
            }
        } catch (ParseException | SAXException | IOException e) {
            throw new TypeConvertExpection(value, clazz, e);
        }
    }

    @Override
    public <T> T convert(Object value, Class<T> clazz) throws TypeConvertExpection {
        if (value == null) {
            return null;
        } else if (value.getClass().isAssignableFrom(clazz)) {
            return (T) value;
        } else if (String.class.isAssignableFrom(clazz)) {
            return (T) format(value);
        } else if (value instanceof String) {
            return (T) parse((String) value, clazz);
        } else if (Time.class.isAssignableFrom(clazz)) {
            if (value instanceof Number) {
                return (T) new Time(((Number) value).longValue());
            } else if (value instanceof Date) {
                return (T) new Time(((Date) value).getTime());
            } else if (value instanceof java.sql.Timestamp) {
                return (T) new Time(((java.sql.Timestamp) value).getTime());
            }
        } else if (DateTime.class.isAssignableFrom(clazz)) {
            if (value instanceof Number) {
                return (T) new DateTime(((Number) value).longValue());
            } else if (value instanceof Date) {
                return (T) new DateTime(((Date) value).getTime());
            } else if (value instanceof java.sql.Timestamp) {
                return (T) new DateTime(((java.sql.Timestamp) value).getTime());
            }
        } else if (Date.class.isAssignableFrom(clazz)) {
            if (value instanceof Number) {
                return (T) new Date(((Number) value).longValue());
            } else if (value instanceof java.sql.Date) {
                return (T) new Date(((java.sql.Date) value).getTime());
            } else if (value instanceof DateTime) {
                return (T) new Date(((DateTime) value).getTime());
            } else if (value instanceof java.sql.Timestamp) {
                return (T) new Date(((java.sql.Timestamp) value).getTime());
            }
        } else if (Integer.class.isAssignableFrom(clazz)) {
            Integer v = null;
            if (value instanceof Number) {
                v = ((Number) value).intValue();
            } else if (value instanceof Boolean) {
                v = (Boolean) value ? 1 : 0;
            } else {
                throw new TypeConvertExpection(value, clazz);
            }
            return (T) v;
        } else if (Long.class.isAssignableFrom(clazz)) {
            Long v = null;
            if (value instanceof Number) {
                v = ((Number) value).longValue();
            } else if (value instanceof Boolean) {
                v = (Boolean) value ? 1L : 0L;
            } else if (value instanceof Date) {
                v = ((Date) value).getTime();
            } else {
                throw new TypeConvertExpection(value, clazz);
            }
            return (T) v;
        } else if (Double.class.isAssignableFrom(clazz)) {
            Double v = null;
            if (value instanceof Number) {
                v = ((Number) value).doubleValue();
            } else {
                throw new TypeConvertExpection(value, clazz);
            }
            return (T) v;
        } else if (Boolean.class.isAssignableFrom(clazz)) {
            Boolean v = null;
            if (value instanceof Number) {
                v = ((Number) value).longValue() > 0;
            } else {
                throw new TypeConvertExpection(value, clazz);
            }
            return (T) v;
        }
        throw new TypeConvertExpection(value, clazz);
    }

    @Override
    public ILogger log() {
        return log;
    }

    @Override
    public ILogger log(Class<? extends ILoggerFactory> factory, Class component) {
        ILoggerFactory lf = factory == null ? null : component(factory);
        return lf == null ? log : lf.log(component);
    }

    @Override
    public ILogger log(Class<? extends ILoggerFactory> factory, String component) {
        ILoggerFactory lf = factory == null ? null : component(factory);
        return lf == null ? log : lf.log(component);
    }

    @SuppressWarnings({"TooBroadCatch", "UseSpecificCatch"})
    private void scanComponentBefore(IComponent component, Class clazz, ComponentContext ctx, boolean top, GlobalParams gp) {
        if (clazz.getSuperclass() != null) {
            scanComponentBefore(component, clazz.getSuperclass(), ctx, false, gp);
        }

        for (Field field : clazz.getDeclaredFields()) {
            try {
                field.setAccessible(true);
                GlobalParam gpAnn = field.getAnnotation(GlobalParam.class);
                if (gpAnn != null) {
                    field.set(component, gp.get(gpAnn.value()));

                } else if (Setting.class.isAssignableFrom(field.getType()) && (top || !BaseSettings.class.isAssignableFrom(clazz))) {
                    Setting setting = (Setting) field.getType().newInstance();
                    String value = null;
                    String fldName;
                    Attribute annotation = (Attribute) field.getAnnotation(Attribute.class);
                    if (annotation != null) {
                        value = annotation.value();
                        fldName = annotation.name() != null && !annotation.name().isEmpty()
                                ? component.name() + "." + annotation.name()
                                : component.name() + "." + field.getName();
                    } else {
                        fldName = component.name() + "." + field.getName();
                    }

                    try {
                        Class dataClass = String.class;
                        Type type = field.getGenericType();
                        if (type instanceof ParameterizedType) {
                            dataClass = (Class) ((ParameterizedType) type).getActualTypeArguments()[0];
                        }

                        Object rawval = null;
                        if (value != null) {
                            switch (value) {
                                case Attribute.CLASS_FULL:
                                    rawval = component.getClass().getName();
                                    break;
                                case Attribute.CLASS_SIMPLE:
                                    rawval = component.getClass().getSimpleName();
                                    break;
                                case Attribute.NOW:
                                    if (Date.class == dataClass) {
                                        rawval = new Date();
                                    } else if (DateTime.class == dataClass) {
                                        rawval = new DateTime();
                                    } else if (Time.class == dataClass) {
                                        rawval = new Time();
                                    }
                                    break;
                            }
                        }

                        settings.register(
                                setting,
                                fldName,
                                rawval == null ? parse(bootFormatter, value, dataClass) : rawval,
                                dataClass,
                                component);
                        field.set(component, setting);
                    } catch (Throwable t) {
                        SettingParseException error = new SettingParseException("source", fldName, t);
                        component.boot().addBootError(error);
                        log().error(error);
                    }
                }
            } catch (IllegalAccessException | InstantiationException e) {
                FieldInitException error = new FieldInitException(field.getName(), clazz, e);
                component.boot().addBootError(error);
                log().error(error);
            }
        }
    }

    @Override
    public <T extends ISQLBatch> T cast(Class<T> iface) throws SystemException {
        try {
            String implName = sqlEngine.getImplementation(iface);
            Class<T> implClass = (Class<T>) iface.getClassLoader().loadClass(implName);
            return sqlEngine.create(implClass);
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            throw new SystemException(e);
        }
    }

    public void scanComponentAfter(IComponent component, Class clazz) {
        if (clazz.getSuperclass() != null) {
            scanComponentAfter(component, clazz.getSuperclass());
        }

        for (Field field : clazz.getDeclaredFields()) {
            try {
                field.setAccessible(true);
                if (ISQLBatch.class.isAssignableFrom(field.getType())) {
                    field.set(component, cast((Class<? extends ISQLBatch>) field.getType()));
                } else if (IAutowired.class.isAssignableFrom(field.getType())) {
                    if (IInterfacedComponent.class.isAssignableFrom(field.getType())) {
                        field.set(component, ((IInterfacedComponent) component(field.getType())).wrapper(field.getType()));
                    } else {
                        field.set(component, component(field.getType()));
                    }
                }
            } catch (IllegalAccessException | IllegalArgumentException | SystemException e) {
                FieldInitException error = new FieldInitException(field.getName(), clazz, e);
                component.boot().addBootError(error);
                log().error(error);
            }

        }
    }

    @Override
    public void register(ISecurity security) {
        this.security = security;
    }

    @Override
    public void beforeRequest(IControllerMethod method, HttpSession session) throws AccessDenied, InvalidSession, NoSecurityException {
        if (security == null) {
            throw new NoSecurityException();
        }

        ISession appSession = (ISession) session.getAttribute("unitoo.session");
        if (appSession == null) {
            appSession = security.newSession();
            session.setAttribute("unitoo.session", appSession);
        }
        security.check(method, appSession);
        ((ThreadInfo) process()).session(appSession);
    }

    @Override
    public void afterRequest(IControllerMethod method) throws AccessDenied, InvalidSession {
        ((ThreadInfo) process()).session(null);
    }

    @Override
    public Iterable<IBusinessField> businessFields(Class<? extends IBusinessObject> type) {
        BusinessFieldList list = businessFields.get(type.getName());
        if (list == null) {
            list = new BusinessFieldList(this);
            list.scan(type);
            businessFields.put(type.getName(), list);
        }
        return list;
    }

    private static class BusinessFieldList extends ArrayList<IBusinessField> {

        private final IApplication app;

        public BusinessFieldList(IApplication app) {
            this.app = app;
        }

        public void scan(Class clazz) {
            if (clazz.getSuperclass() != null) {
                scan(clazz.getSuperclass());
            }

            for (Field field : clazz.getDeclaredFields()) {
                if (!Modifier.isTransient(field.getModifiers())) {
                    Attribute attr = field.getAnnotation(org.master.unitoo.core.api.annotation.Attribute.class);
                    BusinessField bf = new BusinessField(
                            field,
                            attr != null && !attr.name().isEmpty() ? attr.name() : field.getName(),
                            attr != null ? attr.trim() : Decision.Parent,
                            attr != null ? attr.escape() : Decision.Parent,
                            app);
                    add(bf);
                }
            }
        }
    }

    private static class BusinessField implements IBusinessField {

        private final Field field;
        private final String name;
        private final IApplication app;
        private final Class valueClass;
        private final Class keyClass;
        private final Decision trim;
        private final Decision escape;

        public BusinessField(Field field, String name, Decision trim, Decision escape, IApplication app) {
            this.field = field;
            this.name = name;
            this.trim = trim;
            this.escape = escape;
            this.app = app;

            Type t = field.getGenericType();
            if (t instanceof ParameterizedType) {
                if (Map.class.isAssignableFrom(field.getType())) {
                    valueClass = (Class) ((ParameterizedType) t).getActualTypeArguments()[0];
                    keyClass = (Class) ((ParameterizedType) t).getActualTypeArguments()[1];
                } else {
                    keyClass = null;
                    valueClass = (Class) ((ParameterizedType) t).getActualTypeArguments()[0];
                }
            } else {
                keyClass = null;
                valueClass = null;
            }

            field.setAccessible(true);
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public Class type() {
            return field.getType();
        }

        @Override
        public Class itemType() {
            return valueClass;
        }

        @Override
        public Object get(IBusinessObject object) {
            try {
                return field.get(object);
            } catch (IllegalAccessException | IllegalArgumentException e) {
                app.log().error(new AttributeGetException(name, e));
                return null;
            }
        }

        @Override
        public void set(Object value, IBusinessObject object) {
            try {
                field.set(object, value);
            } catch (IllegalAccessException | IllegalArgumentException e) {
                app.log().error(new AttributeGetException(name, e));
            }
        }

        @Override
        public Class keyType() {
            return keyClass;
        }

        @Override
        public Decision trim() {
            return trim;
        }

        @Override
        public Decision escape() {
            return escape;
        }

    }

    private static class Timer implements Runnable {

        private volatile boolean active = true;
        private final DelayQueue<DelayedTaskDecorator> delayQueue = new DelayQueue<>();
        private final ThreadPoolExecutor executor;

        public Timer(ThreadPoolExecutor executor) {
            this.executor = executor;
        }

        public void executeAfter(IBackgroundTask task, long ms) {
            delayQueue.put(new DelayedTaskDecorator(task, ms));
        }

        @Override
        public void run() {
            while (active) {
                try {
                    DelayedTaskDecorator delayedTask = delayQueue.take();
                    if (delayedTask instanceof StopSignal) {
                        break;
                    } else {
                        executor.execute(delayedTask.source());
                    }
                } catch (InterruptedException e) {
                    break;
                }
            }
        }

        public void stop() {
            active = false;
            delayQueue.put(new StopSignal());
        }

    }

    private static class StopSignal extends DelayedTaskDecorator {

        public StopSignal() {
            super(null, 0);
        }
    }

    private static class DelayedTaskDecorator implements Delayed {

        private final IBackgroundTask source;
        private final long delay;
        private final long origin;

        @SuppressWarnings("LeakingThisInConstructor")
        public DelayedTaskDecorator(IBackgroundTask source, long delay) {
            this.origin = System.currentTimeMillis();
            this.source = source;
            this.delay = delay;
        }

        @Override
        public long getDelay(TimeUnit unit) {
            long result = unit.convert(delay - (System.currentTimeMillis() - origin), TimeUnit.MILLISECONDS);
            return result;
        }

        @Override
        public int compareTo(Delayed delayed) {
            if (delayed == this) {
                return 0;
            }

            long d = (getDelay(TimeUnit.MILLISECONDS) - delayed.getDelay(TimeUnit.MILLISECONDS));
            return ((d == 0) ? 0 : ((d < 0) ? -1 : 1));
        }

        public IBackgroundTask source() {
            return source;
        }
    }

    protected static class ThreadInfo implements IProcess {

        private ISession session;
        private ThreadState state;
        private final IApplication app;
        private final ThreadType type;
        private final LinkedBlockingDeque<IProcessContext> stack = new LinkedBlockingDeque<>(1024);

        public ThreadInfo(ThreadType type, IApplication app) {
            this.type = type;
            this.app = app;
            this.state = ThreadState.Idle;
        }

        public void init(ISession session) {
            this.session = session;
            stack.clear();
        }

        @Override
        public ThreadType type() {
            return type;
        }

        @Override
        public ThreadState state() {
            return state;
        }

        @Override
        public Iterable<IProcessContext> stack() {
            return stack;
        }

        @Override
        public IProcessContext methodEnter(String method, MethodType type, String info) {
            IProcessContext item = new ProcessContext(method, type, info);
            stack.push(item);
            return item;
        }

        @Override
        public void methodLive(String info) {
            updateInfo(info);
        }

        @Override
        public void methodLive(ThreadState state) {
            this.state = state;
        }

        @Override
        public void methodLive(ThreadState state, String info) {
            this.state = state;
            updateInfo(info);
        }

        private void updateInfo(String info) {
            ProcessContext item = (ProcessContext) stack.peek();
            if (item != null) {
                item.info(info);
            }
        }

        @Override
        public void methodExit() {
            stack.pop();
        }

        @Override
        public ISession session() {
            return session;
        }

        public void session(ISession session) {
            this.session = session;
        }

        @Override
        public ILanguage language() {
            ILanguage language = app.defaults().language();
            if (session != null) {
                IUser user = session.user();
                if (user != null) {
                    language = user.language();
                }
            }
            return language;
        }

        @Override
        public IProcessContext context() {
            IProcessContext context = stack.peek();
            if (context == null) {
                context = new ProcessContext("UNKNOWN", MethodType.System, "");
            }
            return context;
        }

    }

    private static class ProcessContext implements IProcessContext {

        private final String name;
        private final MethodType type;
        private String info;
        private boolean escape = false;
        private boolean trim = false;

        public ProcessContext(String name, MethodType type, String info) {
            this.name = name;
            this.type = type;
            this.info = info;
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public MethodType type() {
            return type;
        }

        @Override
        public String info() {
            return info;
        }

        public void info(String info) {
            this.info = info;
        }

        @Override
        public boolean escape() {
            return escape;
        }

        @Override
        public IProcessContext escape(boolean escape) {
            this.escape = escape;
            return this;
        }

        @Override
        public boolean trim() {
            return trim;
        }

        @Override
        public IProcessContext trim(boolean trim) {
            this.trim = trim;
            return this;
        }

        @Override
        public IProcessSnapshot save() {
            return new ProcessSnapshot(escape, trim);
        }

        @Override
        public void restore(IProcessSnapshot snapshot) {
            if (snapshot != null) {
                this.escape = snapshot.escape();
                this.trim = snapshot.trim();
            }
        }

    }

    private static class ProcessSnapshot implements IProcessSnapshot {

        private final boolean escape;
        private final boolean trim;

        public ProcessSnapshot(boolean escape, boolean trim) {
            this.escape = escape;
            this.trim = trim;
        }

        @Override
        public boolean escape() {
            return escape;
        }

        @Override
        public boolean trim() {
            return trim;
        }

    }

    private static class BootInfo implements IBootInfo {

        private final ArrayList<Throwable> errors = new ArrayList<>();
        private long bootTime = 0L;
        private boolean success = true;

        @Override
        public Iterable<Throwable> errors() {
            return errors;
        }

        @Override
        public void addBootError(Throwable error) {
            errors.add(error);
            success = false;
        }

        @Override
        public long bootTime() {
            return bootTime;
        }

        public void bootTime(long bootTime) {
            this.bootTime = bootTime;
        }

        @Override
        public boolean success() {
            return success;
        }

    }

    private static class MainServlet extends HttpServlet {

        private final BaseApplication app;

        public MainServlet(BaseApplication app) {
            this.app = app;
        }

        @SuppressWarnings("UseSpecificCatch")
        protected void doRequest(RequestMethod type, HttpServletRequest req, HttpServletResponse resp) {
            LogCache log = new LogCache(app.accessLog);
            log.debug(">>>");
            IFormatter formatter = app.defaults.formatter();
            String mapping = "";
            IControllerMethod method = null;
            try {
                String path = req.getContextPath();
                mapping = req.getRequestURI();
                if (mapping != null && mapping.length() > path.length()) {
                    mapping = mapping.substring(path.length());
                    method = app.mappings.get(mapping);
                }

                String ip = req.getHeader("X-Real-IP");
                if (ip == null || ip.isEmpty()) {
                    ip = req.getHeader("X-FORWARDED-FOR");
                    if (ip == null || ip.isEmpty()) {
                        ip = req.getRemoteAddr();
                    }
                }

                if (method != null) {
                    log.info(type.name() + " " + mapping + " from " + ip + " to " + method.controller().name() + "." + method.name() + "()");
                    formatter = method.getOutFormat();
                    method.process(type, ip, req, resp, log);
                } else {
                    log.error(type.name() + " " + mapping + " from " + ip + " mapping not found");
                    Throwable t = new MethodNotFound(mapping);
                    app.defaults.errorHandler().flush(method, mapping, resp, formatter, t);
                }

            } catch (Throwable t) {
                log.error(t);
                app.defaults.errorHandler().flush(method, mapping, resp, formatter, t);
            }
            log.debug("<<<\n");
            log.flush();
        }

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            doRequest(RequestMethod.GET, req, resp);
        }

        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            doRequest(RequestMethod.POST, req, resp);
        }

        @Override
        protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            doRequest(RequestMethod.DELETE, req, resp);
        }

        @Override
        protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            doRequest(RequestMethod.HEAD, req, resp);
        }

        @Override
        protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            doRequest(RequestMethod.OPTIONS, req, resp);
        }

        @Override
        protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            doRequest(RequestMethod.PUT, req, resp);
        }

        @Override
        protected void doTrace(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            doRequest(RequestMethod.TRACE, req, resp);
        }

    }

    private static class GlobalParams {

        private final Context context;
        private final IApplication app;

        public GlobalParams(IApplication app) throws NamingException {
            this.app = app;
            Context initContext = new InitialContext();
            this.context = (Context) initContext.lookup("java:/comp/env");
        }

        public Object get(String name) {
            try {
                return context.lookup(app.appName() + "." + name);
            } catch (NamingException e) {
                return null;
            }
        }
    }

}
