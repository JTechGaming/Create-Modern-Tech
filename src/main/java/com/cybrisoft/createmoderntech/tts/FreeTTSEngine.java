package com.cybrisoft.createmoderntech.tts;

import io.github.jonelo.jAdapterForNativeTTS.engines.SpeechEngine;
import io.github.jonelo.jAdapterForNativeTTS.engines.SpeechEngineNative;
import io.github.jonelo.jAdapterForNativeTTS.engines.Voice;
import io.github.jonelo.jAdapterForNativeTTS.engines.VoicePreferences;
import io.github.jonelo.jAdapterForNativeTTS.engines.exceptions.SpeechEngineCreationException;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.lwjgl.openal.AL10;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@OnlyIn(Dist.CLIENT)
public class FreeTTSEngine {
    private static Voice voice;
    private static boolean initialized = false;

    private static SpeechEngine speechEngine;

    private static final ExecutorService TTS_EXECUTOR = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "FreeTTS-Thread");
        t.setDaemon(true);
        return t;
    });

    public static void initialize() {
        try {
            speechEngine = SpeechEngineNative.getInstance();

            List<Voice> voices = speechEngine.getAvailableVoices();

            System.out.println("For now the following voices are supported:\n");
            for (Voice voice : voices) {
                System.out.printf("%s%n", voice);
            }

            VoicePreferences voicePreferences = new VoicePreferences();
            voicePreferences.setLanguage("en"); //  ISO-639-1
            voicePreferences.setCountry("GB"); // ISO 3166-1 Alpha-2 code
            voicePreferences.setGender(VoicePreferences.Gender.FEMALE);
            voice = speechEngine.findVoiceByPreferences(voicePreferences);

            if (voice == null) {
                System.out.printf("Warning: Voice has not been found by the voice preferences %s%n", voicePreferences);
                voice = voices.get(0); // it is guaranteed that the speechEngine supports at least one voice
                System.out.printf("Using \"%s\" instead.%n", voice);
            }
        } catch (SpeechEngineCreationException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void speak(String text, Vec3 position, float range) {
        if (!Minecraft.getInstance().isSameThread()) return;
        TTS_EXECUTOR.submit(() -> {
            try {
                speechEngine.setVoice(voice.getName());
                speechEngine.say(text);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(TTS_EXECUTOR::shutdown));
    }
}
