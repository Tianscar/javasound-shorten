/*
 * Adopted from https://github.com/umjammer/JAADec/blob/0.8.9/src/test/java/net/sourceforge/jaad/spi/javasound/AacFormatConversionProviderTest.java
 *
 * Copyright (c) 2022 by Naohide Sano, All rights reserved.
 * Copyright (c) 2023 by Karstian Lee, All rights reserved.
 *
 * Originally programmed by Naohide Sano
 * Modifications by Karstian Lee
 */

package com.beatofthedrum.shortendecoder.test;

import com.beatofthedrum.shortendecoder.spi.ShortenAudioFileReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ShortenTest {

    @Test
    @DisplayName("unsupported exception is able to detect in 3 ways")
    public void unsupported() {

        Path path = Paths.get("src/test/resources/fbodemo1_vorbis.ogg");

        assertThrows(UnsupportedAudioFileException.class, () -> {
            // don't replace with Files#newInputStream(Path)
            new ShortenAudioFileReader().getAudioInputStream(new BufferedInputStream(Files.newInputStream(path.toFile().toPath())));
        });

        assertThrows(UnsupportedAudioFileException.class, () -> {
            new ShortenAudioFileReader().getAudioInputStream(path.toFile());
        });

        assertThrows(UnsupportedAudioFileException.class, () -> {
            new ShortenAudioFileReader().getAudioInputStream(path.toUri().toURL());
        });
    }

    private void play(AudioInputStream pcmAis) throws LineUnavailableException, IOException {
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, pcmAis.getFormat());
        SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
        line.open();
        line.start();

        byte[] buf = new byte[128 * 6];
        while (true) {
            int r = pcmAis.read(buf, 0, buf.length);
            if (r < 0) {
                break;
            }
            line.write(buf, 0, r);
        }
        line.drain();
        line.stop();
        line.close();
    }

    private AudioInputStream decode(AudioInputStream shortenAis) {
        AudioFormat inAudioFormat = shortenAis.getFormat();
        AudioFormat decodedAudioFormat = new AudioFormat(
                AudioSystem.NOT_SPECIFIED,
                inAudioFormat.getSampleSizeInBits(),
                inAudioFormat.getChannels(),
                true,
                inAudioFormat.isBigEndian());
        return AudioSystem.getAudioInputStream(decodedAudioFormat, shortenAis);
    }

    @Test
    @DisplayName("shorten -> pcm, play via SPI")
    public void convertShortenToPCMAndPlay() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        File file = new File("src/test/resources/fbodemo1.shn");
        System.out.println("in file: " + file.getAbsolutePath());
        AudioInputStream shortenAis = AudioSystem.getAudioInputStream(file);
        System.out.println("in stream: " + shortenAis);
        AudioFormat inAudioFormat = shortenAis.getFormat();
        System.out.println("in audio format: " + inAudioFormat);

        AudioFormat decodedAudioFormat = new AudioFormat(
                AudioSystem.NOT_SPECIFIED,
                inAudioFormat.getSampleSizeInBits(),
                inAudioFormat.getChannels(),
                true,
                inAudioFormat.isBigEndian());

        assertTrue(AudioSystem.isConversionSupported(decodedAudioFormat, inAudioFormat));

        shortenAis = AudioSystem.getAudioInputStream(decodedAudioFormat, shortenAis);
        decodedAudioFormat = shortenAis.getFormat();
        System.out.println("decoded in stream: " + shortenAis);
        System.out.println("decoded audio format: " + decodedAudioFormat);

        AudioFormat outAudioFormat = new AudioFormat(
            decodedAudioFormat.getSampleRate(),
            16,
            decodedAudioFormat.getChannels(),
            true,
            false);

        assertTrue(AudioSystem.isConversionSupported(outAudioFormat, decodedAudioFormat));

        AudioInputStream pcmAis = AudioSystem.getAudioInputStream(outAudioFormat, shortenAis);
        System.out.println("out stream: " + pcmAis);
        System.out.println("out audio format: " + pcmAis.getFormat());

        play(pcmAis);
        pcmAis.close();
    }

    @Test
    @DisplayName("play Shorten from InputStream via SPI")
    public void playShortenInputStream() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("fbodemo1.shn");
        AudioInputStream shortenAis = decode(AudioSystem.getAudioInputStream(stream));
        play(shortenAis);
        shortenAis.close();
    }

    @Test
    @DisplayName("play Shorten from URL via SPI")
    public void playShortenURL() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        URL url = new URL("https://github.com/Tianscar/fbodemo1/raw/main/fbodemo1.shn");
        AudioInputStream shortenAis = decode(AudioSystem.getAudioInputStream(url));
        play(shortenAis);
        shortenAis.close();
    }

    @Test
    @DisplayName("list Shorten properties")
    public void listShortenProperties() throws UnsupportedAudioFileException, IOException {
        File file = new File("src/test/resources/fbodemo1.shn");
        AudioFileFormat shortenAff = AudioSystem.getAudioFileFormat(file);
        for (Map.Entry<String, Object> entry : shortenAff.properties().entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        for (Map.Entry<String, Object> entry : shortenAff.getFormat().properties().entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        System.out.println("framelength: " + shortenAff.getFrameLength());
        System.out.println("duration: " + (long) (((double) shortenAff.getFrameLength() / (double) shortenAff.getFormat().getFrameRate()) * 1_000_000L));
    }

}
