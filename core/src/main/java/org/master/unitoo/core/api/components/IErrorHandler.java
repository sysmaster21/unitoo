/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.api.components;

import javax.servlet.http.HttpServletResponse;
import org.master.unitoo.core.api.IComponent;
import org.master.unitoo.core.api.IControllerMethod;

/**
 *
 * @author Andrey
 */
public interface IErrorHandler extends IComponent {

    void flush(IControllerMethod method, String mapping, HttpServletResponse response, IFormatter formatter, Throwable error);

}
