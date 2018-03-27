/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.api.util;

import org.master.unitoo.core.api.components.ILanguage;
import org.master.unitoo.core.errors.UnitooException;

/**
 *
 * @author Andrey
 */
public interface IUser {

    Integer id();

    ILanguage language();

    void language(ILanguage language) throws UnitooException;

    Integer company();

    void company(Integer company) throws UnitooException;

    String login();

    String name();

    void reload() throws UnitooException;
}
