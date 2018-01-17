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

    private static final String ATTR_DELIMITER = "$";

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

    protected static class GlossProps extends PropertiesStorage<IStoredGlossary, IGlossaryItem, PropertiesGlossaryManager> {

        public GlossProps(File folder, PropertiesGlossaryManager parent, IStoredGlossary source) {
            super(folder, parent, source);
        }

        private String keyOf(String name, IGlossaryItem parent, Class type) {
            String key;
            if (parent == null) {
                key = source().name() + "." + name;
            } else {
                key = source().name() + "." + manager().app().format(parent.code()) + ATTR_DELIMITER + name;
            }
            return key;
        }

        @Override
        public boolean hasValue(String name, IGlossaryItem parent, Class type) {
            return super.hasValue(keyOf(name, parent, type), parent, type);
        }

        @Override
        public Object getValue(String name, IGlossaryItem parent, Class type) {
            return super.getValue(keyOf(name, parent, type), parent, type);
        }

        @Override
        public void putValue(String name, Object value, IGlossaryItem parent, Class type) {
            super.putValue(keyOf(name, parent, type), value, parent, type);
        }

        @Override
        public Iterable<String> keys() {
            ArrayList<String> keys = new ArrayList<>();
            for (String prop : super.keys()) {
                if (!prop.contains(ATTR_DELIMITER)) {
                    int c = prop.lastIndexOf(".");
                    keys.add(prop.substring(c + 1));
                }
            }
            return keys;
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
