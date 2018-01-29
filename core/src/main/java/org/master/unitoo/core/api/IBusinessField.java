/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.api;

import org.master.unitoo.core.types.Decision;

/**
 *
 * @author Andrey
 */
public interface IBusinessField {

    String name();

    Class type();

    Object get(IBusinessObject object);

    void set(Object value, IBusinessObject object);

    Class keyType();

    Class itemType();

    Decision trim();

    Decision escape();
}
