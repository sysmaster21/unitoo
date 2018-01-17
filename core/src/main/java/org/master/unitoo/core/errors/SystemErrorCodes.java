/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.errors;

import org.master.unitoo.core.api.IErrorType;

/**
 *
 * @author Andrey
 */
public enum SystemErrorCodes implements IErrorType {

    SystemException("UTS001"),
    AccessDenied("UTS002"),
    ComponentNotActive("UTS003"),
    StorageLoadException("UTS004"),
    StorageFlushException("UTS005"),
    DatabaseException("UTS006"),
    FieldInitException("UTS007"),
    InvalidSession("UTS008"),
    LogonFailed("UTS009"),
    MethodFailed("UTS010"),
    MethodNotAllowed("UTS011"),
    NoSecurityException("UTS012"),
    SettingParseException("UTS013"),
    TypeConvertExpection("UTS014"),
    XMLTranformException("UTS015"),
    EmptyParameterException("UTS016"),
    InvalidParameterException("UTS017"),
    ParameterRangeException("UTS018"),
    ParameterMaskException("UTS019"),
    MethodNotFound("UTS020"),
    HttpResponseError("UTS021"),
    InvalidContentType("UTS022"),
    ComponentLoadException("UTS023"),
    StorageCreateException("UTS024"),
    AttributeGetException("UTS025"),
    AttributeSetException("UTS026"),
    AttributeCreateException("UTS027");

    private final String code;

    private SystemErrorCodes(String code) {
        this.code = code;
    }

    @Override
    public String code() {
        return code;
    }

}
