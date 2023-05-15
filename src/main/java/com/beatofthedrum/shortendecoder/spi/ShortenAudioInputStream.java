package com.beatofthedrum.shortendecoder.spi;

import com.beatofthedrum.shortendecoder.ShortenContext;
import com.beatofthedrum.shortendecoder.ShortenUtils;

import javax.sound.sampled.AudioFormat;
import java.io.EOFException;
import java.io.IOException;

class ShortenAudioInputStream extends AsynchronousAudioInputStream {

    private final ShortenContext sc;

    ShortenAudioInputStream(ShortenContext sc, AudioFormat format, long length) throws IOException {
        super(sc.shn_stream, format, length);
        this.sc = sc;
    }

    @Override
    public void execute() {
        try {
            int bytes_unpacked = (int) ShortenUtils.DecodeBuffer(sc);
            if (sc.error) {
                if (sc.error_message instanceof IOException) throw (IOException) sc.error_message;
                else throw new IOException(sc.error_message);
            }

            if (bytes_unpacked > 0) {
                buffer.write(sc.buffer, 0, bytes_unpacked);
            } else if (bytes_unpacked == 0) throw new EOFException();
        }
        catch (IOException e) {
            buffer.close();
        }
    }

}
