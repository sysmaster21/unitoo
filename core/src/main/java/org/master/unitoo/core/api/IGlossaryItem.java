/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.api;

import org.master.unitoo.core.api.components.IGlossary;
import org.master.unitoo.core.api.components.ILanguage;
import org.master.unitoo.core.types.CustomAttribute;

/**
 *
 * @author Andrey
 * @param <T> класс кода элемента
 */
public interface IGlossaryItem<T> {

    T code();

    String label();
    
    String label(ILanguage language);

    String defLabel(String langCode);

    Iterable<CustomAttribute> attributes();

    CustomAttribute attribute(String name);

    IGlossary<T, ? extends IGlossaryItem<T>> glossary();

}
