/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import org.master.unitoo.core.api.annotation.Component;
import org.master.unitoo.core.api.components.I18nManager;
import org.master.unitoo.core.api.components.ILanguage;
import org.master.unitoo.core.errors.DatabaseException;
import org.master.unitoo.core.errors.StorageAccessException;
import org.master.unitoo.core.errors.TypeConvertExpection;
import org.master.unitoo.core.impl.sql.I18nSQL;
import org.master.unitoo.core.types.ComponentType;

/**
 *
 * @author Andrey
 */
@Component("i18n database manager")
public class DatabaseI18nManager extends DatabaseExternalValuesManager<ILanguage, Object> implements I18nManager {

    private I18nSQL sql;

    @Override
    protected TranslateProps create(ILanguage source) {
        return new TranslateProps(source.code(), this, source, sql);
    }

    @Override
    public ComponentType type() {
        return ComponentType.I18nManager;
    }

    @Override
    public Object getItemCode(Object item) {
        return item;
    }

    protected static class TranslateProps extends DatabaseStorage<ILanguage, Object, DatabaseI18nManager> {

        private final I18nSQL sql;

        public TranslateProps(String name, DatabaseI18nManager parent, ILanguage source, I18nSQL sql) {
            super(name, parent, source);
            this.sql = sql;
        }

        @Override
        protected Integer containsAttr(Object code, String attrName) throws DatabaseException {
            return 0;
        }

        @Override
        protected Integer containsKey(String code) throws DatabaseException {
            return sql.containsKey(name(), code);
        }

        @Override
        protected Object getAttr(Object code, String attrName, Class type) throws DatabaseException, TypeConvertExpection {
            return null;
        }

        @Override
        protected Object getValue(String code, Class type) throws DatabaseException, TypeConvertExpection {
            return parse(sql.getValue(name(), code), type);
        }

        @Override
        protected int setAttr(Object code, String attrName, Object value) throws DatabaseException {
            return 0;
        }

        @Override
        protected int addAttr(Object code, String attrName, Object value) throws DatabaseException {
            return 0;
        }

        @Override
        protected int setValue(String code, Object value) throws DatabaseException {
            return sql.setValue(name(), code, manager().app().format(value));
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
