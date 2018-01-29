/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.types;

import java.lang.reflect.InvocationHandler;
import org.master.unitoo.core.UniToo;
import org.master.unitoo.core.api.IComponent;
import org.master.unitoo.core.api.components.I18nManager;
import org.master.unitoo.core.api.components.IBackgroundTask;
import org.master.unitoo.core.api.components.IConfigManager;
import org.master.unitoo.core.api.components.IController;
import org.master.unitoo.core.api.components.ICounter;
import org.master.unitoo.core.api.components.IErrorHandler;
import org.master.unitoo.core.api.components.IFormatter;
import org.master.unitoo.core.api.components.IGlossary;
import org.master.unitoo.core.api.components.IGlossaryManager;
import org.master.unitoo.core.api.components.ILabelsPack;
import org.master.unitoo.core.api.components.ILanguage;
import org.master.unitoo.core.api.components.ILoggerFactory;
import org.master.unitoo.core.api.components.ISecurity;
import org.master.unitoo.core.api.components.IService;
import org.master.unitoo.core.api.components.ISettings;
import org.master.unitoo.core.api.components.IValidator;
import org.master.unitoo.core.api.components.ext.IHTTPService;
import org.master.unitoo.core.impl.ExternalHTTPService;
import org.master.unitoo.core.api.IDataContent;

/**
 *
 * @author Andrey
 */
public enum ComponentType {

    Formatter(IFormatter.class, UniToo.BOOT_PRIORITY_INIT),
    ErrorHandler(IErrorHandler.class, UniToo.BOOT_PRIORITY_INIT),
    ConfigManager(IConfigManager.class, UniToo.BOOT_PRIORITY_INIT),
    I18nManager(I18nManager.class, UniToo.BOOT_PRIORITY_INIT),
    GlossaryManager(IGlossaryManager.class, UniToo.BOOT_PRIORITY_INIT),
    Logger(ILoggerFactory.class, UniToo.BOOT_PRIORITY_INIT),
    Language(ILanguage.class, UniToo.BOOT_PRIORITY_INIT),
    Labels(ILabelsPack.class, UniToo.BOOT_PRIORITY_INIT),
    //
    DataContent(IDataContent.class, UniToo.BOOT_PRIORITY_CORE),
    Settings(ISettings.class, UniToo.BOOT_PRIORITY_CORE),
    Counter(ICounter.class, UniToo.BOOT_PRIORITY_CORE),
    //
    Glossary(IGlossary.class, UniToo.BOOT_PRIORITY_NORMAL),
    Validator(IValidator.class, UniToo.BOOT_PRIORITY_NORMAL),
    Controller(IController.class, UniToo.BOOT_PRIORITY_NORMAL),
    Service(IService.class, UniToo.BOOT_PRIORITY_NORMAL),
    Security(ISecurity.class, UniToo.BOOT_PRIORITY_NORMAL),
    HttpService(IHTTPService.class, UniToo.BOOT_PRIORITY_NORMAL, ExternalHTTPService.class),
    //
    BackgroundTask(IBackgroundTask.class, UniToo.BOOT_PRIORITY_LAST);

    private final Class<? extends IComponent> clazz;
    private final Class<? extends InvocationHandler> proxy;
    private final int bootOrder;

    private ComponentType(Class<? extends IComponent> clazz, int bootOrder, Class<? extends InvocationHandler> proxy) {
        this.clazz = clazz;
        this.bootOrder = bootOrder;
        this.proxy = proxy;
    }

    private ComponentType(Class<? extends IComponent> clazz, int bootOrder) {
        this.clazz = clazz;
        this.bootOrder = bootOrder;
        this.proxy = null;
    }

    public static final ComponentType valueOf(Class clazz, boolean abstr) {
        for (ComponentType type : values()) {
            if (type.clazz.isAssignableFrom(clazz)) {
                if (abstr && type.proxy != null) {
                    return type;
                } else if (!abstr && type.proxy == null) {
                    return type;
                } else {
                    return null;
                }
            }
        }

        return null;
    }

    public static final ComponentType valueOf(Class clazz) {
        for (ComponentType type : values()) {
            if (type.clazz.isAssignableFrom(clazz)) {
                return type;
            }
        }

        return null;
    }

    public int getBootOrder() {
        return bootOrder;
    }

    public Class<? extends InvocationHandler> proxy() {
        return proxy;
    }

}
