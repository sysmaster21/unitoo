/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.impl;

/**
 *
 * @author Andrey
 */
public class LabelItem {

    private final String key;
    private String value;
    private final String def;
    private boolean changed = false;

    public LabelItem(String key, String value, String def) {
        this.key = key;
        this.value = value;
        this.def = def;
    }

    public String key() {
        return key;
    }

    public String def() {
        return def;
    }

    public String value() {
        return value;
    }

    public void value(String value) {
        this.value = value;
        changed = true;
    }

    public void load(String value) {
        this.value = value;
    }

    public boolean isChanged() {
        return changed;
    }

    public void changed(boolean changed) {
        this.changed = changed;
    }

}
