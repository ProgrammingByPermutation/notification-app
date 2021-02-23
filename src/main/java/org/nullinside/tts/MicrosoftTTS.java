package org.nullinside.tts;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Handles text to speech through the Microsoft API.
 */
public class MicrosoftTTS {
    /**
     * The queue of text to convert to speech.
     */
    private final BlockingQueue<String> voiceQueue = new LinkedBlockingQueue<>();
    /**
     * The thread that runs the commands for text to speech.
     */
    private final Thread voiceRunningThread;
    /**
     * The poison pill for killing the {@link #voiceRunningThread}.
     */
    private boolean shouldExit = false;
    /**
     * The process started to run the text to speech.
     */
    private Process currentSpeakingProcess = null;

    /**
     * Instantiates a new instance of the class.
     */
    public MicrosoftTTS() {
        voiceRunningThread = new Thread(this::voiceQueueingThread, "Microsoft TTS Thread");
        voiceRunningThread.setDaemon(true);
        voiceRunningThread.start();
    }

    /**
     * Disposes of managed and unmanaged resources.
     */
    public void dispose() {
        // First tell the other thread to exit and add a poison pill to the queue to make sure
        // that it will unblock.
        shouldExit = true;
        addMessage("█");

        // Then, kill the currently running message
        stopCurrentSpeak();

        // Let the process exit gracefully.
        try {
            voiceRunningThread.join(10000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Add a message to the queue for text to speech to be spoken.
     *
     * @param message The message to speak.
     */
    public void addMessage(String message) {
        voiceQueue.add(message);
    }

    /**
     * The main thread of {@link #voiceRunningThread}. It just loops through the queue
     * and speaks whenever something comes through.
     */
    private void voiceQueueingThread() {
        while (true) {
            try {
                String currentMessage = voiceQueue.take();

                if (shouldExit) {
                    stopCurrentSpeak();
                    return;
                }

                speak(currentMessage);
            } catch (InterruptedException e) {
                stopCurrentSpeak();
                return;
            }
        }
    }

    /**
     * Invokes the Microsoft API for TTS.
     *
     * @param message The message to speak.
     */
    private void speak(String message) {
        var builder = new ProcessBuilder();
        var command = String.format("\"Add-Type -AssemblyName System.Speech; $synth = New-Object System.Speech.Synthesis.SpeechSynthesizer; $synth.Volume = 100; $synth.Speak('%s?');\"", message.replace("'", "''"));
        builder.command("PowerShell", "-Command", command);

        try {
            currentSpeakingProcess = builder.start();
            currentSpeakingProcess.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Stop current TTS that is playing.
     */
    private void stopCurrentSpeak() {
        if (null == currentSpeakingProcess) {
            return;
        }

        // Kill the other process.
        currentSpeakingProcess.destroyForcibly();
    }
}
