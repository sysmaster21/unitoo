/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.api.components;

import org.master.unitoo.core.api.IControllerMethod;
import org.master.unitoo.core.api.util.ISession;
import org.master.unitoo.core.errors.AccessDenied;
import org.master.unitoo.core.errors.InvalidSession;

/**
 *
 * @author Andrey
 */
public interface ISecurity extends IController {

    ISession newSession();

    void check(IControllerMethod method, ISession session) throws AccessDenied, InvalidSession;

}
