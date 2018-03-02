/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.base;

import org.master.unitoo.core.api.IControllerMethod;
import org.master.unitoo.core.api.util.ISession;
import org.master.unitoo.core.api.components.ISecurity;
import org.master.unitoo.core.api.util.IUser;
import org.master.unitoo.core.errors.AccessDenied;
import org.master.unitoo.core.errors.InvalidSession;
import org.master.unitoo.core.types.SecureLevel;
import org.master.unitoo.core.types.SessionState;

/**
 *
 * @author Andrey
 * @param <U> User class
 */
public abstract class BaseSecurity<U extends IUser> extends BaseController implements ISecurity {

    @Override
    protected U user() throws InvalidSession {
        return (U) super.user();
    }

    @Override
    public void prepare() {
        super.prepare();
        app().register(this);
    }

    @Override
    public void check(IControllerMethod method, ISession session) throws AccessDenied, InvalidSession {
        if (method.secureLevel() == SecureLevel.Preauth
                && session.state() != SessionState.InAuthorize
                && session.state() != SessionState.Normal) {
            throw new InvalidSession();

        } else if (method.secureLevel() == SecureLevel.Authorized
                || method.secureLevel() == SecureLevel.Secured) {

            if (session.state() != SessionState.Normal) {
                throw new InvalidSession();
            } else {
                if (method.secureLevel() == SecureLevel.Secured) {
                    checkAccess(method, session);
                }
            }
        }
    }

    protected abstract void checkAccess(IControllerMethod method, ISession session) throws AccessDenied;

}
