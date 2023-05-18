package com.beatofthedrum.shortendecoder.spi;

import com.beatofthedrum.shortendecoder.ShortenContext;
import com.beatofthedrum.shortendecoder.ShortenException;
import com.beatofthedrum.shortendecoder.ShortenUtils;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.spi.AudioFileReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;

import static java.nio.file.StandardOpenOption.READ;
import static javax.sound.sampled.AudioSystem.NOT_SPECIFIED;

public class ShortenAudioFileReader extends AudioFileReader {

    @Override
    public AudioFileFormat getAudioFileFormat(InputStream stream) throws UnsupportedAudioFileException, IOException {
        final ShortenContext sc;
        if (stream instanceof java.io.DataInputStream) sc = ShortenUtils.ShortenOpenFileInput((java.io.DataInputStream) stream);
        else {
            stream.mark(1000);
            sc = ShortenUtils.ShortenOpenFileInput(new DataInputStream(stream));
        }
        if (sc.error) {
            if (!(stream instanceof java.io.DataInputStream)) stream.reset();
            throwExceptions(sc);
        }
        return new ShortenAudioFileFormat(sc, NOT_SPECIFIED);
    }

    @Override
    public AudioFileFormat getAudioFileFormat(URL url) throws UnsupportedAudioFileException, IOException {
        URLConnection connection = url.openConnection();
        ShortenContext sc = ShortenUtils.ShortenOpenFileInput(new DataInputStream(connection.getInputStream()));
        try {
            throwExceptions(sc);
            return new ShortenAudioFileFormat(sc, connection.getContentLengthLong());
        }
        finally {
            ShortenUtils.ShortenCloseFile(sc);
        }
    }

    @Override
    public AudioFileFormat getAudioFileFormat(File file) throws UnsupportedAudioFileException, IOException {
        ShortenContext sc = ShortenUtils.ShortenOpenFileInput(new DataInputStream(Files.newInputStream(file.toPath(), READ)));
        try {
            throwExceptions(sc);
            return new ShortenAudioFileFormat(sc, file.length());
        }
        finally {
            ShortenUtils.ShortenCloseFile(sc);
        }
    }

    @Override
    public AudioInputStream getAudioInputStream(InputStream stream) throws UnsupportedAudioFileException, IOException {
        if (stream instanceof java.io.DataInputStream) {
            ShortenContext sc = ShortenUtils.ShortenOpenFileInput((java.io.DataInputStream) stream);
            throwExceptions(sc);
            return new ShortenAudioInputStream(sc, NOT_SPECIFIED);
        }
        stream.mark(1000);
        try {
            ShortenContext sc = ShortenUtils.ShortenOpenFileInput(new DataInputStream(stream));
            throwExceptions(sc);
            return new ShortenAudioInputStream(sc, NOT_SPECIFIED);
        }
        catch (UnsupportedAudioFileException | IOException e) {
            stream.reset();
            throw e;
        }
    }

    @Override
    public AudioInputStream getAudioInputStream(URL url) throws UnsupportedAudioFileException, IOException {
        URLConnection connection = url.openConnection();
        InputStream stream = connection.getInputStream();
        try {
            ShortenContext sc = ShortenUtils.ShortenOpenFileInput(new DataInputStream(stream));
            throwExceptions(sc);
            return new ShortenAudioInputStream(sc, connection.getContentLengthLong());
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
            ShortenContext sc = ShortenUtils.ShortenOpenFileInput(new DataInputStream(stream));
            throwExceptions(sc);
            return new ShortenAudioInputStream(sc, file.length());
        }
        catch (UnsupportedAudioFileException | IOException e) {
            stream.close();
            throw e;
        }
    }

    private static void throwExceptions(ShortenContext sc) throws UnsupportedAudioFileException, IOException {
        if (sc.error) {
            if (sc.error_message instanceof ShortenException) throw new UnsupportedAudioFileException();
            else throw sc.error_message;
        }
    }

}
