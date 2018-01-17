/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.api.components;

import org.master.unitoo.core.api.IAutowired;
import org.master.unitoo.core.api.ICustomizedComponent;
import org.master.unitoo.core.impl.Label;

/**
 *
 * @author Andrey
 */
public interface ILabelsPack extends ICustomizedComponent, IAutowired {

    Iterable<Label> labels();

}
