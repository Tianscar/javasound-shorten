package com.beatofthedrum.shortendecoder.spi;

import com.beatofthedrum.shortendecoder.ShortenContext;
import com.beatofthedrum.shortendecoder.ShortenException;
import com.beatofthedrum.shortendecoder.ShortenInputStream;
import com.beatofthedrum.shortendecoder.ShortenUtils;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.spi.AudioFileReader;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import static com.beatofthedrum.shortendecoder.spi.ShortenAudioFileFormatType.SHN;
import static java.nio.file.StandardOpenOption.READ;
import static javax.sound.sampled.AudioSystem.NOT_SPECIFIED;

public class ShortenAudioFileReader extends AudioFileReader {

    @Override
    public AudioFileFormat getAudioFileFormat(InputStream stream) throws UnsupportedAudioFileException, IOException {
        final ShortenContext sc;
        if (stream instanceof ShortenInputStream) sc = ShortenUtils.ShortenOpenInput((ShortenInputStream) stream);
        else {
            stream.mark(1000);
            sc = ShortenUtils.ShortenOpenFileInput(stream);
        }
        if (sc.error) {
            if (!(stream instanceof ShortenInputStream)) stream.reset();
            throwExceptions(sc);
        }
        return getAudioFileFormat(sc, new HashMap<>(), new HashMap<>());
    }

    @Override
    public AudioFileFormat getAudioFileFormat(URL url) throws UnsupportedAudioFileException, IOException {
        ShortenContext sc = ShortenUtils.ShortenOpenFileInput(url.openStream());
        throwExceptions(sc);
        try {
            return getAudioFileFormat(sc, new HashMap<>(), new HashMap<>());
        }
        finally {
            ShortenUtils.ShortenCloseFile(sc);
        }
    }

    @Override
    public AudioFileFormat getAudioFileFormat(File file) throws UnsupportedAudioFileException, IOException {
        ShortenContext sc = ShortenUtils.ShortenOpenFileInput(Files.newInputStream(file.toPath(), READ));
        throwExceptions(sc);
        try {
            return getAudioFileFormat(sc, new HashMap<>(), new HashMap<>());
        }
        finally {
            ShortenUtils.ShortenCloseFile(sc);
        }
    }

    @Override
    public AudioInputStream getAudioInputStream(InputStream stream) throws UnsupportedAudioFileException, IOException {
        if (stream instanceof ShortenInputStream) {
            ShortenContext sc = ShortenUtils.ShortenOpenInput((ShortenInputStream) stream);
            throwExceptions(sc);
            return new ShortenAudioInputStream(sc, getAudioFormat(sc, new HashMap<>()), ShortenUtils.ShortenGetNumSamples(sc));
        }
        stream.mark(1000);
        try {
            ShortenContext sc = ShortenUtils.ShortenOpenFileInput(stream);
            throwExceptions(sc);
            return new ShortenAudioInputStream(sc, getAudioFormat(sc, new HashMap<>()), ShortenUtils.ShortenGetNumSamples(sc));
        }
        catch (UnsupportedAudioFileException | IOException e) {
            stream.reset();
            throw e;
        }
    }

    @Override
    public AudioInputStream getAudioInputStream(URL url) throws UnsupportedAudioFileException, IOException {
        InputStream stream = url.openStream();
        try {
            ShortenContext sc = ShortenUtils.ShortenOpenFileInput(stream);
            throwExceptions(sc);
            return new ShortenAudioInputStream(sc, getAudioFormat(sc, new HashMap<>()), ShortenUtils.ShortenGetNumSamples(sc));
        }
        catch (UnsupportedAudioFileException | IOException e) {
            stream.close();
            throw e;
        }
    }

    @Override
    public AudioInputStream getAudioInputStream(File file) throws UnsupportedAudioFileException, IOException {
        InputStream stream = Files.newInputStream(file.toPath(), READ);
        try {
            ShortenContext sc = ShortenUtils.ShortenOpenFileInput(stream);
            throwExceptions(sc);
            return new ShortenAudioInputStream(sc, getAudioFormat(sc, new HashMap<>()), ShortenUtils.ShortenGetNumSamples(sc));
        }
        catch (UnsupportedAudioFileException | IOException e) {
            stream.close();
            throw e;
        }
    }

    private static void throwExceptions(ShortenContext sc) throws UnsupportedAudioFileException, IOException {
        if (sc.error) {
            if (sc.error_message instanceof ShortenException) throw new UnsupportedAudioFileException();
            else if (sc.error_message instanceof IOException) throw (IOException) sc.error_message;
            else throw new IOException(sc.error_message);
        }
    }

    private static AudioFileFormat getAudioFileFormat(ShortenContext sc,
                                                      Map<String, Object> fileProperties,
                                                      Map<String, Object> formatProperties) {
        int samples = ShortenUtils.ShortenGetNumSamples(sc);
        int sample_rate = ShortenUtils.ShortenGetSampleRate(sc);
        int channels = ShortenUtils.ShortenGetNumChannels(sc);
        int bytes_per_sample = ShortenUtils.ShortenGetBytesPerSample(sc);
        int bits_per_sample = ShortenUtils.ShortenGetBitsPerSample(sc);
        formatProperties.put("samples", samples);
        formatProperties.put("samplerate", sample_rate);
        formatProperties.put("samplesizeinbytes", bytes_per_sample);
        formatProperties.put("samplesizeinbits", bits_per_sample);
        formatProperties.put("channels", channels);
        formatProperties.put("bigendian", false);
        fileProperties.put("shn.version", sc.version);
        fileProperties.put("shn.ftype", sc.ftype);
        fileProperties.put("shn.originalftype", sc.original_ftype);
        fileProperties.put("shn.datasize", sc.datasize);
        return new AudioFileFormat(SHN,
                new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, sample_rate, bits_per_sample,
                channels, frameSize(channels, bits_per_sample),
                sample_rate, false, formatProperties), samples, fileProperties);
    }

    private static AudioFormat getAudioFormat(ShortenContext sc, Map<String, Object> formatProperties) {
        int samples = ShortenUtils.ShortenGetNumSamples(sc);
        int sample_rate = ShortenUtils.ShortenGetSampleRate(sc);
        int channels = ShortenUtils.ShortenGetNumChannels(sc);
        int bytes_per_sample = ShortenUtils.ShortenGetBytesPerSample(sc);
        int bits_per_sample = ShortenUtils.ShortenGetBitsPerSample(sc);
        formatProperties.put("samples", samples);
        formatProperties.put("samplerate", sample_rate);
        formatProperties.put("samplesizeinbytes", bytes_per_sample);
        formatProperties.put("samplesizeinbits", bits_per_sample);
        formatProperties.put("channels", channels);
        formatProperties.put("bigendian", false);
        return new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, sample_rate, bits_per_sample,
                channels, frameSize(channels, bits_per_sample),
                sample_rate, false, formatProperties);
    }

    private static int frameSize(int channels, int sampleSizeInBits) {
        return (channels == NOT_SPECIFIED || sampleSizeInBits == NOT_SPECIFIED)?
                NOT_SPECIFIED:
                ((sampleSizeInBits + 7) / 8) * channels;
    }

}
