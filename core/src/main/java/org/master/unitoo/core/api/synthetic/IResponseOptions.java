/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.api.synthetic;

/**
 *
 * @author Andrey
 */
public interface IResponseOptions {

    void setHeader(String name, String value);

    void content(String type);

    void content(String type, String encoding);

    void addCookie(String name, String value);

    void addCookie(String name, String value, boolean secured, boolean httpOnly);

    ICookieBuilder buildCookie(String name, String value);

}
