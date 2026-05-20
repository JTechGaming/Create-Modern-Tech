package com.cybrisoft.createmoderntech.tts;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.openal.AL10;
import org.lwjgl.system.MemoryUtil;

import javax.sound.sampled.AudioFormat;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;

public class FreeTTSEngine {
    private static Voice voice;
    private static boolean initialized = false;

    public static void initialize() {
        CompletableFuture.runAsync(() -> {
            System.setProperty("freetts.voices",
                    "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");
            VoiceManager vm = VoiceManager.getInstance();
            voice = vm.getVoice("kevin16");
            if (voice != null) {
                voice.allocate();
                initialized = true;
            }
        });
    }

    public static void speak(String text, Vec3 position, float range) {
        if (!initialized) return;

        CompletableFuture.runAsync(() -> {
            PCMCapture capture = new PCMCapture();
            voice.setAudioPlayer(capture);
            voice.speak(text);

            byte[] pcmData = capture.getBytes();
            AudioFormat fmt = capture.getAudioFormat();
            if (pcmData.length == 0 || fmt == null) return;

            int sampleRate = (int) fmt.getSampleRate();

            // hand off to main thread for OpenAL
            Minecraft.getInstance().execute(() ->
                    playViaOpenAL(pcmData, sampleRate, position, range));
        });
    }

    private static void playViaOpenAL(byte[] pcmData, int sampleRate, Vec3 pos, float range) {
        int buffer = AL10.alGenBuffers();
        byte[] swapped = new byte[pcmData.length];
        for (int i = 0; i < pcmData.length - 1; i += 2) {
            swapped[i]     = pcmData[i + 1];
            swapped[i + 1] = pcmData[i];
        }
        ByteBuffer buf = MemoryUtil.memAlloc(swapped.length);
        buf.put(swapped).flip();
        AL10.alBufferData(buffer, AL10.AL_FORMAT_MONO16, buf, sampleRate);
        MemoryUtil.memFree(buf);

        int source = AL10.alGenSources();
        AL10.alSource3f(source, AL10.AL_POSITION, (float)pos.x, (float)pos.y, (float)pos.z);
        AL10.alSourcef(source, AL10.AL_REFERENCE_DISTANCE, 8.0f);
        AL10.alSourcef(source, AL10.AL_GAIN, 2.0f);
        AL10.alSourcef(source, AL10.AL_MAX_DISTANCE, range);
        AL10.alSourcef(source, AL10.AL_ROLLOFF_FACTOR, 1.0f);
        AL10.alSourcei(source, AL10.AL_BUFFER, buffer);

        AL10.alSourcePlay(source);

        // clean up after playback finishes
        CompletableFuture.runAsync(() -> {
            while (AL10.alGetSourcei(source, AL10.AL_SOURCE_STATE) == AL10.AL_PLAYING) {
                try { Thread.sleep(100); } catch (InterruptedException ignored) {}
            }
            Minecraft.getInstance().execute(() -> {
                AL10.alDeleteSources(source);
                AL10.alDeleteBuffers(buffer);
            });
        });
    }
}
