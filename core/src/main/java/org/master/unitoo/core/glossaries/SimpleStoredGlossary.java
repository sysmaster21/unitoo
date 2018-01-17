/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.glossaries;

/**
 *
 * @author Andrey
 */
public abstract class SimpleStoredGlossary<T extends SimpleStoredGlossaryItem> extends StoredGlossary<String, T> {

    @Override
    public Class<String> codeType() {
        return String.class;
    }

}
