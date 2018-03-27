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
public interface I18nSQL extends ISQLBatch<I18nSQL> {

    /**
     * {@code
     *  select
     *      count(*) as Count
     *  from
     *      translates
     *  where
     *      LanguageCode = <#lang#>
     *      and TranslateUpdated > <#date#>
     * }
     *
     * @param lang VARCHAR(250)
     * @param date DATETIME
     *
     * @return Count
     * @throws org.master.unitoo.core.errors.DatabaseException
     *
     * @execute select
     */
    public Integer changedAfter(String lang, Date date) throws DatabaseException;

    /**
     *
     * {@code
     *  select
     *      LabelKey
     *  from
     *      translates
     *  where
     *      LanguageCode = <#lang#>
     * }
     *
     * @param lang VARCHAR(250)
     *
     * @return LabelKey
     * @throws org.master.unitoo.core.errors.DatabaseException
     *
     * @execute select
     */
    public String[] keys(String lang) throws DatabaseException;

    /**
     *
     * {@code
     *  update translates set
     *      TranslateValue = <#value#>,
     *      TranslateUpdated = NOW()
     *  where
     *      LabelKey = <#code#>
     *      and LanguageCode = <#lang#>
     * }
     *
     * @param lang VARCHAR(250)
     * @param code VARCHAR(250)
     * @param value VARCHAR(255)
     *
     * @return @@rowcount
     * @throws org.master.unitoo.core.errors.DatabaseException
     *
     * @execute update
     */
    public int setValue(String lang, String code, Object value) throws DatabaseException;

    /**
     *
     * {@code
     *  insert into translates (LanguageCode, LabelKey, TranslateValue, TranslateUpdated)
     *      values (<#lang#>, <#code#>, <#value#>, NOW())
     * }
     *
     * @param lang VARCHAR(250)
     * @param code VARCHAR(250)
     * @param value VARCHAR(255)
     *
     * @return @@rowcount
     * @throws org.master.unitoo.core.errors.DatabaseException
     *
     * @execute update
     */
    public int addValue(String lang, String code, Object value) throws DatabaseException;

    /**
     *
     * {@code
     *  select
     *      TranslateValue
     *  from
     *      translates
     *  where
     *      LanguageCode = <#lang#>
     *      and LabelKey = <#code#>
     * }
     *
     * @param lang VARCHAR(250)
     * @param code VARCHAR(250)
     *
     * @return TranslateValue
     * @throws org.master.unitoo.core.errors.DatabaseException
     *
     * @execute select
     */
    public String getValue(String lang, String code) throws DatabaseException;

    /**
     *
     * {@code
     *  select
     *      count(*) as Count
     *  from
     *      translates
     *  where
     *      LanguageCode = <#lang#>
     *      and LabelKey = <#code#>
     * }
     *
     * @param lang VARCHAR(250)
     * @param code VARCHAR(250)
     *
     * @return Count
     * @throws org.master.unitoo.core.errors.DatabaseException
     *
     * @execute select
     */
    public Integer containsKey(String lang, String code) throws DatabaseException;

}
