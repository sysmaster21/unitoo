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
public enum RunnableState implements ICodedEnum<String> {

    Stopped("p"),
    Init("i"),
    Waiting("w"),
    Running("r"),
    Killing("k"),
    Stopping("s");

    private final String code;

    private RunnableState(String code) {
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

}
