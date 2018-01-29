/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.base;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.EnumMap;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.entity.ContentType;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.FileItemFactory;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.master.unitoo.core.UniToo;
import org.master.unitoo.core.api.IApplication;
import org.master.unitoo.core.api.IBootInfo;
import org.master.unitoo.core.api.IBusinessObject;
import org.master.unitoo.core.api.IControllerMethod;
import org.master.unitoo.core.api.ILogger;
import org.master.unitoo.core.api.annotation.Request;
import org.master.unitoo.core.api.annotation.Response;
import org.master.unitoo.core.api.components.IController;
import org.master.unitoo.core.api.components.IFormatter;
import org.master.unitoo.core.errors.ComponentNotActive;
import org.master.unitoo.core.errors.MethodNotAllowed;
import org.master.unitoo.core.types.ComponentContext;
import org.master.unitoo.core.types.ComponentType;
import org.master.unitoo.core.types.MethodType;
import org.master.unitoo.core.types.RequestMethod;
import org.master.unitoo.core.types.RunnableState;
import org.master.unitoo.core.api.components.IErrorHandler;
import org.master.unitoo.core.api.components.IValidator;
import org.master.unitoo.core.api.synthetic.IncomeRequestFile;
import org.master.unitoo.core.api.synthetic.RequestAddress;
import org.master.unitoo.core.api.synthetic.RequestFile;
import org.master.unitoo.core.api.synthetic.StreamRequestFile;
import org.master.unitoo.core.errors.InvalidContentType;
import org.master.unitoo.core.errors.ParameterEmptyException;
import org.master.unitoo.core.errors.ParameterInvalidException;
import org.master.unitoo.core.errors.ParameterMaskException;
import org.master.unitoo.core.errors.ParameterRangeException;
import org.master.unitoo.core.impl.RequestParameter;
import org.master.unitoo.core.impl.RequestParameterValidation;
import org.master.unitoo.core.impl.RequestParametersList;
import static org.master.unitoo.core.types.RequestParamMode.Body;
import static org.master.unitoo.core.types.RequestParamMode.Header;
import org.master.unitoo.core.types.SecureLevel;
import org.master.unitoo.core.api.IHttpFlushable;
import org.master.unitoo.core.api.IProcessSnapshot;
import org.master.unitoo.core.api.IResponse;
import org.master.unitoo.core.types.Decision;
import org.master.unitoo.core.api.IDataContent;

/**
 *
 * @author Andrey
 */
public abstract class BaseController implements IController {

    private ComponentContext context;
    private ILogger logger;
    private String base;
    private final ArrayList<IControllerMethod> methods = new ArrayList<>();
    private volatile RunnableState state = RunnableState.Stopped;
    private final AtomicInteger runCount = new AtomicInteger(0);
    private long stopFrom = 0;

    protected MethodType getMethodsTypes() {
        return MethodType.API;
    }

    @Override
    public RunnableState state() {
        return state == RunnableState.Stopped && runCount.get() != 0 ? RunnableState.Stopping : state;
    }

    @Override
    public ComponentType type() {
        return ComponentType.Controller;
    }

    @Override
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

    @Override
    public void init(ComponentContext context) {
        state = RunnableState.Init;
        this.context = context;
        this.base = context.url() == null || context.url().trim().isEmpty() ? "/" : context.url().trim();
        this.base = this.base.endsWith("/") ? this.base : this.base + "/";
        this.base = this.base.startsWith("/") ? this.base : "/" + this.base;
    }

    @Override
    public void destroy() {
    }

    @Override
    public String url() {
        return base;
    }

    @Override
    public IBootInfo boot() {
        return context.boot();
    }

    @Override
    public void prepare() {
        scanMethods(getClass());
        for (IControllerMethod method : methods) {
            app().register(method);
        }
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

    private void scanMethods(Class clazz) {
        if (clazz.getSuperclass() != null) {
            scanMethods(clazz.getSuperclass());
        }

        for (Method method : clazz.getDeclaredMethods()) {
            Request rqMapping = (Request) method.getAnnotation(Request.class);
            if (rqMapping != null) {
                ControllerMethod cm = new ControllerMethod(
                        base,
                        rqMapping,
                        (Response) method.getAnnotation(Response.class),
                        this,
                        method);
                methods.add(cm);
            }
        }

    }

    @Override
    public Iterable<IControllerMethod> methods() {
        return methods;
    }

    protected void live(String info) {
        app().process().methodLive(info);
    }

    private static class ControllerMethod implements IControllerMethod {

        private final String mapping;
        private final boolean outEscape;
        private final boolean outTrim;
        private final boolean inEscape;
        private final boolean inTrim;
        private final EnumMap<RequestMethod, RequestMethod> types = new EnumMap<>(RequestMethod.class);
        private final Class<? extends IErrorHandler> errorHanlerClass;
        private final Class<? extends IFormatter> inFormatClass;
        private IFormatter inFormatter;
        private final Class<? extends IFormatter> outFormatClass;
        private IFormatter outFormatter;
        private final Class<? extends IDataContent> inContentClass;
        private IDataContent inContent;
        private final Class<? extends IDataContent> outContentClass;
        private IDataContent outContent;
        private final Method method;
        private final BaseController parent;
        private IErrorHandler errorHandler;
        private final RequestParametersList params;
        private final SecureLevel secureLevel;
        private final boolean mimeStrict;

        public ControllerMethod(
                String base,
                Request rqMapping,
                Response rsMapping,
                BaseController parent,
                Method method) {

            String name = rqMapping.value() == null || rqMapping.value().isEmpty() ? method.getName() : rqMapping.value();
            this.mapping = "/".equals(name) ? base : base + name.trim();
            this.errorHanlerClass = rqMapping.errors();
            this.parent = parent;
            this.method = method;
            this.inFormatClass = rqMapping.format();
            this.outFormatClass = rsMapping == null ? IFormatter.class : rsMapping.format();
            this.secureLevel = rqMapping.secure();
            this.inContentClass = rqMapping.content();
            this.mimeStrict = rqMapping.strictContent();
            this.outContentClass = rsMapping == null || rsMapping.content().length == 0 ? IDataContent.class : rsMapping.content()[0];
            this.outEscape = Decision.Get(parent.app().defaults().isEscapeControllerResult(), rsMapping == null ? null : rsMapping.escape());
            this.outTrim = Decision.Get(parent.app().defaults().isTrimControllerResult(), rsMapping == null ? null : rsMapping.trim());
            this.inEscape = Decision.Get(parent.app().defaults().isEscapeControllerParams(), rqMapping.escape());
            this.inTrim = Decision.Get(parent.app().defaults().isTrimControllerParams(), rqMapping.trim());

            for (RequestMethod item : rqMapping.type()) {
                this.types.put(item, item);
            }

            params = new RequestParametersList(parent.app(), method, inEscape, inTrim);
        }

        @Override
        public IController controller() {
            return parent;
        }

        private IErrorHandler getErrorHandler() {
            if (errorHandler == null) {
                if (errorHanlerClass == IErrorHandler.class) {
                    errorHandler = parent.app().defaults().errorHandler();
                } else {
                    errorHandler = parent.app().component(errorHanlerClass);
                }
            }
            return errorHandler;
        }

        private IFormatter getInFormatter() {
            if (inFormatter == null) {
                if (inFormatClass == IFormatter.class) {
                    inFormatter = parent.app().defaults().formatter();
                } else {
                    inFormatter = parent.app().component(inFormatClass);
                }
            }
            return inFormatter;
        }

        @Override
        public IFormatter getOutFormat() {
            if (outFormatter == null) {
                if (outFormatClass == IFormatter.class) {
                    outFormatter = parent.app().defaults().formatter();
                } else {
                    outFormatter = parent.app().component(outFormatClass);
                }
            }
            return outFormatter;
        }

        private IDataContent getInContent(String mime) {
            if (inContent == null) {
                if (inContentClass == IDataContent.class) {
                    return parent.app().defaults().content(mime);
                } else {
                    inContent = parent.app().component(inContentClass);
                }
            }
            return inContent;
        }

        private IDataContent getOutContent() {
            if (outContent == null) {
                if (outContentClass == IDataContent.class) {
                    outContent = parent.app().defaults().content(null);
                } else {
                    outContent = parent.app().component(outContentClass);
                }
            }
            return outContent;
        }

        private String extractFileName(HttpServletRequest req) {
            String contentDisposition = req.getHeader("Content-Disposition");
            if (contentDisposition != null) {
                int i = contentDisposition.indexOf("filename=");
                if (i != -1) {
                    return contentDisposition.substring(i + 10, contentDisposition.length() - 1);
                }
            }
            return null;
        }

        @Override
        @SuppressWarnings({"TooBroadCatch", "UseSpecificCatch"})
        public void process(RequestMethod type, String fromIP, HttpServletRequest req, HttpServletResponse httpResponse, ILogger log) throws Exception {
            if (parent.state == RunnableState.Running) {
                parent.runCount.incrementAndGet();

                log.debug("Content type: " + req.getContentType());
                ContentType contentType = req.getContentType() == null || req.getContentType().trim().isEmpty()
                        ? ContentType.TEXT_HTML
                        : ContentType.parse(req.getContentType());

                if (mimeStrict && inContentClass != IDataContent.class) {
                    if (!getInContent(null).contentType("UTF-8").getMimeType().equals(contentType.getMimeType())) {
                        throw new InvalidContentType(contentType.getMimeType());
                    }
                }

                IProcessSnapshot snapshot = parent.app().process().context().save();

                try {
                    if (types.containsKey(type)) {

                        log.debug("Secure level: " + secureLevel.name());
                        if (secureLevel != SecureLevel.None) {
                            parent.app().beforeRequest(this, req.getSession(true));
                        }

                        parent.app().process().methodEnter(name(), parent.getMethodsTypes(), mapping);
                        try {
                            ArrayList<String> values = new ArrayList<>(1);
                            ArrayList<Object> direct = new ArrayList<>(1);
                            Object[] args = new Object[params.size()];
                            HashMap<String, List<FileItem>> multipart = null;

                            if (log.getDebugState()) {
                                log.debug("Headers:");
                                Enumeration<String> headers = req.getHeaderNames();
                                while (headers.hasMoreElements()) {
                                    String name = headers.nextElement();
                                    log.debug("\t" + name);
                                    Enumeration<String> hvals = req.getHeaders(name);
                                    while (hvals.hasMoreElements()) {
                                        log.debug("\t\t" + hvals.nextElement());
                                    }
                                }

                                log.debug("Params:");
                                Enumeration<String> pnames = req.getParameterNames();
                                while (pnames.hasMoreElements()) {
                                    String name = pnames.nextElement();
                                    log.debug("\t" + name);
                                    String[] pvals = req.getParameterValues(name);
                                    for (String s : pvals) {
                                        log.debug("\t\t" + s);
                                    }
                                }
                            }

                            if (contentType.getMimeType() != null && contentType.getMimeType().equalsIgnoreCase("multipart/form-data")) {
                                log.debug("Multipart data:");
                                multipart = new HashMap<>();
                                FileItemFactory factory = new DiskFileItemFactory();
                                ServletFileUpload upload = new ServletFileUpload(factory);
                                try {
                                    Iterator<List<FileItem>> it = upload.parseParameterMap(req).values().iterator();
                                    while (it.hasNext()) {
                                        List<FileItem> files = it.next();
                                        for (FileItem item : files) {
                                            String paramName = item.getFieldName();
                                            List<FileItem> items = multipart.get(paramName);
                                            if (items == null) {
                                                items = new ArrayList<>(1);
                                                multipart.put(paramName, items);
                                            }
                                            items.add(item);
                                            if (item.isFormField()) {
                                                log.debug("\t" + paramName + " [" + item.getContentType() + "] = " + item.getString());
                                            } else {
                                                log.debug("\t" + paramName + " [" + item.getContentType() + "] = length: " + item.getSize());
                                            }
                                        }
                                    }
                                } catch (FileUploadException e) {
                                    throw new IOException(e);
                                }
                            }

                            for (int i = 0; i < args.length; i++) {
                                RequestParameter param = params.parameter(i);

                                parent.app().process().context()
                                        .escape(param.formats().escape())
                                        .trim(param.formats().trim());

                                if (RequestAddress.class.isAssignableFrom(param.type())) {
                                    RequestAddress addr = new RequestAddress(
                                            req.getRemoteHost(),
                                            fromIP,
                                            req.getRemotePort(),
                                            req.getLocalName(),
                                            req.getLocalAddr(),
                                            req.getLocalPort());
                                    args[i] = param.isArray() ? new RequestAddress[]{addr} : addr;
                                } else {
                                    values.clear();
                                    direct.clear();
                                    switch (param.mode()) {
                                        case Cookie:
                                            for (Cookie cookie : req.getCookies()) {
                                                if (param.name().equalsIgnoreCase(cookie.getName())) {
                                                    values.add(cookie.getValue());
                                                    if (!param.type().isArray()) {
                                                        break;
                                                    }
                                                }
                                            }
                                            break;
                                        case Header:
                                            Enumeration<String> hdrs = req.getHeaders(param.name());
                                            while (hdrs.hasMoreElements()) {
                                                values.add(hdrs.nextElement());
                                                if (!param.type().isArray()) {
                                                    break;
                                                }
                                            }
                                            break;
                                        case Body:
                                            if (InputStream.class.isAssignableFrom(param.type())) {
                                                direct.add(req.getInputStream());
                                                log.debug("Body: stream");
                                            } else if (RequestFile.class.isAssignableFrom(param.type())) {
                                                direct.add(new StreamRequestFile(req.getInputStream(), extractFileName(req), contentType));
                                                log.debug("Body: file");
                                            } else if (IBusinessObject.class.isAssignableFrom(param.type())) {
                                                InputStream input = req.getInputStream();
                                                if (log.getDebugState()) {
                                                    ByteArrayOutputStream buf = new ByteArrayOutputStream();
                                                    UniToo.Copy(req.getInputStream(), buf);
                                                    log.debug("Body:\n" + new String(buf.toByteArray(), "UTF-8"));
                                                    input = new ByteArrayInputStream(buf.toByteArray());
                                                }
                                                direct.add(param.formats().content(getInContent(contentType.getMimeType())).deserialize(
                                                        param.type(),
                                                        input,
                                                        param.formats().format(getInFormatter())));
                                            } else {
                                                ByteArrayOutputStream buf = new ByteArrayOutputStream();
                                                UniToo.Copy(req.getInputStream(), buf);
                                                byte[] body = buf.toByteArray();
                                                if (byte[].class.isAssignableFrom(param.type())) {
                                                    direct.add(body);
                                                } else {
                                                    values.add(new String(body, param.formats().format(getInFormatter()).encoding()));
                                                }
                                                if (log.getDebugState()) {
                                                    log.debug("Body:\n");
                                                    log.debug(new String(body, param.formats().format(getInFormatter()).encoding()));
                                                }
                                            }
                                            break;
                                        default:
                                            if (multipart != null && (param.forMethod() == RequestMethod.DEFAULT || param.forMethod() == RequestMethod.POST)) {
                                                List<FileItem> items = multipart.get(param.name());
                                                if (items != null) {
                                                    for (FileItem item : items) {
                                                        if (InputStream.class.isAssignableFrom(param.type())) {
                                                            direct.add(item.getInputStream());
                                                        } else if (RequestFile.class.isAssignableFrom(param.type())) {
                                                            direct.add(new IncomeRequestFile(item));
                                                        } else if (IBusinessObject.class.isAssignableFrom(param.type())) {
                                                            direct.add(param.formats().content(getInContent(ContentType.parse(item.getContentType()).getMimeType())).deserialize(
                                                                    param.type(),
                                                                    item.getInputStream(),
                                                                    param.formats().format(getInFormatter())));
                                                        } else if (byte[].class.isAssignableFrom(param.type())) {
                                                            ByteArrayOutputStream buf = new ByteArrayOutputStream();
                                                            UniToo.Copy(item.getInputStream(), buf);
                                                            direct.add(buf.toByteArray());
                                                        } else {
                                                            values.add(item.getString());
                                                        }
                                                        if (!param.type().isArray()) {
                                                            break;
                                                        }
                                                    }
                                                }
                                            } else if (param.forMethod() == RequestMethod.DEFAULT || param.forMethod() == RequestMethod.GET) {
                                                String[] vals = req.getParameterValues(param.name());
                                                if (vals != null && vals.length > 0) {
                                                    if (param.type().isArray()) {
                                                        values.addAll(Arrays.asList(vals));
                                                    } else {
                                                        values.add(vals[0]);
                                                    }
                                                }
                                            }
                                            break;
                                    }

                                    if (direct.size() > 0) {
                                        if (param.type().isArray()) {
                                            args[i] = direct.toArray((Object[]) Array.newInstance(param.type(), direct.size()));
                                        } else {
                                            args[i] = direct.get(0);
                                        }
                                    } else if (values.size() > 0) {

                                        if (param.type().isArray()) {
                                            args[i] = Array.newInstance(param.type(), values.size());

                                            for (int v = 0; v < values.size(); v++) {
                                                ((Object[]) args[i])[v] = processParamValue(param, values.get(i), contentType);
                                            }

                                        } else {
                                            args[i] = processParamValue(param, values.get(0), contentType);
                                        }
                                    }
                                }
                            }

                            Object obj = method.invoke(parent, args);

                            parent.app().process().context()
                                    .escape(outEscape)
                                    .trim(outTrim);

                            if (obj != null) {
                                log.debug("Response:");
                                if (obj instanceof IHttpFlushable) {
                                    ((IHttpFlushable) obj).flush(httpResponse);
                                    log.debug("\tBody: " + obj.getClass().getName());
                                } else if (obj instanceof IResponse) {
                                    IResponse<Object> response = (IResponse) obj;
                                    for (Map.Entry<String, List<String>> entry : response.getHeaders().entrySet()) {
                                        for (String value : entry.getValue()) {
                                            httpResponse.addHeader(entry.getKey(), value);
                                        }
                                    }

                                    for (Cookie cookie : response.cookies()) {
                                        httpResponse.addCookie(cookie);
                                    }
                                    flush(httpResponse, response.body(), log, response.content());
                                } else {
                                    flush(httpResponse, obj, log, null);
                                }

                                if (log.getDebugState()) {
                                    log.debug("\tHeaders:");
                                    StringBuilder buf = new StringBuilder();
                                    for (String header : httpResponse.getHeaderNames()) {
                                        boolean first = true;
                                        buf.setLength(0);
                                        buf.append("\t\t").append(header).append(" = ");
                                        for (String hvalue : httpResponse.getHeaders(header)) {
                                            if (first) {
                                                first = false;
                                            } else {
                                                buf.append("; ");
                                            }

                                            buf.append(hvalue);
                                        }
                                        log.debug(buf.toString());
                                    }
                                }
                            } else {
                                log.debug("Response: EMPTY");
                                httpResponse.setStatus(HttpServletResponse.SC_NO_CONTENT);
                            }
                        } finally {
                            parent.app().process().methodExit();
                        }
                    } else {
                        throw new MethodNotAllowed(type.name(), mapping);
                    }
                } finally {
                    parent.app().process().context().restore(snapshot);
                    parent.runCount.decrementAndGet();
                    parent.app().afterRequest(this);
                }
            } else {
                throw new ComponentNotActive(parent.name());
            }
        }

        private Object processParamValue(RequestParameter param, String value, ContentType contentType) throws Exception {
            Object result;
            if (IBusinessObject.class.isAssignableFrom(param.type())) {
                result = value == null
                        ? null
                        : param.formats().content(getInContent(contentType.getMimeType())).deserialize(
                                param.type(),
                                new ByteArrayInputStream(value.getBytes(param.formats().format(getInFormatter()).encoding())),
                                param.formats().format(getInFormatter()));
            } else {
                result = parent.app().parse(getInFormatter(), value, param.type());
            }
            return validate(result, param);
        }

        private int available(InputStream stream) {
            try {
                return stream.available();
            } catch (IOException ignore) {
                return 0;
            }
        }

        private void flush(HttpServletResponse resp, Object obj, ILogger log, IDataContent content) throws IOException {
            if (content == null) {
                content = getOutContent();
            }
            resp.setContentType(content.contentType(getOutFormat().encoding()).toString());

            if (obj instanceof IBusinessObject) {
                if (log.getDebugState()) {
                    ByteArrayOutputStream buf = new ByteArrayOutputStream();
                    content.serialize((IBusinessObject) obj, buf, getOutFormat());
                    log.debug("\tBody:\n" + new String(buf.toByteArray(), getOutFormat().encoding()));
                }

                content.serialize((IBusinessObject) obj, resp.getOutputStream(), getOutFormat());
            } else {
                String result = getOutFormat().format(obj);
                if (log.getDebugState()) {
                    log.debug("\tBody:\n" + result);
                }

                resp.getWriter().print(result);
            }
        }

        private Object validate(Object value, RequestParameter param) throws ParameterInvalidException {
            RequestParameterValidation validation = param.validation();

            if (validation.required() && (value == null || (value instanceof String && ((String) value).isEmpty()))) {
                throw new ParameterEmptyException(param.name());
            }

            if (validation.min() != 0 && value != null) {
                if ((value instanceof String && ((String) value).length() < validation.min())
                        || (value instanceof byte[] && ((byte[]) value).length < validation.min())
                        || (value instanceof InputStream && available(((InputStream) value)) < validation.min())
                        || (value instanceof Number && ((Number) value).doubleValue() < validation.min())
                        || (value instanceof Date && ((Date) value).getTime() < validation.min())) {
                    throw new ParameterRangeException(param.name(), validation.min(), validation.max());
                }
            }

            if (validation.max() != 0 && value != null) {
                if ((value instanceof String && ((String) value).length() > validation.max())
                        || (value instanceof byte[] && ((byte[]) value).length > validation.max())
                        || (value instanceof InputStream && available(((InputStream) value)) > validation.max())
                        || (value instanceof Number && ((Number) value).doubleValue() > validation.max())
                        || (value instanceof Date && ((Date) value).getTime() > validation.max())) {
                    throw new ParameterRangeException(param.name(), validation.min(), validation.max());
                }
            }

            if (!validation.mask().isEmpty() && value != null && value instanceof String && !Pattern.matches(validation.mask(), (String) value)) {
                throw new ParameterMaskException(param.name(), validation.mask());
            }

            IValidator valid = validation.validator();
            if (valid != null) {
                value = valid.validate(value);
            }

            return value;
        }

        @Override
        public String name() {
            return method.getName();
        }

        @Override
        public String mapping() {
            return mapping;
        }

        @Override
        public Iterable<RequestMethod> types() {
            return types.values();
        }

        @Override
        public IErrorHandler errors() {
            return getErrorHandler();
        }

        @Override
        public SecureLevel secureLevel() {
            return secureLevel;
        }

    }

}
