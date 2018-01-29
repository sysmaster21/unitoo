/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.api.synthetic;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author Andrey
 */
public class BytesRequestFile extends RequestFile {

    private final byte[] file;
    private final String mime;
    private final String encoding;
    private final String name;

    public BytesRequestFile(byte[] file, String name, String mime, String encoding) {
        this.file = file;
        this.name = name;
        this.mime = mime;
        this.encoding = encoding;
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
    public String encoding() {
        return encoding;
    }

    @Override
    public InputStream stream() throws IOException {
        return new ByteArrayInputStream(file);
    }

}
