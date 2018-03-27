/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.impl;

import java.util.Collection;
import java.util.Map;
import org.master.unitoo.core.api.IBusinessObject;
import org.master.unitoo.core.errors.SystemException;
import org.master.unitoo.core.errors.UnitooException;

/**
 *
 * @author Andrey
 */
public class StandartResponse implements IBusinessObject {

    private final static transient StandartResponse EMPTY_RESPONSE = new StandartResponse("000", null, null, null, null, null);

    public static StandartResponse Success() {
        return EMPTY_RESPONSE;
    }

    public static StandartResponse Error(Throwable t) {
        if (t instanceof UnitooException) {
            return new StandartResponse(((UnitooException) t).code(), ((UnitooException) t).getMessage(), null, null, null, null);
        } else {
            SystemException error = new SystemException(t);
            return new StandartResponse(error.code(), error.getMessage(), null, null, null, null);
        }
    }

    public static StandartResponse Error(String code, String message) {
        return new StandartResponse(code, message, null, null, null, null);
    }

    public static StandartResponse Items(Collection items) {
        return new StandartResponse("000", null, items, null, null, null);
    }

    public static StandartResponse Map(Map map) {
        return new StandartResponse("000", null, null, map, null, null);
    }

    public static StandartResponse Item(IBusinessObject item) {
        return new StandartResponse("000", null, null, null, item, null);
    }

    public static StandartResponse Value(Object value) {
        return new StandartResponse("000", null, null, null, null, value);
    }

    private final String errorCode;
    private final String errorText;
    private final Collection items;
    private final Map map;
    private final IBusinessObject item;
    private final Object value;

    protected StandartResponse() {
        this("000", null, null, null, null, null);
    }

    protected StandartResponse(String errorCode, String errorText, Collection items, Map map, IBusinessObject item, Object value) {
        this.errorCode = errorCode;
        this.errorText = errorText;
        this.value = value;
        this.items = items;
        this.item = item;
        this.map = map;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorText() {
        return errorText;
    }

    public Object getValue() {
        return value;
    }

    public Collection getItems() {
        return items;
    }

    public Map getMap() {
        return map;
    }

    public IBusinessObject getItem() {
        return item;
    }

}
