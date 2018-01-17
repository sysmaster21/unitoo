/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.glossaries;

import org.master.unitoo.core.base.BaseGlossary;

/**
 *
 * @author Andrey
 * @param <T>
 */
public abstract class EnumGlossary<T extends Enum> extends BaseGlossary<T, GlossaryItem<T>> {

    private final Class clazz;

    public EnumGlossary(Class<? extends Enum> clazz) {
        this.clazz = clazz;
    }

    @Override
    protected void load(GlossaryLoader<GlossaryItem<T>> loader) {
        for (Object enumValue : clazz.getEnumConstants()) {
            GlossaryItem item = new GlossaryItem((T) enumValue, ((T) enumValue).name(), this);
            loader.add(item);
        }
    }

}
