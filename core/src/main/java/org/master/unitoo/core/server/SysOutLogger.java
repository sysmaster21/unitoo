/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.server;

import org.master.unitoo.core.api.ILogger;

/**
 *
 * @author Andrey
 */
public class SysOutLogger implements ILogger {

    @Override
    public void error(Throwable t) {
        t.printStackTrace(System.err);
    }

    @Override
    public void error(String s, Throwable t) {
        System.err.print("ERROR > ");
        System.err.println(s);
        t.printStackTrace(System.err);
    }

    @Override
    public void error(String s) {
        System.err.print("ERROR > ");
        System.err.println(s);
    }

    @Override
    public void warning(Throwable t) {
        t.printStackTrace(System.out);
    }

    @Override
    public void warning(String s, Throwable t) {
        System.out.print("WARN  > ");
        System.out.println(s);
        t.printStackTrace(System.out);
    }

    @Override
    public void warning(String s) {
        System.out.print("WARN  > ");
        System.out.println(s);
    }

    @Override
    public void info(String s) {
        System.out.print("INFO  > ");
        System.out.println(s);
    }

    @Override
    public void debug(String s) {
        System.out.print("DEBUG > ");
        System.out.println(s);
    }

    @Override
    public boolean getDebugState() {
        return true;
    }

}
