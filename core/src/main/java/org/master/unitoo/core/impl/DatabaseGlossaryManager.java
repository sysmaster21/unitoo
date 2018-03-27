/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import org.master.unitoo.core.api.IGlossaryItem;
import org.master.unitoo.core.api.annotation.Component;
import org.master.unitoo.core.api.components.IGlossaryManager;
import org.master.unitoo.core.api.components.IStoredGlossary;
import org.master.unitoo.core.errors.DatabaseException;
import org.master.unitoo.core.errors.StorageAccessException;
import org.master.unitoo.core.errors.TypeConvertExpection;
import org.master.unitoo.core.errors.UnitooException;
import org.master.unitoo.core.impl.sql.GlossarySQL;
import org.master.unitoo.core.types.ComponentType;

/**
 *
 * @author Andrey
 */
@Component("Glossary database manager")
public class DatabaseGlossaryManager extends DatabaseExternalValuesManager<IStoredGlossary, IGlossaryItem> implements IGlossaryManager {

    private GlossarySQL sql;

    @Override
    protected GlossaryProps create(IStoredGlossary source) throws UnitooException {
        sql.initStorage(source.name());
        return new GlossaryProps(source.name(), this, source, sql);
    }

    @Override
    public ComponentType type() {
        return ComponentType.GlossaryManager;
    }

    @Override
    public Object getItemCode(Object item) {
        if (item instanceof IGlossaryItem) {
            return ((IGlossaryItem) item).code();
        } else {
            return item;
        }
    }

    protected static class GlossaryProps extends DatabaseStorage<IStoredGlossary, IGlossaryItem, DatabaseGlossaryManager> {

        private final GlossarySQL sql;

        public GlossaryProps(String name, DatabaseGlossaryManager parent, IStoredGlossary source, GlossarySQL sql) {
            super(name, parent, source);
            this.sql = sql;
        }

        @Override
        protected Integer containsAttr(Object code, String attrName) throws DatabaseException {
            return sql.containsAttr(name(), format(code), attrName);
        }

        @Override
        protected Integer containsKey(String code) throws DatabaseException {
            return sql.containsKey(name(), code);
        }

        @Override
        protected Object getAttr(Object code, String attrName, Class type) throws DatabaseException, TypeConvertExpection {
            return parse(sql.getAttr(name(), format(code), attrName), type);
        }

        @Override
        protected Object getValue(String code, Class type) throws DatabaseException, TypeConvertExpection {
            return parse(sql.getValue(name(), code), type);
        }

        @Override
        protected int setAttr(Object code, String attrName, Object value) throws DatabaseException {
            return sql.setAttr(name(), format(code), attrName, manager().app().format(value));
        }

        @Override
        protected int addAttr(Object code, String attrName, Object value) throws DatabaseException {
            return sql.addAttr(name(), format(code), attrName, manager().app().format(value));
        }

        @Override
        protected int setValue(String code, Object value) throws DatabaseException {
            return sql.setValue(name(), code);
        }

        @Override
        protected int addValue(String code, Object value) throws DatabaseException {
            return sql.addValue(name(), code, manager().app().format(value));
        }

        @Override
        public Iterable<String> keys() throws StorageAccessException {
            try {
                String[] keys = sql.keys(name());
                return keys == null ? Collections.EMPTY_LIST : Arrays.asList(keys);
            } catch (DatabaseException e) {
                throw new StorageAccessException(name(), e);
            }
        }

        @Override
        protected Integer changedAfter(Date date) throws DatabaseException {
            return sql.changedAfter(name(), date);
        }

    }
}
