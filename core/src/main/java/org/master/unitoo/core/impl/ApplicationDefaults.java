/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.http.entity.ContentType;
import org.master.unitoo.core.api.IApplication;
import org.master.unitoo.core.api.IApplicationDefaults;
import org.master.unitoo.core.api.components.IErrorHandler;
import org.master.unitoo.core.api.components.IFormatter;
import org.master.unitoo.core.api.components.ILanguage;
import org.master.unitoo.core.api.IDataContent;

/**
 *
 * @author Andrey
 */
public class ApplicationDefaults implements IApplicationDefaults {

    private boolean escapeControllerParams = false;
    private boolean escapeControllerResult = true;
    private boolean escapeExternalParams = false;
    private boolean escapeExternalResult = false;
    private boolean trimControllerParams = true;
    private boolean trimControllerResult = true;
    private boolean trimExternalParams = true;
    private boolean trimExternalResult = true;
    private boolean strictMime = false;
    private final Map<String, IDataContent> defContent = new ConcurrentHashMap<>();
    private final Map<String, String> defHeaders = new ConcurrentHashMap<>();
    private IFormatter defFormat;
    private IErrorHandler defHandler;
    private ILanguage defLanguage;
    private final IApplication app;

    public ApplicationDefaults(IApplication app) {
        this.app = app;
    }

    @Override
    public boolean isEscapeControllerParams() {
        return escapeControllerParams;
    }

    public void setEscapeControllerParams(boolean escapeControllerParams) {
        this.escapeControllerParams = escapeControllerParams;
    }

    @Override
    public boolean isEscapeControllerResult() {
        return escapeControllerResult;
    }

    public void setEscapeControllerResult(boolean escapeControllerResult) {
        this.escapeControllerResult = escapeControllerResult;
    }

    @Override
    public boolean isEscapeExternalParams() {
        return escapeExternalParams;
    }

    public void setEscapeExternalParams(boolean escapeExternalParams) {
        this.escapeExternalParams = escapeExternalParams;
    }

    @Override
    public boolean isEscapeExternalResult() {
        return escapeExternalResult;
    }

    public void setEscapeExternalResult(boolean escapeExternalResult) {
        this.escapeExternalResult = escapeExternalResult;
    }

    @Override
    public boolean isTrimControllerParams() {
        return trimControllerParams;
    }

    public void setTrimControllerParams(boolean trimControllerParams) {
        this.trimControllerParams = trimControllerParams;
    }

    @Override
    public boolean isTrimControllerResult() {
        return trimControllerResult;
    }

    public void setTrimControllerResult(boolean trimControllerResult) {
        this.trimControllerResult = trimControllerResult;
    }

    @Override
    public boolean isTrimExternalParams() {
        return trimExternalParams;
    }

    public void setTrimExternalParams(boolean trimExternalParams) {
        this.trimExternalParams = trimExternalParams;
    }

    @Override
    public boolean isTrimExternalResult() {
        return trimExternalResult;
    }

    public void setTrimExternalResult(boolean trimExternalResult) {
        this.trimExternalResult = trimExternalResult;
    }

    @Override
    public boolean isStrictMime() {
        return strictMime;
    }

    public void setStrictMime(boolean strictMime) {
        this.strictMime = strictMime;
    }

    @Override
    public IDataContent content(String mime) {
        if (mime == null || mime.isEmpty()) {
            return defContent.get(ContentType.TEXT_HTML.getMimeType());
        } else {
            IDataContent content = defContent.get(mime);
            return content == null ? defContent.get(ContentType.TEXT_HTML.getMimeType()) : content;
        }
    }

    public void addContent(Class<? extends IDataContent> defContent) {
        IDataContent content = app.component(defContent);
        this.defContent.put(content.contentType("UTF-8").getMimeType(), content);
    }

    public void addHeader(String name, String value) {
        this.defHeaders.put(name, value);
    }

    @Override
    public Map<String, String> headers() {
        return defHeaders;
    }

    @Override
    public IFormatter formatter() {
        return defFormat;
    }

    public void setFormatter(Class<? extends IFormatter> defFormat) {
        this.defFormat = app.component(defFormat);
    }

    @Override
    public IErrorHandler errorHandler() {
        return defHandler;
    }

    public void setErrorHandler(Class<? extends IErrorHandler> defHandler) {
        this.defHandler = app.component(defHandler);
    }

    @Override
    public ILanguage language() {
        return defLanguage;
    }

    public void setLanguage(Class<? extends ILanguage> language) {
        this.defLanguage = app.component(language);
    }

}
