/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

/**
 *
 * @author Andrey
 */
public class UniToo {

    /**
     *
     */
    public final static int BOOT_PRIORITY_INIT = 0;
    public final static int BOOT_PRIORITY_CORE = 1;
    public final static int BOOT_PRIORITY_FIRST = 2;
    public final static int BOOT_PRIORITY_NORMAL = 3;
    public final static int BOOT_PRIORITY_LAST = 4;

    public static void Copy(final InputStream input, final OutputStream output) throws IOException {
        try (ReadableByteChannel src = Channels.newChannel(input)) {
            try (WritableByteChannel dest = Channels.newChannel(output)) {
                final ByteBuffer buffer = ByteBuffer.allocateDirect(16 * 1024);
                while (src.read(buffer) != -1) {
                    buffer.flip();
                    dest.write(buffer);
                    buffer.compact();
                }
                buffer.flip();
                while (buffer.hasRemaining()) {
                    dest.write(buffer);
                }
            }
        }
    }

    public static void Copy(File from, File to) throws IOException {
        FileChannel inChannel = new FileInputStream(from).getChannel();
        FileChannel outChannel = new FileOutputStream(to).getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } catch (IOException e) {
            throw e;
        } finally {
            if (inChannel != null) {
                inChannel.close();
            }
            if (outChannel != null) {
                outChannel.close();
            }
        }
    }

}
