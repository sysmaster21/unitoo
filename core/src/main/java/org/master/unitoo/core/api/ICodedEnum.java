/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.api;

/**
 *
 * @author Andrey
 * @param <T> code type
 */
public interface ICodedEnum<T> {

    T code();

    Class<T> type();

    boolean is(T code);
}
