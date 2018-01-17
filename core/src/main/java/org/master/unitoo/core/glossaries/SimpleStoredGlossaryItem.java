/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.glossaries;

import org.master.unitoo.core.api.components.IStoredGlossary;

/**
 *
 * @author Andrey
 */
public class SimpleStoredGlossaryItem extends StoredGlossaryItem<String> {

    public SimpleStoredGlossaryItem(String code, IStoredGlossary<String, ? extends StoredGlossaryItem<String>> parent) {
        super(code, parent);
    }

}
