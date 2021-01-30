mvn clean javafx:jlink package
::mvn clean compile jar:jar
::jpackage -d image --type app-image --name NotificationApp --input target --main-jar notification-app-1.0.0.jar --main-class org.nullinside.notification_app/Main
