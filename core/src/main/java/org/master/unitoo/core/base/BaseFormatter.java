/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.base;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Date;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import org.master.unitoo.core.api.IApplication;
import org.master.unitoo.core.api.IBootInfo;
import org.master.unitoo.core.api.IFormatContext;
import org.master.unitoo.core.api.components.IFormatter;
import org.master.unitoo.core.api.synthetic.IJsonObject;
import org.master.unitoo.core.errors.TypeConvertExpection;
import org.master.unitoo.core.errors.XMLTranformException;
import org.master.unitoo.core.types.ComponentContext;
import org.master.unitoo.core.types.ComponentType;
import org.master.unitoo.core.types.DateTime;
import org.master.unitoo.core.types.Time;
import org.master.unitoo.core.utils.GsonDateConvertor;
import org.master.unitoo.core.utils.GsonDateTimeConvertor;
import org.master.unitoo.core.utils.GsonJsonObjectConvertor;
import org.master.unitoo.core.utils.GsonTimeConvertor;

/**
 *
 * @author Andrey
 */
public abstract class BaseFormatter implements IFormatter {

    private ComponentContext context;
    private final ThreadLocal<Charset> fmtCharset = new InheritableThreadLocal<>();
    private final ThreadLocal<DateFormat> fmtDate = new InheritableThreadLocal<>();
    private final ThreadLocal<DateFormat> fmtTime = new InheritableThreadLocal<>();
    private final ThreadLocal<DateFormat> fmtDateTime = new InheritableThreadLocal<>();
    private final ThreadLocal<NumberFormat> fmtDecimal = new InheritableThreadLocal<>();
    private final ThreadLocal<NumberFormat> fmtInteger = new InheritableThreadLocal<>();
    private final ThreadLocal<Transformer> fmtXML = new InheritableThreadLocal<>();
    private final ThreadLocal<Gson> fmtGson = new InheritableThreadLocal<>();

    protected abstract DateFormat createDateFormat();

    protected abstract DateFormat createTimeFormat();

    protected abstract DateFormat createDateTimeFormat();

    protected abstract NumberFormat createDecimalFormat();

    protected abstract NumberFormat createIntegerFormat();

    protected abstract Charset createCharset();

    protected Transformer createXMLTransformer() throws XMLTranformException {
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            return transformer;
        } catch (IllegalArgumentException | TransformerConfigurationException | TransformerFactoryConfigurationError e) {
            throw new XMLTranformException(e);
        }
    }

    protected Gson createGson() {
        return new GsonBuilder()
                .registerTypeAdapter(Date.class, new GsonDateConvertor(this))
                .registerTypeAdapter(Time.class, new GsonTimeConvertor(this))
                .registerTypeAdapter(DateTime.class, new GsonDateTimeConvertor(this))
                .registerTypeHierarchyAdapter(IJsonObject.class, new GsonJsonObjectConvertor(this))
                .disableHtmlEscaping()
                .create();
    }

    @Override
    public Charset encoding() {
        Charset format = fmtCharset.get();
        if (format == null) {
            format = createCharset();
            fmtCharset.set(format);
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
    public Transformer xml() throws XMLTranformException {
        Transformer transformer = fmtXML.get();
        if (transformer == null) {
            transformer = createXMLTransformer();
            fmtXML.set(transformer);
        }
        return transformer;
    }

    @Override
    public Gson gson() {
        Gson gson = fmtGson.get();
        if (gson == null) {
            gson = createGson();
            fmtGson.set(gson);
        }
        return gson;
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
    public IApplication app() {
        return context.application();
    }

    @Override
    public String info() {
        return context.info();
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
    public String format(Object obj, IFormatContext context) {
        return app().format(this, obj, context);
    }

    @Override
    public <T> T parse(String value, Class<T> clazz) throws TypeConvertExpection {
        return app().parse(this, value, clazz);
    }

    @Override
    public <T> T parse(String value, Class<T> clazz, IFormatContext context) throws TypeConvertExpection {
        return app().parse(this, value, clazz, context);
    }

    public IFormatContext currentFormatContext() {
        return ((BaseApplication) app()).currentFormatContext();
    }

    public void enterFormatContext(IFormatContext context) {
        ((BaseApplication) app()).enterFormatContext(context);
    }

    public void exitFormatContext() {
        ((BaseApplication) app()).exitFormatContext();
    }

}
