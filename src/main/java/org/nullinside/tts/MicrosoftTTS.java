package org.nullinside.tts;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MicrosoftTTS {
    private final BlockingQueue<String> voiceQueue = new LinkedBlockingQueue<>();
    private final Thread voiceRunningThread;
    private boolean shouldExit = false;
    private Process currentSpeakingProcess = null;

    public MicrosoftTTS() {
        voiceRunningThread = new Thread(this::voiceQueueingThread, "Microsoft TTS Thread");
        voiceRunningThread.setDaemon(true);
        voiceRunningThread.start();
    }

    public void dispose() throws InterruptedException {
        // First tell the other thread to exit and add a poison pill to the queue to make sure
        // that it will unblock.
        shouldExit = true;
        addMessage("█");

        // Then, kill the currently running message
        stopCurrentSpeak();

        // Let the process exit gracefully.
        voiceRunningThread.join(10000);
    }

    public void addMessage(String message) {
        voiceQueue.add(message);
    }

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

    private void speak(String message) throws InterruptedException {
        var builder = new ProcessBuilder();
        var command = String.format("\"Add-Type -AssemblyName System.Speech; $synth = New-Object System.Speech.Synthesis.SpeechSynthesizer; $synth.Volume = 100; $synth.Speak('%s?');\"", message.replace("'", "''"));
        builder.command("PowerShell", "-Command", command);
        try {
            currentSpeakingProcess = builder.start();
            currentSpeakingProcess.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopCurrentSpeak() {
        if (null == currentSpeakingProcess) {
            return;
        }

        currentSpeakingProcess.destroyForcibly();
    }
}
