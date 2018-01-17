/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.types;

import ch.qos.logback.classic.Level;

/**
 *
 * @author Andrey
 */
public enum LogLevel {

    DEBUG(Level.DEBUG),
    INFO(Level.INFO),
    WARNING(Level.WARN),
    ERROR(Level.ERROR);

    private final Level level;

    private LogLevel(Level level) {
        this.level = level;
    }

    public Level level() {
        return this.level;
    }

}
