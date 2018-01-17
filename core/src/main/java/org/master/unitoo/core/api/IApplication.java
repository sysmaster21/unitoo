/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.api;

import org.master.unitoo.core.api.components.IFormatter;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSession;
import org.master.sqlonfly.interfaces.ISQLBatch;
import org.master.unitoo.core.api.components.IBackgroundTask;
import org.master.unitoo.core.api.components.ILanguage;
import org.master.unitoo.core.api.components.ILoggerFactory;
import org.master.unitoo.core.api.components.ISecurity;
import org.master.unitoo.core.errors.AccessDenied;
import org.master.unitoo.core.errors.InvalidSession;
import org.master.unitoo.core.errors.NoSecurityException;
import org.master.unitoo.core.errors.SystemException;
import org.master.unitoo.core.errors.TypeConvertExpection;
import org.master.unitoo.core.types.ComponentType;

/**
 *
 * @author Andrey
 */
public interface IApplication extends ServletContextListener {

    IApplicationDefaults defaults();

    String serverId();

    String appName();

    String homeFolder();

    String version();

    void register(ISecurity security);

    void register(IControllerMethod method);

    String format(Object obj);

    String format(IFormatter formatter, Object obj);

    String format(IFormatter formatter, Object obj, IFormatContext context);

    <T> T parse(String value, Class<T> clazz) throws TypeConvertExpection;

    <T> T parse(IFormatter formatter, String value, Class<T> clazz) throws TypeConvertExpection;

    <T> T parse(IFormatter formatter, String value, Class<T> clazz, IFormatContext context) throws TypeConvertExpection;

    <T> T convert(Object value, Class<T> clazz) throws TypeConvertExpection;

    ILogger log();

    ILogger log(Class<? extends ILoggerFactory> factory, Class component);

    ILogger log(Class<? extends ILoggerFactory> factory, String component);

    <T> T component(Class<T> clazz);

    IComponent component(ComponentType type, String name);

    <T extends IComponent> Iterable<T> components(Class<T> clazz);

    void execute(IBackgroundTask task, long delay);

    void beforeRequest(IControllerMethod method, HttpSession session) throws AccessDenied, InvalidSession, NoSecurityException;

    void afterRequest(IControllerMethod method) throws AccessDenied, InvalidSession;

    IProcessContext process();

    IProcessContext process(Thread thread);

    <T extends ISQLBatch> T cast(Class<T> iface) throws SystemException;

    ILanguage language(String code);

    Iterable<ILanguage> languages();

}
