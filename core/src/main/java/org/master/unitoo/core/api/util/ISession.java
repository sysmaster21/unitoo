/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.api.util;

import java.util.Date;
import org.master.unitoo.core.types.SessionState;

/**
 *
 * @author Andrey
 */
public interface ISession {

    IUser user();

    Date started();

    SessionState state();
    
}
