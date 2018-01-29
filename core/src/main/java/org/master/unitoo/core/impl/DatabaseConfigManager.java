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

        @Override
        protected Integer containsAttr(String storage, String code, String attrName) throws DatabaseException {
            return sql.containsAttr(storage, code, attrName);
        }

        @Override
        protected Integer containsKey(String storage, String code) throws DatabaseException {
            return sql.containsKey(storage, code);
        }

        @Override
        protected String getAttr(String storage, String code, String attrName) throws DatabaseException {
            return sql.getAttr(storage, code, attrName);
        }

        @Override
        protected String getValue(String storage, String code) throws DatabaseException {
            return sql.getValue(storage, code);
        }

        @Override
        protected int setAttr(String storage, String code, String attrName, Object value) throws DatabaseException {
            return sql.setAttr(storage, code, attrName, value);
        }

        @Override
        protected int addAttr(String storage, String code, String attrName, Object value) throws DatabaseException {
            return sql.addAttr(storage, code, attrName, value);
        }

        @Override
        protected int setValue(String storage, String code, Object value) throws DatabaseException {
            return sql.setValue(storage, code, value);
        }

        @Override
        protected int addValue(String storage, String code, Object value) throws DatabaseException {
            return sql.addValue(storage, code, value);
        }

        @Override
        protected Iterable<String> keys(String storage) throws DatabaseException {
            String[] keys = sql.keys(storage);
            return keys == null ? Collections.EMPTY_LIST : Arrays.asList(keys);
        }

        @Override
        protected Integer changedAfter(String storage, Date date) throws DatabaseException {
            return sql.changedAfter(storage, date);
        }

    }
}
