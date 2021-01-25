package org.nullinside.twitch;

import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.types.GenericMessageEvent;

import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;

public class TwitchChatListener extends ListenerAdapter {
    private final String TWITCH_IRC_URL = "irc.chat.twitch.tv";
    private final String username;
    private final String oauth;
    private final String channel;
    private Thread chatThread;
    private PircBotX chatBot;

    @Override
    public void onGenericMessage(GenericMessageEvent event) {
        System.out.printf("%s: %s", event.getUser().getNick(), event.getMessage());
    }

    public TwitchChatListener() {
        this.username = null;
        this.oauth = null;
        this.channel = null;
    }

    public TwitchChatListener(String username, String oauth, String channel) {
        this.username = username;
        this.oauth = oauth;
        this.channel = channel;
    }

    public boolean connect() {
        //Configure what we want our bot to do
        Configuration configuration = new Configuration.Builder()
                .setLogin(this.username)
                .setServerPassword(String.format("oauth:%s", this.oauth))
                .setName(this.username)
                .addServer(this.TWITCH_IRC_URL, 6697)
                .setSocketFactory(SSLSocketFactory.getDefault())
                .addAutoJoinChannel(String.format("#%s", this.channel))
                .addListener(new TwitchChatListener())
                .buildConfiguration();

        //Create our bot with the configuration
        chatBot = new PircBotX(configuration);
        this.chatThread = new Thread(() -> {
            //Connect to the server
            try {
                chatBot.startBot();
            } catch (IOException | IrcException e) {
                e.printStackTrace();
            }
        });
        this.chatThread.setDaemon(true);
        this.chatThread.start();
        return true;
    }

    public boolean disconnect() {
        if (null != this.chatThread) {
            this.chatBot.stopBotReconnect();
            this.chatBot.close();

            try {
                this.chatThread.join(30000);
            } catch (InterruptedException e) {
                this.chatThread.interrupt();
                e.printStackTrace();
            }

            return true;
        }

        return false;
    }
}
