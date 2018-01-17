/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.impl;

import org.master.unitoo.core.api.IExternalStorage;
import org.master.unitoo.core.api.annotation.Component;
import org.master.unitoo.core.api.components.I18nManager;
import org.master.unitoo.core.api.components.ILanguage;
import org.master.unitoo.core.base.BaseExternalValueManager;
import org.master.unitoo.core.types.ComponentType;

/**
 *
 * @author Andrey
 */
@Component("langs-empty")
public class EmptyI18nManager extends BaseExternalValueManager<ILanguage, Object> implements I18nManager {

    @Override
    public IExternalStorage<ILanguage, Object> register(ILanguage source) {
        return new EmptyStorage<>(source);
    }

    @Override
    public void bootComplete() {
    }

    @Override
    public ComponentType type() {
        return ComponentType.I18nManager;
    }

}
