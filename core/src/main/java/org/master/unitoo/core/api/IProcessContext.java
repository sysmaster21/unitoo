/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.api;

import org.master.unitoo.core.api.components.ILanguage;
import org.master.unitoo.core.api.util.ISession;
import org.master.unitoo.core.types.MethodType;
import org.master.unitoo.core.types.ThreadType;

/**
 *
 * @author Andrey
 */
public interface IProcessContext {

    ThreadType type();

    Iterable<IMethodInformation> stack();

    void methodEnter(String method, MethodType type, String info);

    void methodLive(String info);

    void methodExit();

    ISession session();
    
    ILanguage language();

}
