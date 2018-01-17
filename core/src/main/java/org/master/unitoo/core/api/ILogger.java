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
public interface ILogger {

    void error(Throwable t);

    void error(String s, Throwable t);

    void error(String s);

    void warning(Throwable t);

    void warning(String s, Throwable t);

    void warning(String s);

    void info(String s);

    void debug(String s);

    boolean getDebugState();
}
