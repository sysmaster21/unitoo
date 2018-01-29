/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.api;

import java.util.List;
import java.util.Map;
import javax.servlet.http.Cookie;

/**
 *
 * @author Andrey
 * @param <T>
 */
public interface IResponse<T> {

    String getHeaderValue(String name);

    List<String> getHeaderValues(String name);

    Map<String, List<String>> getHeaders();

    IDataContent content();

    Iterable<Cookie> cookies();

    T body();

}
