/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.base;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import ch.qos.logback.core.util.FileSize;
import java.io.File;
import org.master.unitoo.core.api.IApplication;
import org.master.unitoo.core.api.IBootInfo;
import org.master.unitoo.core.api.ILogger;
import org.master.unitoo.core.api.components.ILoggerFactory;
import org.master.unitoo.core.types.ComponentContext;
import org.master.unitoo.core.types.ComponentType;
import org.master.unitoo.core.types.LogLevel;
import org.master.unitoo.core.types.RunnableState;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Andrey
 */
public abstract class BaseLogger implements ILoggerFactory {

    private ComponentContext context;
    private Appender<ILoggingEvent> appender;
    private RunnableState state = RunnableState.Stopped;

    @Override
    public RunnableState state() {
        return state;
    }

    @Override
    public ComponentType type() {
        return ComponentType.Logger;
    }

    @Override
    public void init(ComponentContext context) {
        state = RunnableState.Init;
        this.context = context;
    }

    @Override
    public ILogger log(Class clazz) {
        Logger logger = (Logger) LoggerFactory.getLogger(clazz);
        logger.addAppender(appender);
        logger.setLevel(level().level());
        logger.setAdditive(false);
        return new LoggerInstance(logger, LogLevel.DEBUG.equals(level()));
    }

    @Override
    public ILogger log(String name) {
        Logger logger = (Logger) LoggerFactory.getLogger(name);
        logger.addAppender(appender);
        logger.setLevel(level().level());
        logger.setAdditive(false);
        return new LoggerInstance(logger, LogLevel.DEBUG.equals(level()));
    }

    @Override
    public String name() {
        return getClass().getName();
    }

    @Override
    public String description() {
        return context.description();
    }

    @Override
    public String version() {
        return context.version();
    }

    @Override
    public String internal() {
        return context.internal();
    }

    @Override
    public IApplication app() {
        return context.application();
    }

    @Override
    public IBootInfo boot() {
        return context.boot();
    }

    private File curPath() {
        String pt = path();
        if (pt.startsWith("~")) {
            String home = context.application().homeFolder();
            home = home == null
                    ? System.getProperty("catalina.base") + File.separator + "logs" + File.separator + app().appName()
                    : home + File.separator + "logs";
            home = home.endsWith(File.separator) ? home.substring(1) : home;
            pt = home + pt.substring(1);
        }
        return new File(pt);
    }

    protected abstract String pattern();

    protected abstract LogLevel level();

    protected abstract String path();

    protected abstract String file();

    protected abstract String rolling();

    protected abstract int maxHistory();

    protected abstract long totalSize();

    protected abstract long maxFile();

    protected abstract boolean cleanOnStart();

    protected void createLogger() {
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();

        PatternLayoutEncoder ple = new PatternLayoutEncoder();
        ple.setPattern(pattern());
        ple.setContext(lc);
        ple.start();

        String fname = file();
        if (fname == null || fname.trim().isEmpty()) {
            fname = "log";
        }
        if (!fname.contains(".")) {
            fname = fname + ".log";
        }
        File logFile = new File(curPath(), fname);

        String rollingMask = rolling();

        if (rollingMask == null || rollingMask.trim().isEmpty()) {
            RollingFileAppender<ILoggingEvent> fileAppender = new RollingFileAppender<>();

            TimeBasedRollingPolicy<ILoggingEvent> rollingPolicy;
            if (maxFile() > 0) {
                rollingPolicy = new SizeAndTimeBasedRollingPolicy<>();
                ((SizeAndTimeBasedRollingPolicy) rollingPolicy).setMaxFileSize(new FileSize(maxFile()));
            } else {
                rollingPolicy = new TimeBasedRollingPolicy<>();
            }

            rollingPolicy.setContext(lc);
            rollingPolicy.setParent(fileAppender);
            rollingPolicy.setFileNamePattern(rollingMask);
            if (maxHistory() > 0) {
                rollingPolicy.setMaxHistory(maxHistory());
            }
            if (totalSize() > 0) {
                rollingPolicy.setTotalSizeCap(new FileSize(totalSize()));
            }
            rollingPolicy.setCleanHistoryOnStart(cleanOnStart());
            rollingPolicy.start();

            fileAppender.setFile(logFile.getPath());
            fileAppender.setEncoder(ple);
            fileAppender.setContext(lc);
            fileAppender.setRollingPolicy(rollingPolicy);
            fileAppender.start();
            this.appender = fileAppender;
        } else {
            FileAppender<ILoggingEvent> fileAppender = new FileAppender<>();
            fileAppender.setFile(logFile.getPath());
            fileAppender.setEncoder(ple);
            fileAppender.setContext(lc);
            fileAppender.start();
            this.appender = fileAppender;
        }

    }

    @Override
    public void prepare() {
    }

    @Override
    public void start() {
        createLogger();
        state = RunnableState.Running;
    }

    @Override
    public String info() {
        return path();
    }

    @Override
    public void destroy() {
    }

    @Override
    public void reject() {
    }

    private static class LoggerInstance implements ILogger {

        private final Logger logger;
        private final boolean debug;

        public LoggerInstance(Logger logger, boolean debug) {
            this.logger = logger;
            this.debug = debug;
        }

        @Override
        public void error(Throwable t) {
            logger.error("", t);
        }

        @Override
        public void error(String s, Throwable t) {
            logger.error(s, t);
        }

        @Override
        public void error(String s) {
            logger.error(s);
        }

        @Override
        public void warning(Throwable t) {
            logger.warn("", t);
        }

        @Override
        public void warning(String s, Throwable t) {
            logger.warn(s, t);
        }

        @Override
        public void warning(String s) {
            logger.warn(s);
        }

        @Override
        public void info(String s) {
            logger.info(s);
        }

        @Override
        public void debug(String s) {
            logger.debug(s);
        }

        @Override
        public boolean getDebugState() {
            return debug;
        }

    }
}
