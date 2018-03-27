/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.impl.sql;

import java.util.Date;
import org.master.sqlonfly.interfaces.ISQLBatch;
import org.master.unitoo.core.errors.DatabaseException;

/**
 * @connection system
 */
public interface GlossarySQL extends ISQLBatch<GlossarySQL> {

    /**
     * {@code
     *  select
     *      count(*) as Count
     *  from
     *      glossaries
     *  where
     *      GlossaryName = <#storage#>
     *      and GlossaryUpdated > <#date#>
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
     *      GlossaryItem
     *  from
     *      glossary_items
     *  where
     *      GlossaryName = <#storage#>
     * }
     *
     * @param storage VARCHAR(250)
     *
     * @return GlossaryItem
     * @throws org.master.unitoo.core.errors.DatabaseException
     *
     * @execute select
     */
    public String[] keys(String storage) throws DatabaseException;

    /**
     *
     * {@code
     *  update glossary_item_attr set
     *      GlossaryItemAttrValue = <#value#>
     *  where
     *      GlossaryItem = <#code#>
     *      and GlossaryItemAttr = <#attrName#>
     *      and GlossaryName = <#storage#>
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
     *  insert into glossary_item_attr (GlossaryItem, GlossaryItemAttr, GlossaryName, GlossaryItemAttrValue)
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
     *  update glossary_items set
     *      GlossaryItem = <#code#>
     *  where
     *      GlossaryItem = <#code#>
     *      and GlossaryName = <#storage#>
     * }
     *
     * @param storage VARCHAR(250)
     * @param code VARCHAR(250)
     *
     * @return @@rowcount
     * @throws org.master.unitoo.core.errors.DatabaseException
     *
     * @execute update
     */
    public int setValue(String storage, String code) throws DatabaseException;

    /**
     *
     * {@code
     *  insert into glossary_items (GlossaryItem, GlossaryName)
     *      values (<#code#>, <#storage#>)
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
     *      GlossaryItemAttrValue
     *  from
     *      glossary_item_attr
     *  where
     *      GlossaryName = <#storage#>
     *      and GlossaryItem = <#code#>
     *      and GlossaryItemAttr = <#attrName#>
     * }
     *
     * @param storage VARCHAR(250)
     * @param code VARCHAR(250)
     * @param attrName VARCHAR(250)
     *
     * @return GlossaryItemAttrValue
     * @throws org.master.unitoo.core.errors.DatabaseException
     *
     * @execute select
     */
    public String getAttr(String storage, String code, String attrName) throws DatabaseException;

    /**
     *
     * {@code
     *  select
     *      GlossaryItem
     *  from
     *      glossary_items
     *  where
     *      GlossaryName = <#storage#>
     *      and GlossaryItem = <#code#>
     * }
     *
     * @param storage VARCHAR(250)
     * @param code VARCHAR(250)
     *
     * @return GlossaryItem
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
     *      glossary_item_attr
     *  where
     *      GlossaryName = <#storage#>
     *      and GlossaryItem = <#code#>
     *      and GlossaryItemAttr = <#attrName#>
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
     *      glossary_items
     *  where
     *      GlossaryName = <#storage#>
     *      and GlossaryItem = <#code#>
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

    /**
     *
     * {@code
     *  insert ignore into glossaries (GlossaryName, GlossaryUpdated)
     *      values (<#storage#>, NOW())
     * }
     *
     * @param storage VARCHAR(250)
     *
     * @return @@rowcount
     * @throws org.master.unitoo.core.errors.DatabaseException
     *
     * @execute update
     */
    public int initStorage(String storage) throws DatabaseException;
    
    
    /**
     *
     * {@code
     * insert ignore into translates (LabelKey, LanguageCode, TranslateValue, TranslateUpdated)
     *      values (<#LabelKey#>, <#LanguageCode#>, <#TranslateValue#>, now())
     * }
     *
     * @param LabelKey VARCHAR(255)
     * @param LanguageCode CHAR(2)
     * @param TranslateValue VARCHAR(255)
     * @throws org.master.unitoo.core.errors.DatabaseException
     *
     * @return @@rowcount
     * @execute update
     */
    int translatesAdd(String LabelKey, String LanguageCode, String TranslateValue) throws DatabaseException;
    
}
