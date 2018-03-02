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
import org.master.unitoo.core.api.annotation.HTTP;
import org.master.unitoo.core.api.annotation.Default;
import org.master.unitoo.core.api.synthetic.RequestData;
import org.master.unitoo.core.api.util.IUser;
import org.master.unitoo.core.errors.InvalidSession;
import org.master.unitoo.core.errors.MethodFailed;

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

    private Class<? extends IErrorHandler> errHandler = IErrorHandler.class;
    private Class<? extends IDataContent> requestContent = IDataContent.class;
    private Decision strictMime = Decision.Parent;
    private RequestMethod requestMethod = RequestMethod.DEFAULT;

    private Class<? extends IFormatter> paramsFormat = IFormatter.class;
    private Class<? extends IDataContent> paramsContent = IDataContent.class;
    private Decision paramsEscape = Decision.Parent;
    private Decision paramsTrim = Decision.Parent;

    private Class<? extends IFormatter> responseFormat = IFormatter.class;
    private Class<? extends IDataContent> responseContent = IDataContent.class;
    private Decision responseEscape = Decision.Parent;
    private Decision responseTrim = Decision.Parent;

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
        return context.description();
    }

    @Override
    public String version() {
        return context.version();
    }

    @Override
    public void init(ComponentContext context) {
        state = RunnableState.Init;
        this.context = context;

        HTTP settings = getClass().getAnnotation(HTTP.class);
        if (settings != null) {
            this.base = settings.url() == null || settings.url().trim().isEmpty() ? "/" : settings.url().trim();
            this.base = this.base.endsWith("/") ? this.base : this.base + "/";
            this.base = this.base.startsWith("/") ? this.base : "/" + this.base;

            errHandler = settings.errors();
            requestContent = settings.request();
            strictMime = settings.strictMime();
            requestMethod = settings.method();

            Default fmt = settings.params();
            if (fmt != null) {
                paramsFormat = fmt.format();
                paramsContent = fmt.content();
                paramsEscape = fmt.escape();
                paramsTrim = fmt.trim();
            }

            fmt = settings.response();
            if (fmt != null) {
                responseFormat = fmt.format();
                responseContent = fmt.content();
                responseEscape = fmt.escape();
                responseTrim = fmt.trim();
            }
        } else {
            this.base = "/";
        }
    }

    private RequestMethod[] methodTypes(RequestMethod[] items) {
        if (items == null || items.length == 0 || (items.length == 1 && items[0] == RequestMethod.DEFAULT)) {
            if (requestMethod == RequestMethod.DEFAULT) {
                return new RequestMethod[]{RequestMethod.GET, RequestMethod.POST};
            } else {
                return new RequestMethod[]{requestMethod};
            }
        } else {
            return items;
        }
    }

    private IErrorHandler getErrHandler() {
        if (IErrorHandler.class == errHandler) {
            return app().defaults().errorHandler();
        } else {
            return app().component(errHandler);
        }
    }

    private IFormatter getParamsFormat() {
        if (IFormatter.class == paramsFormat) {
            return app().defaults().formatter();
        } else {
            return app().component(paramsFormat);
        }
    }

    private IFormatter getResponseFormat() {
        if (IFormatter.class == responseFormat) {
            return app().defaults().formatter();
        } else {
            return app().component(responseFormat);
        }
    }

    private Class<? extends IDataContent> getRequestContent(Class<? extends IDataContent> paramsContent) {
        if (IDataContent.class == paramsContent) {
            return requestContent;
        } else {
            return paramsContent;
        }
    }

    private IDataContent getParamsContent(String mime) {
        if (IDataContent.class == paramsContent) {
            return app().defaults().content(mime);
        } else {
            return app().component(paramsContent);
        }
    }

    private IDataContent getResponseContent(String mime) {
        if (IDataContent.class == responseContent) {
            return app().defaults().content(mime);
        } else {
            return app().component(responseContent);
        }
    }

    private boolean getStrictMime() {
        switch (strictMime) {
            case Use:
                return true;
            case Ignore:
                return false;
            default:
                return app().defaults().isStrictMime();
        }
    }

    private boolean getParamsEscape() {
        switch (paramsEscape) {
            case Use:
                return true;
            case Ignore:
                return false;
            default:
                return app().defaults().isEscapeControllerParams();
        }
    }

    private boolean getResponseEscape() {
        switch (responseEscape) {
            case Use:
                return true;
            case Ignore:
                return false;
            default:
                return app().defaults().isEscapeControllerResult();
        }
    }

    private boolean getParamsTrim() {
        switch (paramsTrim) {
            case Use:
                return true;
            case Ignore:
                return false;
            default:
                return app().defaults().isTrimControllerParams();
        }
    }

    private boolean getResponseTrim() {
        switch (responseTrim) {
            case Use:
                return true;
            case Ignore:
                return false;
            default:
                return app().defaults().isTrimControllerResult();
        }
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
    public String internal() {
        return context.internal();
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

    protected IUser user() throws InvalidSession {
        IUser user = app().process().session().user();
        if (user == null) {
            throw new InvalidSession();
        }
        return user;
    }

    private static class ControllerMethod implements IControllerMethod {

        private final String mapping;
        private final boolean outEscape;
        private final boolean outTrim;
        private final boolean inEscape;
        private final boolean inTrim;
        private final EnumMap<RequestMethod, RequestMethod> types = new EnumMap<>(RequestMethod.class);
        private final Class<? extends IErrorHandler> errorHanlerClass;
        private final Class<? extends IFormatter> paramsFormatClass;
        private IFormatter paramsFormatter;
        private final Class<? extends IFormatter> responseFormatClass;
        private IFormatter responseFormatter;
        private final Class<? extends IDataContent> paramsContentClass;
        private IDataContent paramsContent;
        private final Class<? extends IDataContent> responseContentClass;
        private IDataContent responseContent;
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
            this.paramsFormatClass = rqMapping.format();
            this.responseFormatClass = rsMapping == null ? IFormatter.class : rsMapping.format();
            this.secureLevel = rqMapping.secure();
            this.paramsContentClass = parent.getRequestContent(rqMapping.content());
            this.mimeStrict = Decision.Get(parent.getStrictMime(), rqMapping.strictContent());
            this.responseContentClass = rsMapping == null || rsMapping.content().length == 0 ? IDataContent.class : rsMapping.content()[0];
            this.outEscape = Decision.Get(parent.getResponseEscape(), rsMapping == null ? null : rsMapping.escape());
            this.outTrim = Decision.Get(parent.getResponseTrim(), rsMapping == null ? null : rsMapping.trim());
            this.inEscape = Decision.Get(parent.getParamsEscape(), rqMapping.escape());
            this.inTrim = Decision.Get(parent.getParamsTrim(), rqMapping.trim());

            RequestMethod[] mTypes = parent.methodTypes(rqMapping.type());
            for (RequestMethod item : mTypes) {
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
                    errorHandler = parent.getErrHandler();
                } else {
                    errorHandler = parent.app().component(errorHanlerClass);
                }
            }
            return errorHandler;
        }

        private IFormatter getInFormatter() {
            if (paramsFormatter == null) {
                if (paramsFormatClass == IFormatter.class) {
                    paramsFormatter = parent.getParamsFormat();
                } else {
                    paramsFormatter = parent.app().component(paramsFormatClass);
                }
            }
            return paramsFormatter;
        }

        @Override
        public IFormatter getOutFormat() {
            if (responseFormatter == null) {
                if (responseFormatClass == IFormatter.class) {
                    responseFormatter = parent.getResponseFormat();
                } else {
                    responseFormatter = parent.app().component(responseFormatClass);
                }
            }
            return responseFormatter;
        }

        private IDataContent getInContent(String mime) {
            if (paramsContent == null) {
                if (paramsContentClass == IDataContent.class) {
                    return parent.getParamsContent(mime);
                } else {
                    paramsContent = parent.app().component(paramsContentClass);
                }
            }
            return paramsContent;
        }

        private IDataContent getOutContent() {
            if (responseContent == null) {
                if (responseContentClass == IDataContent.class) {
                    responseContent = parent.getResponseContent(null);
                } else {
                    responseContent = parent.app().component(responseContentClass);
                }
            }
            return responseContent;
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

                IProcessSnapshot snapshot = parent.app().process().context().save();

                try {
                    if (mimeStrict && paramsContentClass != IDataContent.class) {
                        if (!getInContent(null).contentType("UTF-8").getMimeType().equals(contentType.getMimeType())) {
                            throw new InvalidContentType(contentType.getMimeType());
                        }
                    }

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
                                } else if (RequestData.class.isAssignableFrom(param.type())) {
                                    RequestData data = new RequestData(req, parent.app());
                                    args[i] = param.isArray() ? new RequestData[]{data} : data;
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

                            doDefaults(httpResponse);

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

                                    for (Cookie cookie : response.getCookies()) {
                                        httpResponse.addCookie(cookie);
                                    }
                                    flush(httpResponse, response.getBody(), log, response.getContent());
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
                } catch (Throwable t) {
                    Throwable error = new MethodFailed(name(), t);
                    log.error(error);
                    doDefaults(httpResponse);
                    parent.app().defaults().errorHandler().flush(this, mapping, httpResponse, getOutFormat(), t);
                } finally {
                    parent.app().process().context().restore(snapshot);
                    parent.runCount.decrementAndGet();
                    parent.app().afterRequest(this);
                }
            } else {
                throw new ComponentNotActive(parent.name());
            }
        }

        private void doDefaults(HttpServletResponse httpResponse) {
            for (Map.Entry<String, String> entry : parent.app().defaults().headers().entrySet()) {
                httpResponse.addHeader(entry.getKey(), entry.getValue());
            }
        }

        private Object processParamValue(RequestParameter param, String value, ContentType contentType) throws Exception {
            try {
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
            } catch (IOException e) {
                throw new ParameterInvalidException(param.name(), e);
            }
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
            return parent.name() + "." + method.getName();
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
