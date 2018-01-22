/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.impl;

import java.io.File;
import java.util.ArrayList;
import org.master.unitoo.core.api.IGlossaryItem;
import org.master.unitoo.core.api.annotation.Component;
import org.master.unitoo.core.api.components.IGlossaryManager;
import org.master.unitoo.core.api.components.IStoredGlossary;
import org.master.unitoo.core.types.ComponentType;

/**
 *
 * @author Andrey
 */
@Component("glossary-props")
public class PropertiesGlossaryManager extends PropertiesExternalValuesManager<IStoredGlossary, IGlossaryItem> implements IGlossaryManager {

    @Override
    protected GlossProps create(IStoredGlossary source) {
        File parent = new File(app().homeFolder() + File.separator + "lists");
        parent.mkdirs();
        return new GlossProps(parent, this, source);
    }

    @Override
    public ComponentType type() {
        return ComponentType.GlossaryManager;
    }

    @Override
    public Object getItemCode(IGlossaryItem item) {
        return item.code();
    }

    protected static class GlossProps extends PropertiesStorage<IStoredGlossary, IGlossaryItem, PropertiesGlossaryManager> {

        public GlossProps(File folder, PropertiesGlossaryManager parent, IStoredGlossary source) {
            super(folder, parent, source);
        }

        @Override
        public String comments() {
            if (source() != null) {
                String key = keyOf("AB", null, IGlossaryItem.class);
                return "Unitoo server glossary file\n"
                        + "Example of glossary 'AB' item: " + key + "\n"
                        + "Example of glissary 'AB' item 'ext' attribute: " + key + ATTR_DELIMITER + "ext";
            } else {
                return "Unitoo server glossary file";
            }
        }

        @Override
        protected String fileName() {
            return source().name() + ".properties";
        }

    }

}
