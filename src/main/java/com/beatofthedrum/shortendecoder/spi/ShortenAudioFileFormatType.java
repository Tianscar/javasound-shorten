package com.beatofthedrum.shortendecoder.spi;

import javax.sound.sampled.AudioFileFormat;

public class ShortenAudioFileFormatType extends AudioFileFormat.Type {

    public static final AudioFileFormat.Type SHN = new ShortenAudioFileFormatType("Shorten", "shn");

    private ShortenAudioFileFormatType(String name, String extension) {
        super(name, extension);
    }

}
