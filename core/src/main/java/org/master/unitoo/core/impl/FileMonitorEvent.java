/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.impl;

import org.master.unitoo.core.api.util.IMonitorEvent;
import org.master.unitoo.core.types.CRUD;
import org.master.unitoo.core.types.DateTime;

/**
 *
 * @author Andrey
 */
public class FileMonitorEvent implements IMonitorEvent {

    private String fileName;
    private DateTime eventTime;
    private CRUD operation;

    public FileMonitorEvent(String fileName, DateTime eventTime, CRUD operation) {
        this.fileName = fileName;
        this.eventTime = eventTime;
        this.operation = operation;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public DateTime getEventTime() {
        return eventTime;
    }

    public void setEventTime(DateTime eventTime) {
        this.eventTime = eventTime;
    }

    public CRUD getOperation() {
        return operation;
    }

    public void setOperation(CRUD operation) {
        this.operation = operation;
    }

}
