/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.api.synthetic;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import org.apache.http.entity.ContentType;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.FileItemFactory;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.master.unitoo.core.UniToo;
import org.master.unitoo.core.api.IApplication;
import org.master.unitoo.core.api.IBusinessObject;
import org.master.unitoo.core.api.IDataContent;
import org.master.unitoo.core.api.components.IFormatter;

/**
 *
 * @author Andrey
 */
public class RequestData {

    private final HttpServletRequest http;
    private final IApplication app;
    private final Map<String, String> cookies = new HashMap<>();
    private final Map<String, List<FileItem>> multipart = new HashMap<>();
    private final ContentType contentType;

    public RequestData(HttpServletRequest http, IApplication app) throws IOException {
        this.http = http;
        this.app = app;

        this.contentType = http.getContentType() == null || http.getContentType().isEmpty() ? ContentType.TEXT_HTML : ContentType.parse(http.getContentType());

        Cookie[] cList = http.getCookies();
        if (cList != null) {
            for (Cookie cookie : cList) {
                cookies.put(cookie.getName(), cookie.getValue());
            }
        }

        if ("multipart/form-data".equals(contentType.getMimeType())) {
            FileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload upload = new ServletFileUpload(factory);
            try {
                Iterator<List<FileItem>> it = upload.parseParameterMap(http).values().iterator();
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
                    }
                }
            } catch (FileUploadException e) {
                throw new IOException(e);
            }
        }
    }

    public String getHeaderValue(String name) {
        return http.getHeader(name);
    }

    public List<String> getHeaderValues(String name) {
        return Collections.list(http.getHeaders(name));
    }

    public Map<String, List<String>> getHeaders() {
        Enumeration<String> headers = http.getHeaderNames();
        Map<String, List<String>> map = new HashMap<>();
        while (headers.hasMoreElements()) {
            String header = headers.nextElement();
            map.put(header, getHeaderValues(header));
        }
        return map;
    }

    public ContentType getContentType() {
        String ct = http.getContentType();
        return ct == null || ct.isEmpty() ? null : ContentType.parse(ct);
    }

    public String getCookieValue(String name) {
        return cookies.get(name);
    }

    public Map<String, String> getCookies() {
        return cookies;
    }

    public ContentType getParamType(String name) {
        List<FileItem> items = multipart.get(name);
        if (items != null && items.size() > 0) {
            return ContentType.parse(items.get(0).getContentType());
        } else {
            return ContentType.TEXT_PLAIN;
        }

    }

    public String getParamTextValue(String name) {
        List<FileItem> items = multipart.get(name);
        if (items != null && items.size() > 0) {
            FileItem item = items.get(0);
            if (item.isFormField()) {
                return item.getString();
            }
        }
        return http.getParameter(name);
    }

    public byte[] getParamBinaryValue(String name) {
        List<FileItem> items = multipart.get(name);
        if (items != null && items.size() > 0) {
            FileItem item = items.get(0);
            if (item.isFormField()) {
                return item.get();
            }
        }
        return null;
    }

    public InputStream getParamStreamValue(String name) throws IOException {
        List<FileItem> items = multipart.get(name);
        if (items != null && items.size() > 0) {
            FileItem item = items.get(0);
            if (item.isFormField()) {
                return item.getInputStream();
            }
        }
        return null;
    }

    public List<String> getParamTextValues(String name) {
        ArrayList<String> list = new ArrayList<>();
        List<FileItem> items = multipart.get(name);
        if (items != null && items.size() > 0) {
            FileItem item = items.get(0);
            if (item.isFormField()) {
                list.add(item.getString());
            }
        }
        list.addAll(Arrays.asList(http.getParameterValues(name)));
        return list;
    }

    public Iterable<String> getParamNames() {
        ArrayList<String> list = new ArrayList<>();
        Enumeration<String> params = http.getParameterNames();
        while (params.hasMoreElements()) {
            String param = params.nextElement();
            list.add(param);
        }

        list.addAll(multipart.keySet());
        return list;
    }

    public <T> T getParamValue(String name, Class<T> clazz) throws IOException {
        return getParamValue(name, clazz, null, null);
    }

    public <T> T getParamValue(String name, Class<T> clazz, Class<? extends IFormatter> formatClass, Class<? extends IDataContent> contentClass) throws IOException {
        IFormatter formatter = formatClass == null ? app.defaults().formatter() : app.component(formatClass);
        IDataContent content = contentClass == null ? app.defaults().content(getParamType(name).getMimeType()) : app.component(contentClass);
        if (IBusinessObject.class.isAssignableFrom(clazz)) {
            InputStream stream = getParamStreamValue(name);
            return stream == null ? null : (T) content.deserialize((Class<IBusinessObject>) clazz, stream, formatter);
        } else {
            return formatter.parse(getParamTextValue(name), clazz);
        }
    }

    public List<String> getParamValues(String name) {
        return Arrays.asList(http.getParameterValues(name));
    }

    public Map<String, List<String>> getParams() {
        Map<String, List<String>> map = new HashMap<>();
        Enumeration<String> params = http.getParameterNames();
        while (params.hasMoreElements()) {
            String param = params.nextElement();
            map.put(param, getParamValues(param));
        }
        return map;
    }

    public InputStream getBodyAsStream() throws IOException {
        return http.getInputStream();
    }

    public byte[] getBodyAsBytes() throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        UniToo.Copy(getBodyAsStream(), buf);
        return buf.toByteArray();
    }

}
