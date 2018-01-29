/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.master.unitoo.core.api.IBusinessField;
import org.master.unitoo.core.api.IBusinessObject;
import org.master.unitoo.core.api.IObjectConvertor;
import org.master.unitoo.core.api.IProcessSnapshot;
import org.master.unitoo.core.api.components.IFormatter;
import org.master.unitoo.core.errors.XMLException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.master.unitoo.core.api.IDataContent;

/**
 *
 * @author Andrey
 */
public class XMLFormat implements IObjectConvertor {

    private final Transformer transformer;
    private final DocumentBuilder builder;
    private final IFormatter formatter;

    public XMLFormat(IFormatter format) throws XMLException {
        this.formatter = format;
        try {
            this.transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, formatter.encoding());
            transformer.setOutputProperty(OutputKeys.INDENT, "no");
            transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
            this.builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (IllegalArgumentException
                | ParserConfigurationException
                | TransformerConfigurationException
                | TransformerFactoryConfigurationError e) {
            throw new XMLException(e);
        }
    }

    public Transformer transformer() {
        return transformer;
    }

    public DocumentBuilder builder() {
        return builder;
    }

    @Override
    public void serialize(IBusinessObject object, OutputStream stream, IDataContent content) throws IOException {
        try {
            Document xml = builder.newDocument();
            Element root = xml.createElement(content.getRootName());
            xml.appendChild(root);
            serializeObject((IBusinessObject) object, root, content);
            StreamResult result = new StreamResult(new OutputStreamWriter(stream, formatter.encoding()));
            transformer.transform(new DOMSource(xml), result);
        } catch (TransformerException e) {
            throw new IOException(e);
        }
    }

    private void serializeMap(IBusinessObject object, IBusinessField field, Map<Object, Object> map, Element element, IDataContent content) throws IOException {
        String itemName = content.getItemName(object, field);
        String itemKeyName = content.getItemKeyName(object, field);
        String itemValueName = content.getItemValueName(object, field);
        for (Map.Entry<Object, Object> entry : map.entrySet()) {
            Element elementItem = element.getOwnerDocument().createElement(itemName);
            if (content.asAttribute(object, field)) {
                elementItem.setAttribute(itemKeyName, formatter.format(entry.getKey()));
                elementItem.setAttribute(itemValueName, formatter.format(entry.getValue()));
            } else {
                Element itemKey = element.getOwnerDocument().createElement(itemKeyName);
                if (entry.getKey() instanceof IBusinessObject) {
                    serializeObject((IBusinessObject) entry.getKey(), itemKey, content);
                } else {
                    itemKey.setTextContent(formatter.format(entry.getKey()));
                }
                elementItem.appendChild(itemKey);
                Element itemValue = element.getOwnerDocument().createElement(itemKeyName);
                if (entry.getValue() instanceof IBusinessObject) {
                    serializeObject((IBusinessObject) entry.getValue(), itemValue, content);
                } else {
                    itemValue.setTextContent(formatter.format(entry.getValue()));
                }
                elementItem.appendChild(itemValue);
            }
            element.appendChild(elementItem);
        }
    }

    private void serializeCollection(IBusinessObject object, IBusinessField field, Collection list, Element element, IDataContent content) throws IOException {
        String itemName = content.getItemName(object, field);
        String itemValueName = content.getItemValueName(object, field);
        for (Object item : list) {
            Element elementItem = element.getOwnerDocument().createElement(itemName);
            if (item instanceof IBusinessObject) {
                serializeObject((IBusinessObject) item, elementItem, content);
            } else if (content.asAttribute(object, field)) {
                elementItem.setAttribute(itemValueName, formatter.format(item));
            } else {
                elementItem.setTextContent(formatter.format(item));
            }
            element.appendChild(elementItem);
        }
    }

    private void serializeArray(IBusinessObject object, IBusinessField field, Object[] list, Element element, IDataContent content) throws IOException {
        String itemName = content.getItemName(object, field);
        String itemValueName = content.getItemValueName(object, field);
        for (Object item : list) {
            Element elementItem = element.getOwnerDocument().createElement(itemName);
            if (item instanceof IBusinessObject) {
                serializeObject((IBusinessObject) item, elementItem, content);
            } else if (content.asAttribute(object, field)) {
                elementItem.setAttribute(itemValueName, formatter.format(item));
            } else {
                elementItem.setTextContent(formatter.format(item));
            }
            element.appendChild(elementItem);
        }
    }

    @SuppressWarnings("UseSpecificCatch")
    private void serializeObject(IBusinessObject object, Element element, IDataContent content) throws IOException {
        for (IBusinessField field : formatter.app().businessFields(object.getClass())) {
            Object value = field.get(object);
            if (value != null) {
                try {
                    String name = content.getAttributeName(object, field);
                    IProcessSnapshot info = content.beforeSerialize(object, field, formatter);
                    try {
                        if (value instanceof IBusinessObject) {
                            Element item = element.getOwnerDocument().createElement(name);
                            serializeObject((IBusinessObject) value, item, content);
                            element.appendChild(item);
                        } else if (value instanceof Collection) {
                            Element item = element.getOwnerDocument().createElement(name);
                            serializeCollection(object, field, (Collection) value, item, content);
                            element.appendChild(item);
                        } else if (value instanceof Map) {
                            Element item = element.getOwnerDocument().createElement(name);
                            serializeMap(object, field, (Map) value, item, content);
                            element.appendChild(item);
                        } else if (field.type().isArray()) {
                            Element item = element.getOwnerDocument().createElement(name);
                            serializeArray(object, field, (Object[]) value, item, content);
                            element.appendChild(item);
                        } else {
                            if (content.asAttribute(object, field)) {
                                element.setAttribute(name, formatter.format(value));
                            } else {
                                Element attr = element.getOwnerDocument().createElement(name);
                                attr.setTextContent(formatter.format(value));
                                element.appendChild(attr);
                            }
                        }
                    } finally {
                        content.afterSerialize(info);
                    }
                } catch (Throwable e) {
                    throw new IOException("Failed to deserialize " + object.getClass().getName() + "." + field.name(), e);
                }
            }
        }
    }

    @Override
    public <T extends IBusinessObject> T deserialize(Class<T> clazz, InputStream stream, IDataContent content) throws IOException {
        try {
            Document xml = builder.parse(stream);
            T object = clazz.newInstance();
            deserializeObject(object, xml.getDocumentElement(), content);
            return object;
        } catch (IllegalAccessException | InstantiationException | SAXException e) {
            throw new IOException(e);
        }
    }

    private void deserializeMap(IBusinessObject object, IBusinessField field, Map<Object, Object> map, Element element, IDataContent content) throws IOException, IllegalAccessException, InstantiationException {
        String itemName = content.getItemName(object, field);
        String itemKeyName = content.getItemKeyName(object, field);
        String itemValueName = content.getItemValueName(object, field);

        NodeList items = element.getChildNodes();
        for (int i = 0; i < items.getLength(); i++) {
            Element item = (Element) items.item(i);
            if (itemName.equals(item.getTagName())) {
                Object key = null;
                Object value = null;
                if (content.asAttribute(object, field)) {
                    key = !item.hasAttribute(itemKeyName) ? null : formatter.parse(item.getAttribute(itemKeyName), field.keyType());
                    value = !item.hasAttribute(itemValueName) ? null : formatter.parse(item.getAttribute(itemValueName), field.itemType());
                } else {
                    NodeList pairs = item.getChildNodes();
                    for (int k = 0; k < pairs.getLength(); k++) {
                        Node pairNode = pairs.item(k);
                        if (itemKeyName.equals(pairNode.getNodeName())) {
                            if (IBusinessObject.class.isAssignableFrom(field.keyType())) {
                                value = field.keyType().newInstance();
                                deserializeObject((IBusinessObject) value, (Element) pairNode, content);
                            } else {
                                key = formatter.parse(pairNode.getTextContent(), field.keyType());
                            }
                        } else if (itemValueName.equals(pairNode.getNodeName())) {
                            if (IBusinessObject.class.isAssignableFrom(field.itemType())) {
                                value = field.itemType().newInstance();
                                deserializeObject((IBusinessObject) value, (Element) pairNode, content);
                            } else {
                                value = formatter.parse(pairNode.getTextContent(), field.itemType());
                            }
                        }
                    }
                }

                if (key != null && value != null) {
                    map.put(key, value);
                }
            }
        }
    }

    private void deserializeCollection(IBusinessObject object, IBusinessField field, Collection list, Element element, IDataContent content) throws IOException, IllegalAccessException, InstantiationException {
        String itemName = content.getItemName(object, field);
        String itemValueName = content.getItemValueName(object, field);

        NodeList items = element.getChildNodes();
        for (int i = 0; i < items.getLength(); i++) {
            Element item = (Element) items.item(i);
            if (itemName.equals(item.getTagName())) {
                Object value;
                if (content.asAttribute(object, field)) {
                    value = !item.hasAttribute(itemValueName) ? null : formatter.parse(item.getAttribute(itemValueName), field.itemType());
                } else {
                    value = formatter.parse(item.getTextContent(), field.itemType());
                }

                if (value != null) {
                    list.add(value);
                }
            }
        }
    }

    private Object[] deserializeArray(IBusinessObject object, IBusinessField field, Element element, IDataContent content) throws IOException, IllegalAccessException, InstantiationException, NegativeArraySizeException {
        String itemName = content.getItemName(object, field);
        String itemValueName = content.getItemValueName(object, field);
        ArrayList<Object> list = new ArrayList<>();

        NodeList items = element.getChildNodes();
        for (int i = 0; i < items.getLength(); i++) {
            Element item = (Element) items.item(i);
            if (itemName.equals(item.getTagName())) {
                Object value = null;
                if (content.asAttribute(object, field)) {
                    value = !item.hasAttribute(itemValueName) ? null : formatter.parse(item.getAttribute(itemValueName), field.itemType());
                } else {
                    NodeList pairs = item.getChildNodes();
                    for (int k = 0; k < pairs.getLength(); k++) {
                        Node pairNode = pairs.item(k);
                        if (itemValueName.equals(pairNode.getNodeName())) {
                            if (IBusinessObject.class.isAssignableFrom(field.itemType())) {
                                value = field.itemType().newInstance();
                                deserializeObject((IBusinessObject) value, (Element) pairNode, content);
                            } else {
                                value = formatter.parse(pairNode.getTextContent(), field.itemType());
                            }
                            break;
                        }
                    }
                }

                if (value != null) {
                    list.add(value);
                }
            }
        }

        return list.toArray((Object[]) Array.newInstance(field.itemType(), list.size()));
    }

    @SuppressWarnings("UseSpecificCatch")
    private void deserializeObject(IBusinessObject object, Element element, IDataContent content) throws IOException {
        Map<String, Element> childs = getChildsMap(element);
        Map<String, String> attrs = getAttributesMap(element);

        for (IBusinessField field : formatter.app().businessFields(object.getClass())) {
            try {
                String name = content.getAttributeName(object, field);
                IProcessSnapshot info = content.beforeDeserialize(object, field, formatter);

                try {
                    if (IBusinessObject.class.isAssignableFrom(field.type())) {
                        Element item = childs.get(name);
                        if (item != null) {
                            IBusinessObject value = (IBusinessObject) field.get(object);
                            value = value == null ? (IBusinessObject) field.type().newInstance() : value;
                            deserializeObject(value, item, content);
                            field.set(value, object);
                        }
                    } else if (Collection.class.isAssignableFrom(field.type())) {
                        Element item = childs.get(name);
                        if (item != null) {
                            Collection value = (Collection) field.get(object);
                            value = value == null ? (Collection) field.type().newInstance() : value;
                            deserializeCollection(object, field, value, item, content);
                            field.set(value, object);
                        }
                    } else if (Map.class.isAssignableFrom(field.type())) {
                        Element item = childs.get(name);
                        if (item != null) {
                            Map value = (Map) field.get(object);
                            value = value == null ? (Map) field.type().newInstance() : value;
                            deserializeMap(object, field, value, item, content);
                            field.set(value, object);
                        }
                    } else if (field.type().isArray()) {
                        Element item = childs.get(name);
                        if (item != null) {
                            field.set(deserializeArray(object, field, item, content), object);
                        }
                    } else {
                        if (content.asAttribute(object, field)) {
                            String value = attrs.get(name);
                            field.set(value == null ? null : formatter.parse(value, field.type()), object);
                        } else {
                            Element item = childs.get(name);
                            String value = item == null ? null : item.getTextContent();
                            field.set(value == null ? null : formatter.parse(value, field.type()), object);
                        }
                    }
                } finally {
                    content.afterDeserialize(info);
                }
            } catch (Throwable e) {
                throw new IOException("Failed to deserialize " + object.getClass().getName() + "." + field.name(), e);
            }
        }
    }

    private Map<String, Element> getChildsMap(Element element) {
        HashMap<String, Element> childs = new HashMap<>();
        NodeList elements = element.getChildNodes();
        for (int i = 0; i < elements.getLength(); i++) {
            Node child = elements.item(i);
            if (child instanceof Element) {
                childs.put(((Element) child).getTagName(), (Element) child);
            }
        }
        return childs;
    }

    private Map<String, String> getAttributesMap(Element element) {
        HashMap<String, String> childs = new HashMap<>();
        NamedNodeMap attrs = element.getAttributes();
        for (int i = 0; i < attrs.getLength(); i++) {
            Node child = attrs.item(i);
            childs.put(child.getNodeName(), child.getNodeValue());
        }
        return childs;
    }
}
