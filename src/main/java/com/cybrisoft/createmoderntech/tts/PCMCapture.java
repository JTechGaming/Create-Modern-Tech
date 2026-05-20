package com.cybrisoft.createmoderntech.tts;

import com.sun.speech.freetts.audio.AudioPlayer;

import javax.sound.sampled.AudioFormat;
import java.io.ByteArrayOutputStream;

public class PCMCapture implements AudioPlayer {
    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    private AudioFormat format;

    @Override
    public void setAudioFormat(AudioFormat fmt) { this.format = fmt; }

    @Override
    public AudioFormat getAudioFormat() { return format; }

    @Override
    public boolean write(byte[] data) {
        buffer.write(data, 0, data.length);
        return true;
    }

    @Override
    public boolean write(byte[] data, int offset, int size) {
        buffer.write(data, offset, size);
        return true;
    }

    public byte[] getBytes() { return buffer.toByteArray(); }

    // remaining AudioPlayer methods are no-ops or return defaults
    @Override public void begin(int size) {}
    @Override
    public boolean end() { return false; }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void cancel() {}
    @Override public void reset() { buffer.reset(); }
    @Override public void close() {}
    @Override public float getVolume() { return 1.0f; }
    @Override public void setVolume(float v) {}
    @Override public long getTime() { return -1; }
    @Override public void resetTime() {}
    @Override public boolean drain() { return true; }
    @Override public void showMetrics() {}
    @Override public void startFirstSampleTimer() {}
}
