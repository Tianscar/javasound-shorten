package com.beatofthedrum.shortendecoder;

import java.io.IOException;

public class ShortenException extends IOException {
    private static final long serialVersionUID = -8852375846383609864L;
    public ShortenException(String message) {
        super(message);
    }
    public ShortenException(String message, Throwable cause) {
        super(message, cause);
    }
    public ShortenException(Throwable cause) {
        super(cause);
    }
    public ShortenException() {
    }
}
