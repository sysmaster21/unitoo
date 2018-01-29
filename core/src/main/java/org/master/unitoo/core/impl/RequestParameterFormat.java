/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.impl;

import org.master.unitoo.core.api.IApplication;
import org.master.unitoo.core.api.components.IFormatter;
import org.master.unitoo.core.api.IDataContent;

/**
 *
 * @author Andrey
 */
public class RequestParameterFormat {

    private final Class<? extends IDataContent> contentClass;
    private IDataContent content;
    private final Class<? extends IFormatter> formatClass;
    private IFormatter format;
    private final boolean escaping;
    private final boolean trim;
    private final IApplication app;

    public RequestParameterFormat(Class<? extends IDataContent> contentClass, Class<? extends IFormatter> formatClass, boolean escaping, boolean trim, IApplication app) {
        this.contentClass = contentClass;
        this.formatClass = formatClass;
        this.escaping = escaping;
        this.trim = trim;
        this.app = app;
    }

    public boolean escape() {
        return escaping;
    }

    public boolean trim() {
        return trim;
    }

    public IDataContent content(IDataContent parent) {
        if (content == null) {
            if (contentClass == null || contentClass == IDataContent.class) {
                content = parent;
            } else {
                content = app.component(contentClass);
            }
        }
        return content;
    }

    public IFormatter format(IFormatter parent) {
        if (format == null) {
            if (formatClass == null || formatClass == IFormatter.class) {
                format = parent;
            } else {
                format = app.component(formatClass);
            }
        }
        return format;
    }
}
