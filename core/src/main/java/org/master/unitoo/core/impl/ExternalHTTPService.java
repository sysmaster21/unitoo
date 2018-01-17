/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.impl;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.master.unitoo.core.UniToo;
import org.master.unitoo.core.api.IApplication;
import org.master.unitoo.core.api.IBootInfo;
import org.master.unitoo.core.api.IFormatContext;
import org.master.unitoo.core.api.ILogger;
import org.master.unitoo.core.api.annotation.Request;
import org.master.unitoo.core.api.components.IFormatter;
import org.master.unitoo.core.api.components.ext.IHTTPService;
import org.master.unitoo.core.api.synthetic.RequestFile;
import org.master.unitoo.core.errors.HttpResponseError;
import org.master.unitoo.core.errors.MethodNotFound;
import org.master.unitoo.core.server.LogCache;
import org.master.unitoo.core.types.ComponentContext;
import org.master.unitoo.core.types.ComponentType;
import org.master.unitoo.core.types.MIME;
import org.master.unitoo.core.types.RequestMethod;
import org.master.unitoo.core.types.RequestParamMode;
import org.master.unitoo.core.types.RunnableState;
import org.master.unitoo.core.server.Setting;
import org.master.unitoo.core.api.annotation.Attribute;
import org.master.unitoo.core.api.annotation.Response;

/**
 *
 * @author Andrey
 */
public class ExternalHTTPService implements IHTTPService, InvocationHandler {

    @Attribute(name = "url", value = "")
    public Setting<String> url;

    private ComponentContext context;
    private ILogger logger;
    private String base;
    private final ConcurrentHashMap<Method, ExternalMethod> methods = new ConcurrentHashMap<>();
    private volatile RunnableState state = RunnableState.Stopped;
    private final AtomicInteger runCount = new AtomicInteger(0);
    private long stopFrom = 0;

    public void scan(Class<? extends IHTTPService> iface) {
        if (iface.getSuperclass() != null && IHTTPService.class.isAssignableFrom(iface)) {
            scan(iface);
        }

        for (Method method : iface.getDeclaredMethods()) {
            Request rqMapping = (Request) method.getAnnotation(Request.class);
            if (rqMapping != null) {
                ExternalMethod em = new ExternalMethod(
                        this,
                        method,
                        rqMapping,
                        (Response) method.getAnnotation(Response.class)
                );

                methods.put(method, em);
            }
        }
    }

    private void addQueryParam(StringBuilder buf, RequestParameter param, Object value, IFormatter formatter, String start) {
        if (value != null) {
            String strvalue;
            try {
                strvalue = URLEncoder.encode(formatter.format(value, param.getFormatContext()), formatter.encoding().name());
            } catch (UnsupportedEncodingException e) {
                strvalue = formatter.format(value);
            }

            if (buf.length() == 0) {
                buf.append(start).append(param.name()).append("=").append(strvalue);
            } else {
                buf.append("&").append(param.name()).append("=").append(strvalue);
            }
        }
    }

    @Override
    @SuppressWarnings("UseSpecificCatch")
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        LogCache log = new LogCache(log());
        log.info(">>>");
        try {
            String fullName = context.componentClass().getName() + "." + method.getName() + "()";
            log.debug("Calling external method: " + fullName);
            ExternalMethod em = methods.get(method);
            if (em == null) {
                throw new MethodNotFound(fullName);
            }

            IFormatter paramFormatter = em.paramFormatter();
            log.debug("Using param formatter: " + paramFormatter.getClass());
            Object bodyObject = null;
            RequestParameter bodyParam = null;

            ArrayList<HttpCookie> cookies = new ArrayList<>();
            StringBuilder strbuf = new StringBuilder();
            RequestParameter[] params = em.params().params();
            for (int i = 0; i < params.length; i++) {
                RequestParameter param = params[i];
                if (param.mode() == RequestParamMode.Param && args[i] != null) {
                    addQueryParam(strbuf, param, args[i], paramFormatter, "?");
                } else if (param.mode() == RequestParamMode.Cookie) {
                    String val = paramFormatter.format(args[i], param.getFormatContext());
                    HttpCookie cookie = new HttpCookie(param.name(), val);
                    cookies.add(cookie);
                } else if (param.mode() == RequestParamMode.Body) {
                    bodyObject = args[i];
                    bodyParam = param;
                }
            }

            String query = url() + "/" + em.getName() + strbuf.toString();
            log.info("Calling query: " + query);
            HttpURLConnection conn = (HttpURLConnection) new URL(query).openConnection();
            conn.setUseCaches(false);
            conn.setDoOutput(em.hasOutput());
            conn.setDoInput(em.hasInput());
            conn.setRequestMethod(em.getType().name());

            strbuf.setLength(0);
            boolean first = true;
            for (HttpCookie cookie : cookies) {
                if (first) {
                    first = false;
                } else {
                    strbuf.append("; ");
                }
                strbuf.append(cookie.toString());
            }
            conn.setRequestProperty("Cookie", strbuf.toString());

            for (int i = 0; i < params.length; i++) {
                RequestParameter param = params[i];
                if (param.mode() == RequestParamMode.Header && args[i] != null) {
                    String val = paramFormatter.format(args[i], param.getFormatContext());
                    conn.setRequestProperty(param.name(), val);
                    log.debug("Setting header: " + param.name() + " = " + val);
                }
            }

            if (em.hasOutput()) {

                HttpEntity multipart = null;

                if (MIME.MULTIPART_FORM_DATA.equals(em.contentType())) {
                    log.debug("Multipart params:");
                    MultipartEntityBuilder builder = MultipartEntityBuilder.create();

                    for (int i = 0; i < params.length; i++) {
                        RequestParameter param = params[i];
                        if (param.mode() == RequestParamMode.Post && args[i] != null) {

                            if (args[i] instanceof RequestFile) {
                                RequestFile file = (RequestFile) args[i];
                                log.debug("\t" + param.name() + " = file: " + file.name() + "; type: " + file.mime());
                                builder.addBinaryBody(
                                        param.name(),
                                        file.stream(),
                                        MIME.ToContentType(file.mime()),
                                        file.name());
                            } else {
                                ContentType type;
                                if (param.mime() != null && !param.mime().isEmpty()) {
                                    type = MIME.ToContentType(param.mime());
                                } else {
                                    type = MIME.ByClass(param.type(), ContentType.TEXT_PLAIN);
                                }

                                if (args[i] instanceof byte[]) {
                                    log.debug("\t" + param.name() + " = binary: " + ((byte[]) args[i]).length + " bytes; type: " + type.getMimeType());
                                    builder.addBinaryBody(
                                            param.name(),
                                            (byte[]) args[i],
                                            type,
                                            "");
                                } else if (args[i] instanceof InputStream) {
                                    log.debug("\t" + param.name() + " = stream; type: " + type.getMimeType());
                                    builder.addBinaryBody(
                                            param.name(),
                                            (InputStream) args[i],
                                            type,
                                            "");
                                } else {
                                    String value = paramFormatter.format(args[i], param.getFormatContext());
                                    log.debug("\t" + param.name() + " = type: " + type.getMimeType() + "; value = " + value);
                                    builder.addTextBody(param.name(), value, type);
                                }
                            }
                        }
                    }
                    multipart = builder.build();
                }

                String contentType;
                if (MIME.WithCharset(em.contentType())) {
                    contentType = em.contentType() + "; charset=" + paramFormatter.encoding().name();
                } else if (multipart != null) {
                    contentType = multipart.getContentType().getValue();
                } else {
                    contentType = em.contentType();
                }
                conn.setRequestProperty("Content-Type", contentType);
                log.debug("Request content type: " + contentType);
                try (OutputStream out = conn.getOutputStream()) {
                    if (bodyObject != null && bodyParam != null) {
                        if (bodyObject instanceof byte[]) {
                            log.debug("Body content: bytes, length " + ((byte[]) bodyObject).length);
                            out.write((byte[]) bodyObject);
                        } else if (bodyObject instanceof InputStream) {
                            log.debug("Body content: stream");
                            UniToo.Copy((InputStream) bodyObject, out);
                        } else {
                            String val = paramFormatter.format(bodyObject, bodyParam.getFormatContext());
                            log.debug("Body content:\n" + val);
                            out.write(val.getBytes(paramFormatter.encoding()));
                        }
                    }

                    if (MIME.X_WWW_FORM_URLENCODED.equals(em.contentType)) {
                        strbuf.setLength(0);
                        for (int i = 0; i < params.length; i++) {
                            RequestParameter param = params[i];
                            if (param.mode() == RequestParamMode.Post && args[i] != null) {
                                addQueryParam(strbuf, param, args[i], paramFormatter, "");
                            }
                        }
                        String val = strbuf.toString();
                        log.debug("Post encoded params:\n" + val);
                        out.write(val.getBytes(paramFormatter.encoding()));
                    } else if (multipart != null) {
                        multipart.writeTo(out);
                    }
                    out.flush();
                }
                conn.connect();
            } else {
                conn.setDoOutput(false);
            }

            int rc = conn.getResponseCode();
            log.info("Response code: " + rc);
            IFormatter resultFormatter = em.resultFormatter();
            if (rc != 200) {
                HttpResponseError error = new HttpResponseError(rc);
                String text = null;
                try (InputStream input = conn.getErrorStream()) {
                    ByteArrayOutputStream buf = new ByteArrayOutputStream();
                    UniToo.Copy(input, buf);
                    text = new String(buf.toByteArray(), resultFormatter.encoding());
                } catch (Throwable ignore) {

                }
                log.error("Error text:\n" + text);
                throw error;
            }

            if (em.hasInput()) {
                if (InputStream.class == em.outputType()) {
                    log.debug("Response data: stream");
                    return new BufferedInputStream(conn.getInputStream());
                } else {
                    try (InputStream input = conn.getInputStream()) {

                        ByteArrayOutputStream buf = new ByteArrayOutputStream();
                        UniToo.Copy(input, buf);
                        byte[] data = buf.toByteArray();

                        if (byte[].class == em.outputType) {
                            log.debug("Response data: bytes, length: " + data.length);
                            return data;
                        } else {
                            String str = new String(data, resultFormatter.encoding());
                            log.debug("Response data:\n" + str);
                            return resultFormatter.parse(str, em.outputType(), em.getFormatContext());
                        }
                    }
                }
            } else {
                try {
                    conn.getInputStream().close();
                } catch (Throwable ignore) {

                }
            }
            return null;
        } finally {
            log.info("<<<\n");
            log.flush();
        }
    }

    @Override
    public RunnableState state() {
        return state == RunnableState.Stopped && runCount.get() != 0 ? RunnableState.Stopping : state;
    }

    @Override
    public ComponentType type() {
        return ComponentType.HttpService;
    }

    public ILogger log() {
        if (logger == null) {
            logger = app().log(context.logger(), getClass());
        }
        return logger;
    }

    @Override
    public String info() {
        return context.info();
    }

    @Override
    public String version() {
        return context.version();
    }

    public String url() {
        if (url == null || url.val() == null || url.val().trim().isEmpty()) {
            return base;
        } else {
            return url.val();
        }
    }

    @Override
    public void init(ComponentContext context) {
        state = RunnableState.Init;
        this.context = context;
        this.base = context.url();
    }

    @Override
    public void destroy() {
    }

    @Override
    public IBootInfo boot() {
        return context.boot();
    }

    @Override
    public void prepare() {
        scan(context.componentClass());
    }

    @Override
    public void start() {
        state = RunnableState.Running;
    }

    @Override
    public long stopping() {
        return System.currentTimeMillis() - stopFrom;
    }

    @Override
    public void stop() {
        stopFrom = System.currentTimeMillis();
        state = RunnableState.Stopped;
    }

    @Override
    public void kill() {
        //NO THREADS TO STOP
    }

    @Override
    public void reject() {
    }

    @Override
    public String name() {
        return getClass().getName();
    }

    @Override
    public String description() {
        return context.description();
    }

    @Override
    public IApplication app() {
        return context.application();
    }

    private static class ExternalMethod {

        private final ExternalHTTPService parent;
        private final String name;
        private final RequestMethod type;
        private final Class<? extends IFormatter> paramsFormatterClass;
        private final Class<? extends IFormatter> resultFormatterClass;
        private IFormatter resultFormatter;
        private IFormatter paramFormatter;
        private final RequestParametersList params;
        private final Class outputType;
        private final String contentType;
        private final boolean output;
        private final boolean input;
        private final IFormatContext resultContext;

        public ExternalMethod(
                ExternalHTTPService parent,
                Method method,
                Request rqMapping,
                Response rsMapping
        ) {

            this.parent = parent;
            this.name = rqMapping.value();
            this.type = rqMapping.type().length == 0 ? RequestMethod.GET : rqMapping.type()[0];
            this.paramsFormatterClass = rqMapping.format();
            this.resultFormatterClass = rsMapping == null ? IFormatter.class : rsMapping.format();
            this.outputType = method.getReturnType();
            this.input = Void.class != method.getReturnType() && void.class != method.getReturnType();

            boolean es = UniToo.getEffectiveDecision(parent.app().defaults().isEscapeExternalParams(), rqMapping.escape());
            boolean tr = UniToo.getEffectiveDecision(parent.app().defaults().isTrimExternalParams(), rqMapping.trim());
            this.params = new RequestParametersList(parent.app(), method, false, es, tr);
            this.contentType = rqMapping.mime().isEmpty() ? params.contentType() : rqMapping.mime();
            this.output = params.isPost();

            final boolean resultEscape = UniToo.getEffectiveDecision(parent.app().defaults().isEscapeExternalParams(), rqMapping.escape());
            final boolean resultTrim = UniToo.getEffectiveDecision(parent.app().defaults().isTrimExternalParams(), rqMapping.trim());
            this.resultContext = new IFormatContext() {
                @Override
                public boolean escape() {
                    return resultEscape;
                }

                @Override
                public boolean trim() {
                    return resultTrim;
                }
            };
        }

        public IFormatter paramFormatter() {
            if (paramFormatter == null) {
                if (paramsFormatterClass == IFormatter.class) {
                    return parent.app().defaults().formatter();
                } else {
                    paramFormatter = parent.app().component(paramsFormatterClass);
                }
            }
            return paramFormatter;
        }

        public IFormatter resultFormatter() {
            if (resultFormatter == null) {
                if (resultFormatterClass == IFormatter.class) {
                    return parent.app().defaults().formatter();
                } else {
                    resultFormatter = parent.app().component(resultFormatterClass);
                }
            }
            return resultFormatter;
        }

        public RequestParametersList params() {
            return params;
        }

        public String getName() {
            return name;
        }

        public RequestMethod getType() {
            return type;
        }

        public Class outputType() {
            return outputType;
        }

        public String contentType() {
            return contentType;
        }

        public boolean hasOutput() {
            return output;
        }

        public boolean hasInput() {
            return input;
        }

        public IFormatContext getFormatContext() {
            return resultContext;
        }

    }

}
