/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.impl;

import org.master.unitoo.core.api.IApplication;
import org.master.unitoo.core.api.components.ILanguage;

/**
 *
 * @author Andrey
 */
public final class Label {

    private final IApplication app;
    private final String key;
    private final String def;
    private final boolean escape;
    private final boolean trim;

    public Label(IApplication app, String key, String def, boolean escape, boolean trim) {
        this.app = app;
        this.key = key;
        this.def = def;
        this.escape = escape;
        this.trim = trim;
    }

    public String key() {
        return key;
    }

    public String value() {
        return value(app.process().language());
    }

    public String value(ILanguage language) {
        String value = language.label(key);
        if (escape) {
            value = org.apache.commons.text.StringEscapeUtils.escapeHtml4(value);
        }

        if (trim) {
            value = value.trim();
        }

        return value;
    }

    public String value(String code) {
        ILanguage lang = app.language(code);
        lang = lang == null ? app.defaults().language() : lang;
        return value(lang);
    }

    public String def() {
        return def;
    }

    public boolean escape() {
        return escape;
    }

    public boolean trim() {
        return trim;
    }

}
