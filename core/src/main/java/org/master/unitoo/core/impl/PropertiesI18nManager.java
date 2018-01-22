/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.impl;

import java.io.File;
import org.master.unitoo.core.api.annotation.Component;
import org.master.unitoo.core.api.components.I18nManager;
import org.master.unitoo.core.api.components.ILanguage;
import org.master.unitoo.core.types.ComponentType;

/**
 *
 * @author Andrey
 */
@Component("langs-props")
public class PropertiesI18nManager extends PropertiesExternalValuesManager<ILanguage, Object> implements I18nManager {

    @Override
    protected LangProps create(ILanguage source) {
        File parent = new File(app().homeFolder() + File.separator + "i18n");
        parent.mkdirs();
        return new LangProps(parent, this, source);
    }

    @Override
    public ComponentType type() {
        return ComponentType.ConfigManager;
    }

    @Override
    public Object getItemCode(Object item) {
        return item;
    }

    protected static class LangProps extends PropertiesStorage<ILanguage, Object, PropertiesI18nManager> {

        public LangProps(File folder, PropertiesI18nManager parent, ILanguage source) {
            super(folder, parent, source);
        }

        @Override
        public String comments() {
            return "Unitoo server language file";
        }

        @Override
        protected String fileName() {
            return source().code() + ".properties";
        }

    }

}
