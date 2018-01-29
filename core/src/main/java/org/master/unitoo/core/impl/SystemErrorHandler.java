/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.http.HttpServletResponse;
import org.master.unitoo.core.api.IControllerMethod;
import org.master.unitoo.core.api.annotation.Component;
import org.master.unitoo.core.api.components.IFormatter;
import org.master.unitoo.core.api.components.mappers.HTML_CONTENT;
import org.master.unitoo.core.base.BaseErrorHandler;
import org.master.unitoo.core.errors.MethodNotAllowed;
import org.master.unitoo.core.errors.MethodNotFound;
import org.master.unitoo.core.api.IDataContent;

/**
 *
 * @author Andrey
 */
@Component("core")
public class SystemErrorHandler extends BaseErrorHandler {

    @Override
    protected void flushError(IControllerMethod method, String mapping, HttpServletResponse response, IFormatter formatter, Throwable error) throws Exception {
        if (error instanceof MethodNotAllowed) {
            response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        } else if (error instanceof MethodNotFound) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        StringWriter buf = new StringWriter();
        error.printStackTrace(new PrintWriter(buf));
        String text = buf.toString().replace("\n", "<br>");
        response.getWriter().print(text);
    }

    @Override
    protected IDataContent content() {
        return app().component(HTML_CONTENT.class);
    }

}
