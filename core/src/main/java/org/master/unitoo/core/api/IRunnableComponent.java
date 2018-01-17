/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.api;

import org.master.unitoo.core.types.RunnableState;

/**
 *
 * @author Andrey
 */
public interface IRunnableComponent extends ICustomizedComponent {

    void start();

    RunnableState state();

    void reject();
}
