/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.impl.sql;

import java.util.Date;
import java.util.List;
import org.master.sqlonfly.interfaces.ISQLBatch;
import org.master.unitoo.core.api.IDatabaseStorageAPI;
import org.master.unitoo.core.errors.DatabaseException;

/**
 * @connection system
 */
public interface ConfigSQL extends ISQLBatch<ConfigSQL>, IDatabaseStorageAPI {

    @Override
    /**
     *
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

    @Override
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
    public List<String> keys(String storage) throws DatabaseException;

    @Override
    public int setAttr(String storage, String code, String attrName, Object value) throws DatabaseException;

    @Override
    public int addAttr(String storage, String code, String attrName, Object value) throws DatabaseException;

    @Override
    public int setValue(String storage, String code, Object value) throws DatabaseException;

    @Override
    public int addValue(String storage, String code, Object value) throws DatabaseException;

    @Override
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

    @Override
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
    public String getValue(String storage, String code);

    @Override
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

    @Override
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
