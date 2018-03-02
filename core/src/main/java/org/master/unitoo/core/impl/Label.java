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
    private final String extKey;
    private final String def;

    public Label(IApplication app, String key, String extKey, String def) {
        this.app = app;
        this.key = key;
        this.def = def;
        this.extKey = extKey;
    }

    public String extKey() {
        return extKey;
    }

    public String key() {
        return key;
    }

    public String value() {
        return value(app.process().language());
    }

    public String value(ILanguage language) {
        String value = language.label(key);
        return value;
    }

    public String value(String code) {
        ILanguage lang = app.component(ILanguage.class, code);
        lang = lang == null ? app.defaults().language() : lang;
        return value(lang);
    }

    public String def() {
        return def;
    }

}
