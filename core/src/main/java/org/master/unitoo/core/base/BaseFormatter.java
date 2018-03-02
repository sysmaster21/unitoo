/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.base;

import java.text.DateFormat;
import java.text.NumberFormat;
import org.master.unitoo.core.api.IApplication;
import org.master.unitoo.core.api.IBootInfo;
import org.master.unitoo.core.api.components.IFormatter;
import org.master.unitoo.core.errors.TypeConvertExpection;
import org.master.unitoo.core.errors.XMLException;
import org.master.unitoo.core.types.ComponentContext;
import org.master.unitoo.core.types.ComponentType;
import org.master.unitoo.core.types.BinaryFormat;
import org.master.unitoo.core.utils.JSONFormat;
import org.master.unitoo.core.utils.XMLFormat;

/**
 *
 * @author Andrey
 */
public abstract class BaseFormatter implements IFormatter {

    private ComponentContext context;
    private final ThreadLocal<BinaryFormat> fmtBinary = new InheritableThreadLocal<>();
    private final ThreadLocal<String> fmtCharset = new InheritableThreadLocal<>();
    private final ThreadLocal<DateFormat> fmtDate = new InheritableThreadLocal<>();
    private final ThreadLocal<DateFormat> fmtTime = new InheritableThreadLocal<>();
    private final ThreadLocal<DateFormat> fmtDateTime = new InheritableThreadLocal<>();
    private final ThreadLocal<NumberFormat> fmtDecimal = new InheritableThreadLocal<>();
    private final ThreadLocal<NumberFormat> fmtInteger = new InheritableThreadLocal<>();
    private final ThreadLocal<XMLFormat> fmtXML = new InheritableThreadLocal<>();
    private final ThreadLocal<JSONFormat> fmtJson = new InheritableThreadLocal<>();

    protected abstract DateFormat createDateFormat();

    protected abstract DateFormat createTimeFormat();

    protected abstract DateFormat createDateTimeFormat();

    protected abstract NumberFormat createDecimalFormat();

    protected abstract NumberFormat createIntegerFormat();

    protected abstract String createCharset();

    protected abstract BinaryFormat createBinary();

    @Override
    public String encoding() {
        String format = fmtCharset.get();
        if (format == null) {
            format = createCharset();
            fmtCharset.set(format);
        }
        return format;
    }

    @Override
    public BinaryFormat binary() {
        BinaryFormat format = fmtBinary.get();
        if (format == null) {
            format = createBinary();
            fmtBinary.set(format);
        }
        return format;
    }

    @Override
    public DateFormat date() {
        DateFormat format = fmtDate.get();
        if (format == null) {
            format = createDateFormat();
            fmtDate.set(format);
        }
        return format;
    }

    @Override
    public DateFormat time() {
        DateFormat format = fmtTime.get();
        if (format == null) {
            format = createTimeFormat();
            fmtTime.set(format);
        }
        return format;
    }

    @Override
    public DateFormat datetime() {
        DateFormat format = fmtDateTime.get();
        if (format == null) {
            format = createDateTimeFormat();
            fmtDateTime.set(format);
        }
        return format;
    }

    @Override
    public NumberFormat decimal() {
        NumberFormat format = fmtDecimal.get();
        if (format == null) {
            format = createDecimalFormat();
            fmtDecimal.set(format);
        }
        return format;
    }

    @Override
    public NumberFormat integer() {
        NumberFormat format = fmtInteger.get();
        if (format == null) {
            format = createIntegerFormat();
            fmtInteger.set(format);
        }
        return format;
    }

    @Override
    public XMLFormat xml() throws XMLException {
        XMLFormat format = fmtXML.get();
        if (format == null) {
            format = new XMLFormat(this);
            fmtXML.set(format);
        }
        return format;
    }

    @Override
    public JSONFormat json() {
        JSONFormat format = fmtJson.get();
        if (format == null) {
            format = new JSONFormat(this);
            fmtJson.set(format);
        }
        return format;
    }

    @Override
    public void init(ComponentContext context) {
        this.context = context;
    }

    @Override
    public void destroy() {
    }

    @Override
    public String name() {
        return getClass().getName();
    }

    @Override
    public String description() {
        return context.description();
    }

    @Override
    public String version() {
        return context.version();
    }

    @Override
    public String internal() {
        return context.internal();
    }

    @Override
    public IApplication app() {
        return context.application();
    }

    @Override
    public String info() {
        return context.description();
    }

    @Override
    public ComponentType type() {
        return ComponentType.Formatter;
    }

    @Override
    public IBootInfo boot() {
        return context.boot();
    }

    @Override
    public String format(Object obj) {
        return app().format(this, obj);
    }

    @Override
    public <T> T parse(String value, Class<T> clazz) throws TypeConvertExpection {
        return app().parse(this, value, clazz);
    }

}
