/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.api;

import java.util.Map;
import org.master.unitoo.core.api.components.IErrorHandler;
import org.master.unitoo.core.api.components.IFormatter;
import org.master.unitoo.core.api.components.ILanguage;

/**
 *
 * @author Andrey
 */
public interface IApplicationDefaults {

    boolean isEscapeControllerParams();

    boolean isEscapeControllerResult();

    boolean isEscapeExternalParams();

    boolean isEscapeExternalResult();

    boolean isTrimControllerParams();

    boolean isTrimControllerResult();

    boolean isTrimExternalParams();

    boolean isTrimExternalResult();

    boolean isStrictMime();

    IDataContent content(String mime);

    Map<String, String> headers();

    IFormatter formatter();

    IErrorHandler errorHandler();

    ILanguage language();

}
