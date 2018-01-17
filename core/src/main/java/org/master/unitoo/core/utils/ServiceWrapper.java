/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.utils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import org.master.unitoo.core.api.components.IService;
import org.master.unitoo.core.types.MethodType;

/**
 *
 * @author Andrey
 */
public class ServiceWrapper implements InvocationHandler {

    private final IService service;

    public ServiceWrapper(IService service) {
        this.service = service;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        service.app().process().methodEnter(service.name() + "." + method.getName(), MethodType.Service, "");
        try {
            return method.invoke(service, args);
        } finally {
            service.app().process().methodExit();
        }
    }

}
