/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.api;

import java.util.Map;
import org.master.unitoo.core.api.components.IFormatter;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSession;
import org.master.sqlonfly.interfaces.ISQLBatch;
import org.master.sqlonfly.interfaces.ISQLDataRow;
import org.master.sqlonfly.interfaces.ISQLDataTable;
import org.master.unitoo.core.api.components.IBackgroundTask;
import org.master.unitoo.core.api.components.ILoggerFactory;
import org.master.unitoo.core.api.components.ISecurity;
import org.master.unitoo.core.errors.AccessDenied;
import org.master.unitoo.core.errors.InvalidSession;
import org.master.unitoo.core.errors.NoSecurityException;
import org.master.unitoo.core.errors.SystemException;
import org.master.unitoo.core.errors.TypeConvertExpection;
import org.master.unitoo.core.errors.UnitooException;
import org.master.unitoo.core.impl.Label;
import org.master.unitoo.core.types.ComponentType;

/**
 *
 * @author Andrey
 */
public interface IApplication extends ServletContextListener {

    //-------------------------------------------------------------------------- Settings
    IApplicationDefaults defaults();

    String serverId();

    String appName();

    String homeFolder();

    String version();

    //-------------------------------------------------------------------------- Registration
    void register(ISecurity security);

    void register(IControllerMethod method);

    //-------------------------------------------------------------------------- Formatting
    String format(Object obj);

    String format(IFormatter formatter, Object obj);

    <T> T parse(String value, Class<T> clazz) throws TypeConvertExpection;

    <T> T parse(IFormatter formatter, String value, Class<T> clazz) throws TypeConvertExpection;

    <T> T convert(Object value, Class<T> clazz) throws TypeConvertExpection;

    //-------------------------------------------------------------------------- Components
    ILogger log();

    ILogger log(Class<? extends ILoggerFactory> factory, Class component);

    ILogger log(Class<? extends ILoggerFactory> factory, String component);

    Label label(String key);

    Iterable<Label> labels();

    <T> T component(Class<T> clazz);

    <T> T component(Class<T> iface, String name);

    IComponent component(ComponentType type, String name);

    <T extends IComponent> Iterable<T> components(Class<T> clazz);

    //-------------------------------------------------------------------------- System
    void beforeRequest(IControllerMethod method, HttpSession session) throws AccessDenied, InvalidSession, NoSecurityException;

    void afterRequest(IControllerMethod method) throws AccessDenied, InvalidSession;

    <T extends ISQLBatch> T cast(Class<T> iface) throws SystemException;

    //-------------------------------------------------------------------------- Processes
    void execute(IBackgroundTask task, long delay);

    IProcess process();

    IProcess process(Thread thread);

    //-------------------------------------------------------------------------- BusinessObjects
    Iterable<IBusinessField> businessFields(Class<? extends IBusinessObject> type);

    void map(IBusinessObject dest, ISQLDataTable source) throws UnitooException;

    void map(IBusinessObject dest, Map<String, Object> source) throws UnitooException;
}
