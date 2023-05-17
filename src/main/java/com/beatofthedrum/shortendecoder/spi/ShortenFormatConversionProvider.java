package com.beatofthedrum.shortendecoder.spi;

import com.beatofthedrum.shortendecoder.Defines;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.spi.FormatConversionProvider;
import java.io.IOException;

import static com.beatofthedrum.shortendecoder.spi.ShortenAudioFormat.Encoding.SHORTEN;
import static com.beatofthedrum.shortendecoder.spi.ShortenAudioFormat.Encoding.fromFType;
import static javax.sound.sampled.AudioFormat.Encoding.PCM_SIGNED;
import static javax.sound.sampled.AudioSystem.NOT_SPECIFIED;

public class ShortenFormatConversionProvider extends FormatConversionProvider {

    private static final AudioFormat.Encoding[] SOURCE_ENCODINGS = new AudioFormat.Encoding[] { SHORTEN };
    private static final AudioFormat.Encoding[] TARGET_ENCODINGS = new AudioFormat.Encoding[] { PCM_SIGNED };

    @Override
    public AudioFormat.Encoding[] getSourceEncodings() {
        return SOURCE_ENCODINGS.clone();
    }

    @Override
    public AudioFormat.Encoding[] getTargetEncodings() {
        return TARGET_ENCODINGS.clone();
    }

    @Override
    public AudioFormat.Encoding[] getTargetEncodings(AudioFormat sourceFormat) {
        if (sourceFormat.getEncoding().equals(SHORTEN)) return TARGET_ENCODINGS.clone();
        else return new AudioFormat.Encoding[0];
    }

    @Override
    public AudioFormat[] getTargetFormats(AudioFormat.Encoding targetEncoding, AudioFormat sourceFormat) {
        Integer ftype = (Integer) sourceFormat.properties().get("shn.ftype");
        if (ftype != null && sourceFormat.getEncoding().equals(SHORTEN)) {
            AudioFormat.Encoding encoding = fromFType(ftype);
            if (encoding == PCM_SIGNED || sourceFormat.getSampleSizeInBits() == 8)
                return new AudioFormat[] { getTargetFormat(sourceFormat, encoding) };
        }
        return new AudioFormat[0];
    }

    private static AudioFormat getTargetFormat(AudioFormat sourceFormat, AudioFormat.Encoding encoding) {
        return new AudioFormat(
                encoding,
                NOT_SPECIFIED,
                sourceFormat.getSampleSizeInBits(),
                sourceFormat.getChannels(),
                NOT_SPECIFIED,
                NOT_SPECIFIED,
                sourceFormat.isBigEndian()
        );
    }

    @Override
    public AudioInputStream getAudioInputStream(AudioFormat.Encoding targetEncoding, AudioInputStream sourceStream) {
        if (sourceStream instanceof ShortenAudioInputStream) {
            Integer ftype = (Integer) sourceStream.getFormat().properties().get("shn.ftype");
            if (ftype != null) {
                AudioFormat.Encoding encoding = fromFType(ftype);
                if (encoding == PCM_SIGNED || sourceStream.getFormat().getSampleSizeInBits() == 8)
                    return getAudioInputStream(getTargetFormat(sourceStream.getFormat(), fromFType(ftype)), sourceStream);
            }
        }
        throw new IllegalArgumentException("conversion not supported");
    }

    @Override
    public AudioInputStream getAudioInputStream(AudioFormat targetFormat, AudioInputStream sourceStream) {
        if (sourceStream instanceof ShortenAudioInputStream) {
            Integer ftype = (Integer) sourceStream.getFormat().properties().get("shn.ftype");
            if (ftype != null) {
                AudioFormat sourceFormat = sourceStream.getFormat();
                if (sourceFormat.isBigEndian() == targetFormat.isBigEndian() &&
                        sourceFormat.getChannels() == targetFormat.getChannels() &&
                        sourceFormat.getSampleSizeInBits() == targetFormat.getSampleSizeInBits() &&
                        fromFType(ftype).equals(targetFormat.getEncoding())) {
                    try {
                        return new DecodedShortenAudioInputStream((ShortenAudioInputStream) sourceStream);
                    }
                    catch (IOException ignored) {}
                }
                throw new IllegalArgumentException("unable to convert "
                        + sourceFormat + " to "
                        + targetFormat);
            }
        }
        throw new IllegalArgumentException("conversion not supported");
    }

}
