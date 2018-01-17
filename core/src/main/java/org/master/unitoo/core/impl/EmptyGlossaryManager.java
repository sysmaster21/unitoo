/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.impl;

import org.master.unitoo.core.api.IExternalStorage;
import org.master.unitoo.core.api.IGlossaryItem;
import org.master.unitoo.core.api.annotation.Component;
import org.master.unitoo.core.api.components.IGlossaryManager;
import org.master.unitoo.core.api.components.IStoredGlossary;
import org.master.unitoo.core.base.BaseExternalValueManager;
import org.master.unitoo.core.types.ComponentType;

/**
 *
 * @author Andrey
 */
@Component("glossary-empty")
public class EmptyGlossaryManager extends BaseExternalValueManager<IStoredGlossary, IGlossaryItem> implements IGlossaryManager {

    @Override
    public IExternalStorage<IStoredGlossary, IGlossaryItem> register(IStoredGlossary source) {
        return new EmptyStorage<>(source);
    }

    @Override
    public void bootComplete() {
    }

    @Override
    public ComponentType type() {
        return ComponentType.GlossaryManager;
    }

}
