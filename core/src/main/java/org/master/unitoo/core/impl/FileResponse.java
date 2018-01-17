/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.impl;

import java.io.IOException;
import java.io.InputStream;
import javax.servlet.http.HttpServletResponse;
import org.master.unitoo.core.UniToo;
import org.master.unitoo.core.api.IHttpFlushable;

/**
 *
 * @author Andrey
 */
public class FileResponse implements IHttpFlushable {

    private String encoding;
    private String mime;
    private String name;
    private byte[] dataArray;
    private InputStream dataStream;

    public String encoding() {
        return encoding;
    }

    public FileResponse encoding(String encoding) {
        this.encoding = encoding;
        return this;
    }

    public String mime() {
        return mime;
    }

    public FileResponse mime(String mime) {
        this.mime = mime;
        return this;
    }

    public String name() {
        return name;
    }

    public FileResponse name(String name) {
        this.name = name;
        return this;
    }

    public FileResponse data(byte[] data) {
        this.dataArray = data;
        return this;
    }

    public FileResponse stream(InputStream stream) {
        this.dataStream = stream;
        return this;
    }

    @Override
    public void flush(HttpServletResponse response) throws IOException {
        response.setContentType(encoding == null || encoding.isEmpty() ? mime : mime + "; charset=" + encoding);
        response.setHeader("Content-Disposition", "attachment; filename=\"" + name + "\"");

        if (dataArray != null) {
            response.getOutputStream().write(dataArray);
        }

        if (dataStream != null) {
            UniToo.Copy(dataStream, response.getOutputStream());
        }
    }

}
