/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.api.synthetic;

import java.io.IOException;
import java.io.InputStream;
import org.apache.http.entity.ContentType;
import org.apache.tomcat.util.http.fileupload.FileItem;

/**
 *
 * @author Andrey
 */
public class IncomeRequestFile extends RequestFile {

    private final FileItem file;

    public IncomeRequestFile(FileItem file) {
        this.file = file;
    }

    @Override
    public String mime() {
        return ContentType.parse(file.getContentType()).getMimeType();
    }

    @Override
    public String name() {
        return file.getName();
    }

    @Override
    public InputStream stream() throws IOException {
        return file.getInputStream();
    }

    @Override
    public String encoding() {
        return ContentType.parse(file.getContentType()).getCharset().name();
    }

}
