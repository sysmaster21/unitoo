/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.api;

/**
 *
 * @author Andrey
 */
public interface IInterfacedComponent {

    <T> T wrapper(Class<T> iface);
    
    Iterable<Class<? extends IInterfacedComponent>> interfaces();

}
