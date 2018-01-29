/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.impl.sql;

import java.util.Date;
import java.util.List;
import org.master.sqlonfly.impl.SqlDefaultDataTable;
import org.master.sqlonfly.interfaces.ISQLBatch;
import org.master.unitoo.core.errors.DatabaseException;

/**
 * @connection system
 */
public interface ConfigSQL extends ISQLBatch<ConfigSQL> {

    /**
     * {@code
     *  select
     *      count(*) as Count
     *  from
     *      config
     *  where
     *      ConfigStorage = <#storage#>
     *      ConfigValueChanged > <#date#>
     * }
     *
     * @param storage VARCHAR(250)
     * @param date DATETIME
     *
     * @return Count
     * @throws org.master.unitoo.core.errors.DatabaseException
     *
     * @execute select
     */
    public Integer changedAfter(String storage, Date date) throws DatabaseException;

    /**
     *
     * {@code
     *  select
     *      ConfigName
     *  from
     *      config
     *  where
     *      ConfigStorage = <#storage#>
     * }
     *
     * @param storage VARCHAR(250)
     *
     * @return ConfigName
     * @throws org.master.unitoo.core.errors.DatabaseException
     *
     * @execute select
     */
    public String[] keys(String storage) throws DatabaseException;

    /**
     *
     * {@code
     *  update config_attrs set
     *      ConfigAttrValue = <#value#>
     *  where
     *      ConfigName = <#code#>
     *      and ConfigAttrName = <#attrName#>
     *      and ConfigStorage = <#storage#>
     * }
     *
     * @param storage VARCHAR(250)
     * @param code VARCHAR(250)
     * @param attrName VARCHAR(250)
     * @param value TEXT
     *
     * @return @@rowcount
     * @throws org.master.unitoo.core.errors.DatabaseException
     *
     * @execute update
     */
    public int setAttr(String storage, String code, String attrName, Object value) throws DatabaseException;

    /**
     *
     * {@code
     *  insert into config_attrs (ConfigName, ConfigAttrName, ConfigStorage, ConfigAttrValue)
     *      values (<#code#>, <#attrName#>, <#storage#>, <#value#>)
     * }
     *
     * @param storage VARCHAR(250)
     * @param code VARCHAR(250)
     * @param attrName VARCHAR(250)
     * @param value TEXT
     *
     * @return @@rowcount
     * @throws org.master.unitoo.core.errors.DatabaseException
     *
     * @execute update
     */
    public int addAttr(String storage, String code, String attrName, Object value) throws DatabaseException;

    /**
     *
     * {@code
     *  update config set
     *      ConfigValue = <#value#>,
     *      ConfigValueChanged = NOW()
     *  where
     *      ConfigName = <#code#>
     *      and ConfigStorage = <#storage#>
     * }
     *
     * @param storage VARCHAR(250)
     * @param code VARCHAR(250)
     * @param value TEXT
     *
     * @return @@rowcount
     * @throws org.master.unitoo.core.errors.DatabaseException
     *
     * @execute update
     */
    public int setValue(String storage, String code, Object value) throws DatabaseException;

    /**
     *
     * {@code
     *  insert into config (ConfigName, ConfigStorage, ConfigValue, ConfigValueChanged)
     *      values (<#code#>, <#storage#>, <#value#>, NOW())
     * }
     *
     * @param storage VARCHAR(250)
     * @param code VARCHAR(250)
     * @param value TEXT
     *
     * @return @@rowcount
     * @throws org.master.unitoo.core.errors.DatabaseException
     *
     * @execute update
     */
    public int addValue(String storage, String code, Object value) throws DatabaseException;

    /**
     *
     * {@code
     *  select
     *      ConfigAttrValue
     *  from
     *      config_attrs
     *  where
     *      ConfigStorage = <#storage#>
     *      and ConfigName = <#code#>
     *      and ConfigAttrName = <#attrName#>
     * }
     *
     * @param storage VARCHAR(250)
     * @param code VARCHAR(250)
     * @param attrName VARCHAR(250)
     *
     * @return ConfigAttrValue
     * @throws org.master.unitoo.core.errors.DatabaseException
     *
     * @execute select
     */
    public String getAttr(String storage, String code, String attrName) throws DatabaseException;

    /**
     *
     * {@code
     *  select
     *      ConfigValue
     *  from
     *      config
     *  where
     *      ConfigStorage = <#storage#>
     *      and ConfigName = <#code#>
     * }
     *
     * @param storage VARCHAR(250)
     * @param code VARCHAR(250)
     *
     * @return ConfigValue
     * @throws org.master.unitoo.core.errors.DatabaseException
     *
     * @execute select
     */
    public String getValue(String storage, String code) throws DatabaseException;

    /**
     *
     * {@code
     *  select
     *      count(*) as Count
     *  from
     *      config_attrs
     *  where
     *      ConfigStorage = <#storage#>
     *      and ConfigName = <#code#>
     *      and ConfigAttrName = <#attrName#>
     * }
     *
     * @param storage VARCHAR(250)
     * @param code VARCHAR(250)
     * @param attrName VARCHAR(250)
     *
     * @return Count
     * @throws org.master.unitoo.core.errors.DatabaseException
     *
     * @execute select
     */
    public Integer containsAttr(String storage, String code, String attrName) throws DatabaseException;

    /**
     *
     * {@code
     *  select
     *      count(*) as Count
     *  from
     *      config
     *  where
     *      ConfigStorage = <#storage#>
     *      and ConfigName = <#code#>
     * }
     *
     * @param storage VARCHAR(250)
     * @param code VARCHAR(250)
     *
     * @return Count
     * @throws org.master.unitoo.core.errors.DatabaseException
     *
     * @execute select
     */
    public Integer containsKey(String storage, String code) throws DatabaseException;

}
