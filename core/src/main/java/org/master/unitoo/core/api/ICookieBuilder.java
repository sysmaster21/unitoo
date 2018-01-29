/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.api;

/**
 *
 * @author Andrey
 */
public interface ICookieBuilder {

    ICookieBuilder comment(String text);

    ICookieBuilder domain(String pattern);

    ICookieBuilder maxAge(int expire);

    ICookieBuilder path(String uri);

    ICookieBuilder secure(boolean flag);

    ICookieBuilder httpOnly(boolean flag);

    ICookieBuilder version(int v);

    void add();

}
