/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.types;

import org.master.unitoo.core.api.ICodedEnum;

/**
 *
 * @author Andrey
 */
public enum CRUD implements ICodedEnum<String> {

    Create("C"),
    Read("R"),
    Update("U"),
    Delete("D");

    private final String code;

    private CRUD(String code) {
        this.code = code;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public Class<String> type() {
        return String.class;
    }

    @Override
    public boolean is(String code) {
        return code != null && code.equals(this.code);
    }

}
