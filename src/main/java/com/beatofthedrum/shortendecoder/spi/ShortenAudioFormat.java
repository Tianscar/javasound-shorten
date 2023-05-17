package com.beatofthedrum.shortendecoder.spi;

import com.beatofthedrum.shortendecoder.Defines;
import com.beatofthedrum.shortendecoder.ShortenContext;
import com.beatofthedrum.shortendecoder.ShortenUtils;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import java.util.HashMap;
import java.util.Map;

import static com.beatofthedrum.shortendecoder.spi.ShortenAudioFormat.Encoding.SHORTEN;
import static javax.sound.sampled.AudioSystem.NOT_SPECIFIED;

public class ShortenAudioFormat extends AudioFormat {

    public static class Encoding extends AudioFormat.Encoding {
        public static final AudioFormat.Encoding SHORTEN = new Encoding("Shorten");
        private Encoding(String name) {
            super(name);
        }
        public static AudioFormat.Encoding fromFType(int ftype) {
            if (ftype == Defines.TYPE_AU3) {
                return ALAW;
            }
            else if (ftype == Defines.TYPE_AU1 || ftype == Defines.TYPE_AU2) {
                return ULAW;
            }
            else return PCM_SIGNED;
        }
    }

    public ShortenAudioFormat(ShortenContext sc) {
        super(
                SHORTEN,
                ShortenUtils.ShortenGetSampleRate(sc),
                ShortenUtils.ShortenGetBitsPerSample(sc),
                ShortenUtils.ShortenGetNumChannels(sc),
                frameSize(ShortenUtils.ShortenGetNumChannels(sc), ShortenUtils.ShortenGetBitsPerSample(sc)),
                ShortenUtils.ShortenGetSampleRate(sc),
                false,
                generateProperties(sc)
        );
    }

    private static Map<String, Object> generateProperties(ShortenContext sc) {
        Map<String, Object> properties = new HashMap<>();
        properties.put("vbr", true);
        properties.put("bitrate", ShortenUtils.ShortenGetBitsPerSample(sc) * ShortenUtils.ShortenGetSampleRate(sc));
        
        properties.put("shn.ftype", sc.ftype);
        properties.put("shn.originalftype", sc.original_ftype);
        return properties;
    }

    private static int frameSize(int channels, int sampleSizeInBits) {
        return (channels == NOT_SPECIFIED || sampleSizeInBits == NOT_SPECIFIED)?
                NOT_SPECIFIED:
                ((sampleSizeInBits + 7) / 8) * channels;
    }

    @Override
    public String toString() {
        String sEndian = "";
        if (getEncoding().equals(SHORTEN) && ((getSampleSizeInBits() > 8)
                || (getSampleSizeInBits() == AudioSystem.NOT_SPECIFIED))) {
            if (isBigEndian()) {
                sEndian = "big-endian";
            } else {
                sEndian = "little-endian";
            }
        }
        return super.toString() + sEndian;
    }

}
