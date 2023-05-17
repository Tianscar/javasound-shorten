package com.beatofthedrum.shortendecoder.spi;

import com.beatofthedrum.shortendecoder.ShortenContext;
import com.beatofthedrum.shortendecoder.ShortenUtils;

import javax.sound.sampled.AudioFileFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.beatofthedrum.shortendecoder.spi.ShortenAudioFileFormat.Type.SHORTEN;

public class ShortenAudioFileFormat extends AudioFileFormat {

    public static class Type extends AudioFileFormat.Type {
        public static final AudioFileFormat.Type SHORTEN = new Type("Shorten", "shn");
        private Type(String name, String extension) {
            super(name, extension);
        }
    }

    private final HashMap<String, Object> props;

    public ShortenAudioFileFormat(ShortenContext sc, long byteLength) {
        super(SHORTEN, (int) byteLength, new ShortenAudioFormat(sc), ShortenUtils.ShortenGetNumSamples(sc));

        props = new HashMap<>();
        props.put("shn.version", sc.version);
        props.put("shn.datasize", sc.datasize);
    }

    /**
     * Java 5.0 compatible method to get the full map of properties. The
     * properties use the KEY_ keys defined in this class.
     */
    public Map<String, Object> properties() {
        Map<String, Object> ret;
        if (props == null) {
            ret = new HashMap<>(0);
        } else {
            ret = (Map<String, Object>) props.clone();
        }
        return Collections.unmodifiableMap(ret);
    }

    /**
     * Java 5.0 compatible method to get a property. As key use the KEY_ constants defined in this class.
     */
    public Object getProperty(String key) {
        if (props == null) {
            return null;
        }
        return props.get(key);
    }

}
