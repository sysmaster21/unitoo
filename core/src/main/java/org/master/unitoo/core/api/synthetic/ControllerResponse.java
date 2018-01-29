/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.api.synthetic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.http.Cookie;
import org.master.unitoo.core.api.ICookieBuilder;
import org.master.unitoo.core.api.IResponse;
import org.master.unitoo.core.api.IDataContent;

/**
 *
 * @author Andrey
 * @param <T>
 */
public class ControllerResponse<T> implements IResponse<T> {

    private final Map<String, List<String>> headers = new ConcurrentHashMap<>();
    private final ArrayList<Cookie> cookies = new ArrayList<>();
    private IDataContent content;
    private T body;

    public void setHeaderValues(String name, String[] values) {
        List<String> list = Arrays.asList(values);
        list = headers.putIfAbsent(name, list);
        if (list != null) {
            list.addAll(Arrays.asList(values));
        }
    }

    public void setHeaderValue(String name, String value) {
        List<String> list = new ArrayList<>();
        list.add(value);
        list = headers.putIfAbsent(name, list);
        if (list != null) {
            list.add(value);
        }
    }

    @Override
    public String getHeaderValue(String name) {
        List<String> list = headers.get(name);
        return list == null
                ? null
                : list.isEmpty() ? null : list.get(0);

    }

    @Override
    public List<String> getHeaderValues(String name) {
        return headers.get(name);
    }

    @Override
    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public void content(IDataContent content) {
        this.content = content;
    }

    @Override
    public IDataContent content() {
        return content;
    }

    public void addCookie(String name, String value) {
        cookies.add(new Cookie(name, value));
    }

    public void addCookie(String name, String value, boolean secured, boolean httpOnly) {
        Cookie cookie = new Cookie(name, value);
        cookie.setSecure(secured);
        cookie.setHttpOnly(httpOnly);
        cookies.add(cookie);
    }

    @Override
    public Iterable<Cookie> cookies() {
        return cookies;
    }

    public ICookieBuilder buildCookie(String name, String value) {
        return new CookieBuilder(cookies, name, value);
    }

    @Override
    public T body() {
        return body;
    }

    public void body(T body) {
        this.body = body;
    }

}
