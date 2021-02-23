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

/**
 * Listens to the messages that come through a Twitch chat.
 */
public class TwitchChatListener extends ListenerAdapter {
    /**
     * The IRC url for Twitch.
     */
    private final String TWITCH_IRC_URL = "irc.chat.twitch.tv";
    /**
     * The Twitch username.
     */
    private final String username;
    /**
     * The OAuth token for authenticating as the Twitch user. (https://twitchapps.com/tmi)
     */
    private final String oauth;
    /**
     * The twitch channel to monitor.
     */
    private final String channel;
    /**
     * The notification sound to play when a message is received.
     */
    private final String notificationSound;
    /**
     * True if all messages are going through TTS, false otherwise.
     */
    private final boolean useTTS;
    /**
     * The thread dedicated to hosting the IRC bot thread.
     */
    private Thread chatThread;
    /**
     * The IRC bot instance.
     */
    private PircBotX chatBot;
    /**
     * The Microsoft TTS API object.
     */
    private MicrosoftTTS tts;

    /**
     * Instantiates a new instance of the class.
     *
     * @param username The Twitch username.
     * @param oauth    The OAuth token for authenticating as the Twitch user.
     * @param channel  The Twitch channel to monitor.
     * @param useTTS   True if all messages are going through TTS, false otherwise.
     */
    public TwitchChatListener(String username, String oauth, String channel, boolean useTTS) {
        this.username = username;
        this.oauth = oauth;
        this.channel = channel;
        this.notificationSound = null;
        this.useTTS = useTTS;
    }

    /**
     * Called whenever a message is sent in Twitch chat.
     *
     * @param event The event wrapping the Twitch chat message.
     */
    @Override
    public void onGenericMessage(GenericMessageEvent event) {
        if (null != notificationSound) {
            var sound = new Media(new File(notificationSound).toURI().toString());
            var mediaPlayer = new MediaPlayer(sound);
            mediaPlayer.play();
        }

        if (null != tts) {
            tts.addMessage(String.format("%s says %s", event.getUser().getNick(), event.getMessage()));
        }
    }

    /**
     * Connects to Twitch chat.
     *
     * @return True if successful, false otherwise.
     */
    public boolean connect() {
        if (null != chatBot) {
            return false;
        }

        if (useTTS) {
            tts = new MicrosoftTTS();
        }

        //Configure what we want our bot to do
        var configuration = new Configuration.Builder()
                .setLogin(username)
                .setServerPassword(String.format("oauth:%s", oauth))
                .setName(username)
                .addServer(TWITCH_IRC_URL, 6697)
                .setSocketFactory(SSLSocketFactory.getDefault())
                .addAutoJoinChannel(String.format("#%s", channel))
                .addListener(this)
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

    /**
     * Disconnects from Twitch chat.
     *
     * @return True if successful, false otherwise.
     */
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

            chatBot = null;
            chatThread = null;
        }

        if (null != tts) {
            tts.dispose();
            tts = null;
        }

        return true;
    }
}
