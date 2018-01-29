/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.impl;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import javax.servlet.http.Cookie;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.master.unitoo.core.UniToo;
import org.master.unitoo.core.api.IApplication;
import org.master.unitoo.core.api.IBootInfo;
import org.master.unitoo.core.api.IBusinessObject;
import org.master.unitoo.core.api.ILogger;
import org.master.unitoo.core.api.IProcessSnapshot;
import org.master.unitoo.core.api.IResponse;
import org.master.unitoo.core.api.annotation.Request;
import org.master.unitoo.core.api.components.IFormatter;
import org.master.unitoo.core.api.components.ext.IHTTPService;
import org.master.unitoo.core.api.synthetic.RequestFile;
import org.master.unitoo.core.errors.HttpResponseError;
import org.master.unitoo.core.errors.MethodNotFound;
import org.master.unitoo.core.server.LogCache;
import org.master.unitoo.core.types.ComponentContext;
import org.master.unitoo.core.types.ComponentType;
import org.master.unitoo.core.types.RequestMethod;
import org.master.unitoo.core.types.RequestParamMode;
import org.master.unitoo.core.types.RunnableState;
import org.master.unitoo.core.server.Setting;
import org.master.unitoo.core.api.annotation.Attribute;
import org.master.unitoo.core.api.annotation.Response;
import org.master.unitoo.core.types.Decision;
import org.master.unitoo.core.api.IDataContent;

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

    private void addQueryParam(StringBuilder buf, ExternalMethod method, RequestParameter param, Object value, String start) throws IOException {
        if (value != null) {
            String strvalue;
            try {
                strvalue = URLEncoder.encode(serializeParam(method, param, value), param.formats().format(method.paramsFormatter()).encoding());
            } catch (UnsupportedEncodingException e) {
                strvalue = param.formats().format(method.paramsFormatter()).format(value);
            }

            if (buf.length() == 0) {
                buf.append(start).append(param.name()).append("=").append(strvalue);
            } else {
                buf.append("&").append(param.name()).append("=").append(strvalue);
            }
        }
    }

    private String serializeParam(IFormatter formatter, IDataContent content, Object value) throws IOException {
        if (value instanceof IBusinessObject) {
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            content.serialize(
                    (IBusinessObject) value,
                    buf,
                    formatter);
            return new String(buf.toByteArray(), formatter.encoding());
        } else {
            return formatter.format(value);
        }
    }

    private String serializeParam(ExternalMethod method, RequestParameter param, Object value) throws IOException {
        if (value != null) {
            if (value instanceof IBusinessObject) {
                IFormatter formatter = param.formats().format(method.paramsFormatter());
                ByteArrayOutputStream buf = new ByteArrayOutputStream();
                param.formats().content(method.paramsContent()).serialize(
                        (IBusinessObject) value,
                        buf,
                        formatter);
                return new String(buf.toByteArray(), formatter.encoding());
            } else {
                return param.formats().format(method.paramsFormatter()).format(value);
            }
        } else {
            return null;
        }
    }

    @Override
    @SuppressWarnings("UseSpecificCatch")
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        LogCache log = new LogCache(log());
        log.info(">>>");
        IProcessSnapshot snapshot = app().process().context().save();
        try {
            String fullName = context.componentClass().getName() + "." + method.getName() + "()";
            log.debug("Calling external method: " + fullName);
            ExternalMethod eMethod = methods.get(method);
            if (eMethod == null) {
                throw new MethodNotFound(fullName);
            }

            IFormatter bodyFormatter = null;
            IDataContent bodyContent = null;
            Object bodyObject = null;
            RequestParameter bodyParam = null;

            ArrayList<HttpCookie> cookies = new ArrayList<>();
            StringBuilder strbuf = new StringBuilder();
            RequestParameter[] params = eMethod.paramList.params();
            for (int i = 0; i < params.length; i++) {
                if (args[i] != null) {
                    RequestParameter param = params[i];
                    app().process().context()
                            .escape(param.formats().escape())
                            .trim(param.formats().trim());
                    switch (param.mode()) {
                        case Param:
                            if ((param.forMethod() == RequestMethod.DEFAULT && eMethod.type == RequestMethod.GET) || param.forMethod() == RequestMethod.GET) {
                                addQueryParam(strbuf, eMethod, param, args[i], "?");
                            }
                            break;
                        case Cookie:
                            HttpCookie cookie = new HttpCookie(param.name(), serializeParam(eMethod, param, args[i]));
                            cookies.add(cookie);
                            break;
                        case Body:
                            bodyObject = args[i];
                            bodyParam = param;
                            bodyContent = param.formats().content(eMethod.paramsContent());
                            bodyFormatter = param.formats().format(eMethod.paramsFormatter());
                            break;
                        default:
                            break;
                    }
                }
            }

            String query = url() + "/" + eMethod.name + strbuf.toString();
            log.info("Calling query: " + query);
            HttpURLConnection conn = (HttpURLConnection) new URL(query).openConnection();
            conn.setUseCaches(false);
            conn.setDoOutput(eMethod.type == RequestMethod.POST);
            conn.setDoInput(eMethod.input);
            conn.setRequestMethod(eMethod.type.name());

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
                    String val = serializeParam(eMethod, param, args[i]);
                    conn.setRequestProperty(param.name(), val);
                    log.debug("Setting header: " + param.name() + " = " + val);
                }
            }

            if (eMethod.type == RequestMethod.POST) {

                HttpEntity multipart = null;

                if (ContentType.MULTIPART_FORM_DATA.getMimeType().equals(eMethod.paramsContent().contentType("UTF-8").getMimeType())) {
                    log.debug("Multipart params:");
                    MultipartEntityBuilder builder = MultipartEntityBuilder.create();

                    for (int i = 0; i < params.length; i++) {
                        RequestParameter param = params[i];
                        if ((param.forMethod() == RequestMethod.DEFAULT || param.mode() == RequestParamMode.Post) && args[i] != null) {

                            if (args[i] instanceof RequestFile) {
                                RequestFile file = (RequestFile) args[i];
                                log.debug("\t" + param.name() + " = file: " + file.name() + "; type: " + file.mime());
                                builder.addBinaryBody(
                                        param.name(),
                                        file.stream(),
                                        file.encoding() == null ? ContentType.create(file.mime()) : ContentType.create(file.mime(), file.encoding()),
                                        file.name());
                            } else {
                                IDataContent content = param.formats().content(eMethod.paramsContent());
                                ContentType type = content.contentType(param.formats().format(eMethod.paramsFormatter()).encoding());

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
                                    String value = serializeParam(eMethod, param, args[i]);
                                    log.debug("\t" + param.name() + " = type: " + type + "; value = " + value);
                                    builder.addTextBody(param.name(), value, type);
                                }
                            }
                        }
                    }
                    multipart = builder.build();
                }

                String requestType;
                if (bodyContent != null && bodyFormatter != null) {
                    requestType = bodyContent.contentType(bodyFormatter.encoding()).toString();
                } else if (multipart != null) {
                    requestType = multipart.getContentType().getValue();
                } else {
                    requestType = eMethod.paramsContent().contentType(eMethod.paramsFormatter().encoding()).toString();
                }
                conn.setRequestProperty("Content-Type", requestType);
                log.debug("Request content type: " + requestType);
                try (OutputStream out = conn.getOutputStream()) {
                    if (bodyObject != null && bodyParam != null && bodyContent != null && bodyFormatter != null) {
                        if (bodyObject instanceof byte[]) {
                            log.debug("Body content: bytes, length " + ((byte[]) bodyObject).length);
                            out.write((byte[]) bodyObject);
                        } else if (bodyObject instanceof InputStream) {
                            log.debug("Body content: stream");
                            UniToo.Copy((InputStream) bodyObject, out);
                        } else {
                            String val = serializeParam(bodyFormatter, bodyContent, bodyObject);
                            log.debug("Body content:\n" + val);
                            out.write(val.getBytes(bodyFormatter.encoding()));
                        }
                    }

                    if (ContentType.APPLICATION_FORM_URLENCODED.getMimeType().equals(eMethod.paramsContent().contentType("UTF-8").getMimeType())) {
                        strbuf.setLength(0);
                        for (int i = 0; i < params.length; i++) {
                            RequestParameter param = params[i];
                            if ((param.forMethod() == RequestMethod.DEFAULT || param.mode() == RequestParamMode.Post) && args[i] != null) {
                                addQueryParam(strbuf, eMethod, param, args[i], "");
                            }
                        }
                        String val = strbuf.toString();
                        log.debug("Post encoded params:\n" + val);
                        out.write(val.getBytes(eMethod.paramsFormatter().encoding()));
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
            IFormatter resultFormatter = eMethod.resultFormatter();
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

            if (eMethod.input) {
                app().process().context().escape(eMethod.resultEscape).trim(eMethod.resultTrim);

                if (log.getDebugState()) {
                    log.debug("Response headers:");
                    StringBuilder headerBuf = new StringBuilder();
                    Map<String, List<String>> map = conn.getHeaderFields();
                    for (Map.Entry<String, List<String>> entry : map.entrySet()) {
                        List<String> list = entry.getValue();
                        headerBuf.setLength(0);
                        headerBuf.append("\t");
                        if (entry.getKey() != null) {
                            headerBuf.append(entry.getKey()).append(" = ");
                        }
                        if (list.size() == 1) {
                            headerBuf.append(list.get(0));
                        } else {
                            for (String s : list) {
                                headerBuf.append(s).append("; ");
                            }
                        }

                        log.debug(headerBuf.toString());
                    }
                }

                IDataContent resultContent = eMethod.resultContent(conn.getContentType());
                if (InputStream.class == eMethod.outputType) {
                    log.debug("Response data: stream");
                    return new BufferedInputStream(conn.getInputStream());
                } else if (IResponse.class == eMethod.outputType) {
                    HttpResponse result = new HttpResponse(conn);
                    if (eMethod.outputGeneric == InputStream.class) {
                        result.body(conn.getInputStream());
                    } else {
                        try (InputStream input = conn.getInputStream()) {
                            result.body(makeResult(log, input, eMethod.outputGeneric, resultFormatter, resultContent));
                        }
                    }
                    return result;
                } else {
                    try (InputStream input = conn.getInputStream()) {
                        return makeResult(log, input, eMethod.outputType, resultFormatter, resultContent);
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
            app().process().context().restore(snapshot);
            log.info("<<<\n");
            log.flush();
        }
    }

    private Object makeResult(ILogger log, InputStream stream, Class type, IFormatter format, IDataContent content) throws IOException {
        if (IBusinessObject.class.isAssignableFrom(type)) {
            InputStream input;
            if (log.getDebugState()) {
                ByteArrayOutputStream buf = new ByteArrayOutputStream();
                UniToo.Copy(stream, buf);
                log.debug("Response object:\n" + new String(buf.toByteArray(), "UTF-8"));
                input = new ByteArrayInputStream(buf.toByteArray());
            } else {
                input = stream;
            }
            return content.deserialize(type, input, format);
        } else {
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            UniToo.Copy(stream, buf);
            byte[] data = buf.toByteArray();
            if (byte[].class == type) {
                log.debug("Response data: bytes, length: " + data.length);
                return data;
            } else {
                String str = new String(data, format.encoding());
                return format.parse(str, type);
            }
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
        private IFormatter paramsFormatter;
        private final Class<? extends IFormatter> resultFormatterClass;
        private IFormatter resultFormatter;
        private final Class<? extends IDataContent> paramsContentClass;
        private IDataContent paramsContent;
        private final Class<? extends IDataContent>[] resultContentClass;
        private ConcurrentHashMap<String, IDataContent> resultContents;
        private final RequestParametersList paramList;
        private final Class outputType;
        private final Class outputGeneric;
        private final boolean input;
        private final boolean resultEscape;
        private final boolean resultTrim;

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
            this.paramsContentClass = rqMapping.content();
            this.resultContentClass = rsMapping == null
                    ? null
                    : rsMapping.content();
            this.outputType = method.getReturnType();
            this.input = Void.class != method.getReturnType() && void.class != method.getReturnType();
            Type t = method.getGenericReturnType();
            if (t instanceof ParameterizedType) {
                outputGeneric = (Class) ((ParameterizedType) t).getActualTypeArguments()[0];
            } else {
                outputGeneric = String.class;
            }

            resultEscape = Decision.Get(parent.app().defaults().isEscapeExternalParams(), rqMapping.escape());
            resultTrim = Decision.Get(parent.app().defaults().isTrimExternalParams(), rqMapping.trim());

            boolean es = Decision.Get(parent.app().defaults().isEscapeExternalParams(), rqMapping.escape());
            boolean tr = Decision.Get(parent.app().defaults().isTrimExternalParams(), rqMapping.trim());
            this.paramList = new RequestParametersList(parent.app(), method, es, tr);
        }

        public IFormatter paramsFormatter() {
            if (paramsFormatter == null) {
                if (paramsFormatterClass == IFormatter.class) {
                    return parent.app().defaults().formatter();
                } else {
                    paramsFormatter = parent.app().component(paramsFormatterClass);
                }
            }
            return paramsFormatter;
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

        public IDataContent paramsContent() {
            if (paramsContent == null) {
                if (paramsContentClass == IDataContent.class) {
                    paramsContent = parent.app().defaults().content(null);
                } else {
                    paramsContent = parent.app().component(paramsContentClass);
                }
            }
            return paramsContent;
        }

        public IDataContent resultContent(String contentType) {
            String mime = ContentType.parse(contentType).getMimeType();

            if (resultContentClass == null) {
                return parent.app().defaults().content(mime);
            }

            if (resultContents == null) {
                resultContents = new ConcurrentHashMap<>();

                for (Class<? extends IDataContent> clazz : resultContentClass) {
                    IDataContent content = clazz == IDataContent.class
                            ? parent.app().defaults().content(mime)
                            : parent.app().component(clazz);
                    resultContents.put(content.contentType("UTF-8").getMimeType(), content);
                }
            }

            IDataContent content = resultContents.get(mime);
            return content == null ? parent.app().defaults().content(mime) : content;
        }
    }

    private class HttpResponse implements IResponse<Object> {

        private final HttpURLConnection connection;
        private Object body;

        public HttpResponse(HttpURLConnection connection) {
            this.connection = connection;
        }

        @Override
        public String getHeaderValue(String name) {
            return connection.getHeaderField(name);
        }

        @Override
        public List<String> getHeaderValues(String name) {
            return connection.getHeaderFields().get(name);
        }

        @Override
        public Map<String, List<String>> getHeaders() {
            return connection.getHeaderFields();
        }

        @Override
        public Iterable<Cookie> cookies() {
            return Collections.EMPTY_LIST;
        }

        @Override
        public Object body() {
            return body;
        }

        public void body(Object body) {
            this.body = body;
        }

        @Override
        public IDataContent content() {
            return null;
        }

    }

}
