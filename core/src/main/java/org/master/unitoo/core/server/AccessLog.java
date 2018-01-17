/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.server;

import org.master.unitoo.core.api.annotation.Component;
import org.master.unitoo.core.base.BaseLogger;
import org.master.unitoo.core.types.LogLevel;
import org.master.unitoo.core.api.annotation.Attribute;

/**
 *
 * @author Andrey
 */
@Component("log")
public class AccessLog extends BaseLogger {

    @Attribute(name = "pattern", value = "[%d{yyyy-MM-dd HH:mm:ss.SSS}] %-5level %-28.28thread %-64.64logger{64} %msg %ex%n")
    public Setting<String> pattern;

    @Attribute(name = "level", value = "INFO")
    public Setting<LogLevel> level;

    @Attribute(name = "path", value = "~/")
    public Setting<String> path;

    @Attribute(name = "file", value = "access.log")
    public Setting<String> file;

    @Attribute(name = "rollFile", value = "{yyyy-MM-dd}-access.zip")
    public Setting<String> rollFile;

    @Attribute(name = "maxHistory", value = "0")
    public Setting<Integer> maxHistory;

    @Attribute(name = "totalSize", value = "0")
    public Setting<Long> totalSize;

    @Attribute(name = "maxFile", value = "0")
    public Setting<Long> maxFile;

    @Attribute(name = "cleanOnStart", value = "true")
    public Setting<Boolean> cleanOnStart;

    @Override
    protected String pattern() {
        return pattern.val();
    }

    @Override
    protected LogLevel level() {
        return level.val();
    }

    @Override
    public String file() {
        return file.val();
    }

    @Override
    public String path() {
        return path.val();
    }

    @Override
    protected String rolling() {
        return rollFile.val();
    }

    @Override
    protected int maxHistory() {
        return maxHistory.val() == null ? 0 : maxHistory.val();
    }

    @Override
    protected long totalSize() {
        return totalSize.val() == null ? 0 : totalSize.val();
    }

    @Override
    protected long maxFile() {
        return maxFile.val() == null ? 0 : maxFile.val();
    }

    @Override
    protected boolean cleanOnStart() {
        return cleanOnStart.val() == null ? false : cleanOnStart.val();
    }

}
