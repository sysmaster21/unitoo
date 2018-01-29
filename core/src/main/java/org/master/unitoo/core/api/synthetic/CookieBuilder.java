/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.api.synthetic;

import java.util.ArrayList;
import javax.servlet.http.Cookie;
import org.master.unitoo.core.api.ICookieBuilder;

/**
 *
 * @author Andrey
 */
public class CookieBuilder implements ICookieBuilder {

    private final ArrayList<Cookie> cookies;
    private final Cookie cookie;

    public CookieBuilder(ArrayList<Cookie> cookies, String name, String value) {
        this.cookies = cookies;
        this.cookie = new Cookie(name, value);
    }

    @Override
    public ICookieBuilder comment(String text) {
        cookie.setComment(text);
        return this;
    }

    @Override
    public ICookieBuilder domain(String pattern) {
        cookie.setDomain(pattern);
        return this;
    }

    @Override
    public ICookieBuilder maxAge(int expire) {
        cookie.setMaxAge(expire);
        return this;
    }

    @Override
    public ICookieBuilder path(String uri) {
        cookie.setPath(uri);
        return this;
    }

    @Override
    public ICookieBuilder secure(boolean flag) {
        cookie.setSecure(flag);
        return this;
    }

    @Override
    public ICookieBuilder httpOnly(boolean flag) {
        cookie.setHttpOnly(flag);
        return this;
    }

    @Override
    public ICookieBuilder version(int v) {
        cookie.setVersion(v);
        return this;
    }

    @Override
    public void add() {
        cookies.add(cookie);
    }

}
