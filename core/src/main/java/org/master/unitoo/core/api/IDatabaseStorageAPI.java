/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.api;

import java.util.Date;
import java.util.List;
import org.master.unitoo.core.errors.DatabaseException;

/**
 *
 * @author RogovA
 */
public interface IDatabaseStorageAPI {

    Integer containsKey(String storage, String code) throws DatabaseException;

    Integer containsAttr(String storage, String code, String attrName) throws DatabaseException;

    Object getValue(String storage, String code) throws DatabaseException;

    Object getAttr(String storage, String code, String attrName) throws DatabaseException;

    int addValue(String storage, String code, Object value) throws DatabaseException;

    int setValue(String storage, String code, Object value) throws DatabaseException;

    int addAttr(String storage, String code, String attrName, Object value) throws DatabaseException;

    int setAttr(String storage, String code, String attrName, Object value) throws DatabaseException;

    List<String> keys(String storage) throws DatabaseException;
    
    Integer changedAfter(String storage, Date date) throws DatabaseException;

}
