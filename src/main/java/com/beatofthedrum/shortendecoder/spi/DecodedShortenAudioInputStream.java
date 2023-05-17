package com.beatofthedrum.shortendecoder.spi;

import com.beatofthedrum.shortendecoder.ShortenContext;
import com.beatofthedrum.shortendecoder.ShortenUtils;

import javax.sound.sampled.AudioFormat;
import java.io.EOFException;
import java.io.IOException;

import static com.beatofthedrum.shortendecoder.spi.ShortenAudioFormat.Encoding.fromFType;

class DecodedShortenAudioInputStream extends AsynchronousAudioInputStream {

    private final ShortenContext sc;

    DecodedShortenAudioInputStream(ShortenAudioInputStream stream) throws IOException {
        this(stream.getShortenContext(), getDecodedFormat(
                stream.getShortenContext(),
                stream.getFormat()), stream.getFrameLength());
    }

    private DecodedShortenAudioInputStream(ShortenContext sc, AudioFormat format, long length) throws IOException {
        super(sc.shn_stream, format, length);
        this.sc = sc;
    }

    private static AudioFormat getDecodedFormat(ShortenContext sc, AudioFormat sourceFormat) {
        return new AudioFormat(
                fromFType(sc.ftype),
                sourceFormat.getSampleRate(),
                sourceFormat.getSampleSizeInBits(),
                sourceFormat.getChannels(),
                sourceFormat.getFrameSize(),
                sourceFormat.getFrameRate(),
                sourceFormat.isBigEndian(),
                sourceFormat.properties()
        );
    }

    @Override
    public void execute() {
        try {
            int bytes_unpacked = (int) ShortenUtils.DecodeBuffer(sc);
            if (sc.error) throw sc.error_message;

            if (bytes_unpacked > 0) {
                buffer.write(sc.buffer, 0, bytes_unpacked);
            } else if (bytes_unpacked == 0) throw new EOFException();
        }
        catch (IOException e) {
            buffer.close();
        }
    }

}
