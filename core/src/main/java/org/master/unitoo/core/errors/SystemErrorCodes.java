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

    UTS_SystemException("UTS001"),
    UTS_AccessDenied("UTS002"),
    UTS_ComponentNotActive("UTS003"),
    UTS_StorageLoadException("UTS004"),
    UTS_StorageFlushException("UTS005"),
    UTS_DatabaseException("UTS006"),
    UTS_FieldInitException("UTS007"),
    UTS_InvalidSession("UTS008"),
    UTS_LogonFailed("UTS009"),
    UTS_MethodFailed("UTS010"),
    UTS_MethodNotAllowed("UTS011"),
    UTS_NoSecurityException("UTS012"),
    UTS_SettingParseException("UTS013"),
    UTS_TypeConvertExpection("UTS014"),
    UTS_XMLException("UTS015"),
    UTS_EmptyParameterException("UTS016"),
    UTS_InvalidParameterException("UTS017"),
    UTS_ParameterRangeException("UTS018"),
    UTS_ParameterMaskException("UTS019"),
    UTS_MethodNotFound("UTS020"),
    UTS_HttpResponseError("UTS021"),
    UTS_InvalidContentType("UTS022"),
    UTS_ComponentLoadException("UTS023"),
    UTS_StorageCreateException("UTS024"),
    UTS_AttributeGetException("UTS025"),
    UTS_AttributeSetException("UTS026"),
    UTS_AttributeCreateException("UTS027"),
    UTS_StorageAccessException("UTS028");

    private final String code;

    private SystemErrorCodes(String code) {
        this.code = code;
    }

    @Override
    public String code() {
        return code;
    }

}
