package com.beatofthedrum.shortendecoder;

import java.io.*;

public class ShortenInputStream extends InputStream implements DataInput {

    private final DataInputStream in;
    private final BufferedReader lineReader;
    private int offset;

    public ShortenInputStream(InputStream in) throws IllegalArgumentException {
        this.in = in instanceof DataInputStream ? (DataInputStream) in : new DataInputStream(in);
        lineReader = new BufferedReader(new InputStreamReader(in));
        offset = 0;
    }

    @Override
    public int read() throws IOException {
        int i = in.read();
        if (i >= 0) offset ++;

        return i;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int i = in.read(b, off, len);

        if (i > 0) offset += i;

        return i;
    }

    @Override
    public long skip(long n) throws IOException {
        long i = in.skip(n);

        if (i > 0) offset += i;

        return i;
    }

    public void close() throws IOException {
        in.close();
    }

    @Override
    public int available() throws IOException {
        return in.available();
    }

    @Override
    public void readFully(byte[] b) throws IOException {
        in.readFully(b);
    }

    @Override
    public void readFully(byte[] b, int off, int len) throws IOException {
        in.readFully(b, off, len);
    }

    @Override
    public int skipBytes(int n) throws IOException {
        return in.skipBytes(n);
    }

    @Override
    public boolean readBoolean() throws IOException {
        return in.readBoolean();
    }

    @Override
    public byte readByte() throws IOException {
        return in.readByte();
    }

    @Override
    public int readUnsignedByte() throws IOException {
        return in.readUnsignedByte();
    }

    @Override
    public short readShort() throws IOException {
        return in.readShort();
    }

    @Override
    public int readUnsignedShort() throws IOException {
        return in.readUnsignedShort();
    }

    @Override
    public char readChar() throws IOException {
        return in.readChar();
    }

    @Override
    public int readInt() throws IOException {
        return in.readInt();
    }

    @Override
    public long readLong() throws IOException {
        return in.readLong();
    }

    @Override
    public float readFloat() throws IOException {
        return in.readFloat();
    }

    @Override
    public double readDouble() throws IOException {
        return in.readDouble();
    }

    @Override
    public String readLine() throws IOException {
        return lineReader.readLine();
    }

    @Override
    public String readUTF() throws IOException {
        return in.readUTF();
    }

    public int offset() {
        return offset;
    }

}
