/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.api;

import org.master.unitoo.core.errors.UnitooException;

/**
 *
 * @author Andrey
 */
public interface IFlushable extends IBootListener {

    boolean isChanged();

    void flush() throws UnitooException;

}
