/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.types;

import java.io.InputStream;
import org.apache.http.entity.ContentType;
import org.master.unitoo.core.api.synthetic.IJsonObject;
import org.w3c.dom.Document;

/**
 *
 * @author Andrey
 */
public class MIME {

    public final static String PLAIN = "text/plain";
    public final static String HTML = "text/html";
    public final static String XML = "text/xml";
    public final static String JSON = "application/json";
    public final static String BINARY = "application/octet-stream";
    public final static String X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";
    public final static String MULTIPART_FORM_DATA = "multipart/form-data";

    public static String ByClass(Class clazz, String def) {
        if (Document.class.isAssignableFrom(clazz)) {
            return MIME.XML;
        } else if (IJsonObject.class.isAssignableFrom(clazz)) {
            return MIME.JSON;
        } else if (byte[].class.isAssignableFrom(clazz)) {
            return MIME.BINARY;
        } else if (InputStream.class.isAssignableFrom(clazz)) {
            return MIME.BINARY;
        } else if (String.class.isAssignableFrom(clazz)) {
            return MIME.HTML;
        } else {
            return def;
        }
    }

    public static ContentType ByClass(Class clazz, ContentType def) {
        if (Document.class.isAssignableFrom(clazz)) {
            return ContentType.TEXT_XML;
        } else if (IJsonObject.class.isAssignableFrom(clazz)) {
            return ContentType.APPLICATION_JSON;
        } else if (byte[].class.isAssignableFrom(clazz)) {
            return ContentType.APPLICATION_OCTET_STREAM;
        } else if (InputStream.class.isAssignableFrom(clazz)) {
            return ContentType.APPLICATION_OCTET_STREAM;
        } else if (String.class.isAssignableFrom(clazz)) {
            return ContentType.TEXT_HTML;
        } else {
            return def;
        }
    }

    public static boolean WithCharset(String test) {
        if (test != null) {
            switch (test) {
                case PLAIN:
                case HTML:
                case XML:
                case JSON:
                    return true;
            }
        }
        return false;
    }

    public static boolean WithBoundary(String test) {
        if (test != null) {
            switch (test) {
                case MULTIPART_FORM_DATA:
                    return true;
            }
        }
        return false;
    }

    public static ContentType ToContentType(String mime) {
        if (mime != null) {
            switch (mime) {
                case PLAIN:
                    return ContentType.TEXT_PLAIN;
                case HTML:
                    return ContentType.TEXT_HTML;
                case XML:
                    return ContentType.TEXT_XML;
                case JSON:
                    return ContentType.APPLICATION_JSON;
            }
        }
        return ContentType.APPLICATION_OCTET_STREAM;
    }
}
