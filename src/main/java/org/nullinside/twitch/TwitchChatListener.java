package org.nullinside.twitch;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.nullinside.tts.MicrosoftTTS;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.types.GenericMessageEvent;

import javax.net.ssl.SSLSocketFactory;
import java.io.File;
import java.io.IOException;

public class TwitchChatListener extends ListenerAdapter {
    private final String TWITCH_IRC_URL = "irc.chat.twitch.tv";
    private final String username;
    private final String oauth;
    private final String channel;
    private final String notificationSound;
    private Thread chatThread;
    private PircBotX chatBot;
    private MicrosoftTTS tts;

    public TwitchChatListener(String username, String oauth, String channel, boolean useTTS) {
        this.username = username;
        this.oauth = oauth;
        this.channel = channel;

        var config = org.nullinside.notification_app.Configuration.getInstance();
        notificationSound = config.twitchChatAlertGlobalConfig.alertSoundFilename;

        if (useTTS) {
            tts = new MicrosoftTTS();
        }
    }

    @Override
    public void onGenericMessage(GenericMessageEvent event) {
        if (null != tts) {
            tts.addMessage(String.format("%s says %s", event.getUser().getNick(), event.getMessage()));
        } else {
            var sound = new Media(new File(notificationSound).toURI().toString());
            var mediaPlayer = new MediaPlayer(sound);
            mediaPlayer.play();
        }
    }

    public boolean connect() {
        //Configure what we want our bot to do
        var configuration = new Configuration.Builder()
                .setLogin(username)
                .setServerPassword(String.format("oauth:%s", oauth))
                .setName(username)
                .addServer(TWITCH_IRC_URL, 6697)
                .setSocketFactory(SSLSocketFactory.getDefault())
                .addAutoJoinChannel(String.format("#%s", channel))
                .addListener(new TwitchChatListener(username, oauth, channel, null != tts))
                .buildConfiguration();

        //Create our bot with the configuration
        chatBot = new PircBotX(configuration);
        chatThread = new Thread(() -> {
            //Connect to the server
            try {
                chatBot.startBot();
            } catch (IOException | IrcException e) {
                e.printStackTrace();
            }
        });
        chatThread.setDaemon(true);
        chatThread.start();
        return true;
    }

    public boolean disconnect() {
        if (null != chatThread) {
            chatBot.stopBotReconnect();
            chatBot.close();

            try {
                chatThread.join(30000);
            } catch (InterruptedException e) {
                chatThread.interrupt();
                e.printStackTrace();
            }

            return true;
        }

        return false;
    }
}
