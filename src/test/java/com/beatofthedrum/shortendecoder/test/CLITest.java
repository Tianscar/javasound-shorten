package com.beatofthedrum.shortendecoder.test;

import com.beatofthedrum.shortendecoder.cli.DecoderDemo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CLITest {

    @Test
    @DisplayName("shn -> wav")
    public void decode() {
        DecoderDemo.main(new String[] {"src/test/resources/fbodemo1.shn", "fbodemo1.wav"});
    }

}
