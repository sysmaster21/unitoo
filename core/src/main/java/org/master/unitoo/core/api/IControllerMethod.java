/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.master.unitoo.core.api.components.IController;
import org.master.unitoo.core.api.components.IErrorHandler;
import org.master.unitoo.core.api.components.IFormatter;
import org.master.unitoo.core.types.RequestMethod;
import org.master.unitoo.core.types.SecureLevel;

/**
 *
 * @author Andrey
 */
public interface IControllerMethod {

    String name();

    String mapping();

    Iterable<RequestMethod> types();

    IController controller();

    IErrorHandler errors();

    SecureLevel secureLevel();

    void process(RequestMethod type, String fromIP, HttpServletRequest req, HttpServletResponse resp, ILogger log) throws Exception;

    IFormatter getOutFormat();
    
    String descr();

}
