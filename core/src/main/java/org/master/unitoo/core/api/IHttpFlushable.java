/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.api;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Andrey
 */
public interface IHttpFlushable {

    void flush(HttpServletResponse response) throws IOException;

}
