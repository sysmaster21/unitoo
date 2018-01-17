/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.server;

import java.util.ArrayList;
import org.master.unitoo.core.api.ILogger;
import org.master.unitoo.core.types.LogLevel;

/**
 *
 * @author Andrey
 */
public class LogCache implements ILogger {

    private final ArrayList<LogItem> items = new ArrayList<>();
    private ILogger logger;

    public LogCache(ILogger logger) {
        setTarget(logger);
    }

    public final void setTarget(ILogger logger) {
        this.logger = logger;
    }

    @Override
    public void error(Throwable t) {
        addMessage(LogLevel.ERROR, null, t);
    }

    @Override
    public void error(String s, Throwable t) {
        addMessage(LogLevel.ERROR, s, t);
    }

    @Override
    public void error(String s) {
        addMessage(LogLevel.ERROR, s, null);
    }

    @Override
    public void warning(Throwable t) {
        addMessage(LogLevel.WARNING, null, t);
    }

    @Override
    public void warning(String s, Throwable t) {
        addMessage(LogLevel.WARNING, s, t);
    }

    @Override
    public void warning(String s) {
        addMessage(LogLevel.WARNING, s, null);
    }

    @Override
    public void info(String s) {
        addMessage(LogLevel.INFO, s, null);
    }

    @Override
    public void debug(String s) {
        addMessage(LogLevel.DEBUG, s, null);
    }

    @Override
    public boolean getDebugState() {
        return logger.getDebugState();
    }

    private synchronized void addMessage(LogLevel level, String msg, Throwable error) {
        items.add(new LogItem(level, msg, error));
    }

    public void flush() {
        for (LogItem item : items) {
            switch (item.level) {
                case DEBUG:
                    logger.debug(item.msg);
                    break;
                case INFO:
                    logger.info(item.msg);
                    break;
                case WARNING:
                    if (item.error == null) {
                        logger.warning(item.msg);
                    } else if (item.msg == null) {
                        logger.warning(item.error);
                    } else {
                        logger.warning(item.msg, item.error);
                    }
                    break;
                case ERROR:
                    if (item.msg == null) {
                        logger.error(item.error);
                    } else if (item.error == null) {
                        logger.error(item.msg);
                    } else {
                        logger.error(item.msg, item.error);
                    }
                    break;
            }
        }
    }

    private class LogItem {

        private final LogLevel level;
        private final String msg;
        private final Throwable error;

        public LogItem(LogLevel level, String msg, Throwable error) {
            this.level = level;
            this.msg = msg;
            this.error = error;
        }

    }

}
