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
import org.master.unitoo.core.api.components.IConfigManager;
import org.master.unitoo.core.errors.DatabaseException;
import org.master.unitoo.core.errors.StorageAccessException;
import org.master.unitoo.core.errors.TypeConvertExpection;
import org.master.unitoo.core.impl.sql.ConfigSQL;
import org.master.unitoo.core.server.ServerConfig;
import org.master.unitoo.core.types.ComponentType;

/**
 *
 * @author Andrey
 */
@Component("config-props")
public class DatabaseConfigManager extends DatabaseExternalValuesManager<ServerConfig, Object> implements IConfigManager {

    private ConfigSQL sql;

    @Override
    protected ConfigProps create(ServerConfig source) {
        return new ConfigProps("global", this, source, sql);
    }

    @Override
    public ComponentType type() {
        return ComponentType.ConfigManager;
    }

    @Override
    public Object getItemCode(Object item) {
        return item;
    }

    protected static class ConfigProps extends DatabaseStorage<ServerConfig, Object, DatabaseConfigManager> {

        private final ConfigSQL sql;

        public ConfigProps(String name, DatabaseConfigManager parent, ServerConfig source, ConfigSQL sql) {
            super(name, parent, source);
            this.sql = sql;
        }

        private String format(Object obj) {
            return manager().app().format(obj);
        }

        private Object parse(String value, Class type) throws TypeConvertExpection {
            return manager().app().parse(value, type);
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
