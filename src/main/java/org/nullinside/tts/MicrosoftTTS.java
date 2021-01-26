package org.nullinside.tts;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MicrosoftTTS {
    private final BlockingQueue<String> voiceQueue = new LinkedBlockingQueue<>();
    private boolean shouldExit = false;
    private Process currentSpeakingProcess = null;
    private final Thread voiceRunningThread;

    public MicrosoftTTS() {
        voiceRunningThread = new Thread(this::voiceQueueingThread);
        voiceRunningThread.start();
    }

    public void dispose() throws InterruptedException {
        // First tell the other thread to exit and add a poison pill to the queue to make sure
        // that it will unblock.
        this.shouldExit = true;
        addMessage("█");

        // Then, kill the currently running message
        this.stopCurrentSpeak();

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
                    this.stopCurrentSpeak();
                    return;
                }

                this.speak(currentMessage);
            } catch (InterruptedException e) {
                this.stopCurrentSpeak();
                return;
            }
        }
    }

    private void speak(String message) throws InterruptedException {
        ProcessBuilder builder = new ProcessBuilder();
        builder.command("PowerShell", "-Command", String.format("\"Add-Type -AssemblyName System.Speech; $synth = New-Object System.Speech.Synthesis.SpeechSynthesizer; $synth.Volume = 50; $synth.Speak('%s?');\"", message));
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
