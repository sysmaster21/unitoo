/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.glossaries;

import java.util.concurrent.ConcurrentHashMap;
import org.master.unitoo.core.api.components.IGlossary;
import org.master.unitoo.core.api.components.ILanguage;

/**
 *
 * @author Andrey
 * @param <T> класс кода элемента
 */
public class TranslatedGlossaryItem<T> extends GlossaryItem<T> {

    private final ConcurrentHashMap<String, String> translates = new ConcurrentHashMap<>();

    public TranslatedGlossaryItem(T code, IGlossary<T, ? extends GlossaryItem<T>> parent) {
        super(code, parent);
    }

    public void putTranslate(String langCode, String text) {
        translates.put(langCode, text);
    }

    @Override
    public String label(ILanguage language) {
        String text = translates.get(language.code());
        return text == null ? code().toString() : text;
    }

}
