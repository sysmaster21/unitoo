/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.api.components;

import org.master.unitoo.core.api.IStoppableComponent;
import org.master.unitoo.core.types.DateTime;

/**
 *
 * @author Andrey
 */
public interface IBackgroundTask extends IStoppableComponent, Runnable {

    DateTime nextRun();

    DateTime lastRun();

    long running();
}
