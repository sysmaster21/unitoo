/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.base;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.master.unitoo.core.api.IApplication;
import org.master.unitoo.core.api.IBootInfo;
import org.master.unitoo.core.api.IBusinessField;
import org.master.unitoo.core.api.IBusinessObject;
import org.master.unitoo.core.types.ComponentContext;
import org.master.unitoo.core.types.ComponentType;
import org.master.unitoo.core.api.IProcessContext;
import org.master.unitoo.core.api.IProcessSnapshot;
import org.master.unitoo.core.api.components.IFormatter;
import org.master.unitoo.core.types.Decision;
import org.master.unitoo.core.api.IDataContent;

/**
 *
 * @author Andrey
 */
public abstract class BaseDataContent implements IDataContent {

    private ComponentContext context;
    private final ConcurrentHashMap<String, ConverterContext> classes = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, ConverterContext> fields = new ConcurrentHashMap<>();

    @Override
    public void init(ComponentContext context) {
        this.context = context;
        doMappings(new Mapping() {
            @Override
            public ConverterContext registerObject(Class<? extends IBusinessObject> clazz) {
                ConverterContext context = new ConverterContext();
                classes.put(clazz.getName(), context);
                return context;
            }

            @Override
            public ConverterContext registerMap(Class<? extends Map> clazz) {
                ConverterContext context = new ConverterContext();
                classes.put(clazz.getName(), context);
                return context;
            }

            @Override
            public ConverterContext registerList(Class<? extends Collection> clazz) {
                ConverterContext context = new ConverterContext();
                classes.put(clazz.getName(), context);
                return context;
            }

            @Override
            public ConverterContext registerField(Class<? extends IBusinessObject> clazz, String field) {
                ConverterContext context = new ConverterContext();
                fields.put(clazz.getName() + "." + field, context);
                return context;
            }
        });
    }

    @Override
    public String getRootName() {
        return "object";
    }

    protected void doMappings(Mapping mapping) {

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
    public String internal() {
        return context.internal();
    }

    @Override
    public String info() {
        return context.description();
    }

    @Override
    public ComponentType type() {
        return ComponentType.DataContent;
    }

    @Override
    public IBootInfo boot() {
        return context.boot();
    }

    @Override
    public String getAttributeName(IBusinessObject object, IBusinessField field) {
        ConverterContext cvt = findContext(object, field);
        if (cvt != null && cvt.name != null && !cvt.name.isEmpty()) {
            return cvt.name;
        } else {
            return field.name();
        }
    }

    @Override
    public String getItemName(IBusinessObject object, IBusinessField field) {
        ConverterContext cvt = findContext(object, field);
        if (cvt != null && cvt.itemName != null && !cvt.itemName.isEmpty()) {
            return cvt.itemName;
        } else {
            return "item";
        }
    }

    @Override
    public String getItemKeyName(IBusinessObject object, IBusinessField field) {
        ConverterContext cvt = findContext(object, field);
        if (cvt != null && cvt.keyName != null && !cvt.keyName.isEmpty()) {
            return cvt.keyName;
        } else {
            return "key";
        }
    }

    @Override
    public String getItemValueName(IBusinessObject object, IBusinessField field) {
        ConverterContext cvt = findContext(object, field);
        if (cvt != null && cvt.valueName != null && !cvt.valueName.isEmpty()) {
            return cvt.valueName;
        } else {
            return "value";
        }
    }

    @Override
    public boolean asAttribute(IBusinessObject object, IBusinessField field) {
        ConverterContext cvt = findContext(object, field);
        if (cvt != null && cvt.useAttributes != Decision.Parent) {
            return cvt.useAttributes == Decision.Use;
        } else {
            return Map.class.isAssignableFrom(field.type());
        }
    }

    private ConverterContext findContext(IBusinessObject object, IBusinessField field) {
        ConverterContext cvt = fields.get(object.getClass().getName() + "." + field.name());
        cvt = cvt == null ? classes.get(object.getClass().getName()) : cvt;
        return cvt;
    }

    protected IProcessSnapshot prepareContext(IBusinessObject object, IBusinessField field, IFormatter formatter) {
        final IProcessContext ctx = app().process().context();
        IProcessSnapshot snapshot = ctx.save();
        ConverterContext cvt = findContext(object, field);
        if (cvt != null) {
            if (cvt.escape != Decision.Parent) {
                ctx.escape(cvt.escape == Decision.Use);
            }

            if (cvt.trim != Decision.Parent) {
                ctx.trim(cvt.trim == Decision.Use);
            }

        } else {
            if (field.escape() != Decision.Parent) {
                ctx.escape(field.escape() == Decision.Use);
            }

            if (field.trim() != Decision.Parent) {
                ctx.trim(field.trim() == Decision.Use);
            }
        }
        return snapshot;
    }

    protected void restoreContext(IProcessSnapshot snapshot) {
        app().process().context().restore(snapshot);
    }

    @Override
    public IProcessSnapshot beforeSerialize(IBusinessObject object, IBusinessField field, IFormatter formatter) {
        return prepareContext(object, field, formatter);
    }

    @Override
    public void afterSerialize(IProcessSnapshot snapshot) {
        restoreContext(snapshot);
    }

    @Override
    public IProcessSnapshot beforeDeserialize(IBusinessObject object, IBusinessField field, IFormatter formatter) {
        return prepareContext(object, field, formatter);
    }

    @Override
    public void afterDeserialize(IProcessSnapshot snapshot) {
        restoreContext(snapshot);
    }

    protected interface Mapping {

        ConverterContext registerObject(Class<? extends IBusinessObject> clazz);

        ConverterContext registerMap(Class<? extends Map> clazz);

        ConverterContext registerList(Class<? extends Collection> clazz);

        ConverterContext registerField(Class<? extends IBusinessObject> clazz, String field);
    }

    protected class ConverterContext {

        private Decision escape = Decision.Parent;
        private Decision trim = Decision.Parent;
        private Decision useAttributes = Decision.Parent;
        private String name = "";
        private String itemName = "";
        private String keyName = "";
        private String valueName = "";

        public ConverterContext escape(Decision escape) {
            this.escape = escape;
            return this;
        }

        public ConverterContext trim(Decision trim) {
            this.trim = trim;
            return this;
        }

        public ConverterContext useAttributes(Decision useAttributes) {
            this.useAttributes = useAttributes;
            return this;
        }

        public ConverterContext name(String name) {
            this.name = name;
            return this;
        }

        public ConverterContext itemName(String itemName) {
            this.itemName = itemName;
            return this;
        }

        public ConverterContext keyName(String keyName) {
            this.keyName = keyName;
            return this;
        }

        public ConverterContext valueName(String valueName) {
            this.valueName = valueName;
            return this;
        }

    }
}
