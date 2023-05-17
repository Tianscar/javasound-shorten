package com.beatofthedrum.shortendecoder.spi;

import com.beatofthedrum.shortendecoder.ShortenContext;

import javax.sound.sampled.AudioInputStream;

public class ShortenAudioInputStream extends AudioInputStream {

    private final ShortenContext sc;

    ShortenAudioInputStream(ShortenContext sc, long length) {
        super(sc.shn_stream, new ShortenAudioFormat(sc), length);
        this.sc = sc;
    }

    public ShortenContext getShortenContext() {
        return sc;
    }

}
