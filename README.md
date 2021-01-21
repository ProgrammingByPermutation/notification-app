# Notification Application

This application exists to allow me to create random notifications.

### Use Cases

1. Twitch
    * Follow
    * Join Channel
    * Talk in Chat (beep)
    * Talk in Chat (TTS)
    * Talk in Chat (pop-up)
1. Jenkins
    * Build Failures
1. https://www.nullinside.com <sup>1</sup>
    * Website Up

<sup>1</sup> This could technically be a Jenkins builds of post-deployment tests.

### JavaFX vs Swing

We chose JavaFX.

There are more jobs using Swing but JavaFX has been out for a while now with many stable versions. There are even
project templates in IntelliJ for it.

Also, Swing looks like Swing and I don't feel like spending time stylizing.