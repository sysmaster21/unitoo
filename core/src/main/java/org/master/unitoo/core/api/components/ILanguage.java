/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.api.components;

import java.util.Map;
import org.master.unitoo.core.api.IComponent;
import org.master.unitoo.core.api.IFlushable;
import org.master.unitoo.core.errors.UnitooException;
import org.master.unitoo.core.impl.Label;

/**
 *
 * @author Andrey
 */
public interface ILanguage extends IComponent, IFlushable {

    String code();

    String label(String key);

    boolean register(Label label);

    void update(String key, String value);

    void refresh() throws UnitooException;

    void init(I18nManager manager) throws UnitooException;

    Map<String, String> items(String regexp);

}
