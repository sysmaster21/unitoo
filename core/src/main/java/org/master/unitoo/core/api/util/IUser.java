/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.api.util;

import org.master.unitoo.core.api.components.ILanguage;

/**
 *
 * @author Andrey
 * @param <T>
 */
public interface IUser<T> {

    T id();

    String login();

    String name();

    ILanguage language();
}
