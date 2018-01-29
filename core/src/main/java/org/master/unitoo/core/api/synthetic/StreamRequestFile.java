/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.api.synthetic;

import java.io.IOException;
import java.io.InputStream;
import org.apache.http.entity.ContentType;

/**
 *
 * @author Andrey
 */
public class StreamRequestFile extends RequestFile {

    private final InputStream file;
    private final String mime;
    private final String encoding;
    private final String name;

    public StreamRequestFile(InputStream file, String name, String mime, String encoding) {
        this.file = file;
        this.mime = mime;
        this.name = name;
        this.encoding = encoding;
    }

    public StreamRequestFile(InputStream file, String name, ContentType contentType) {
        this(file, name, contentType.getMimeType(), contentType.getCharset() == null ? null : contentType.getCharset().name());
    }

    @Override
    public String mime() {
        return mime;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public InputStream stream() throws IOException {
        return file;
    }

    @Override
    public String encoding() {
        return encoding;
    }

}
