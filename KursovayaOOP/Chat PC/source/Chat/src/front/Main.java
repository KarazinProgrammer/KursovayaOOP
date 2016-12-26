package front;

import back.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.File;
import java.util.Timer;

public class Main extends Application {

    public static Controller controller;
    private ServerUDP server;
    public static ClientUDP client;
    public static Timer timer = new Timer();
    public static User iAm = new User(User.receiveMacAddress(), "Default", 1);

    @Override
    public void start(Stage primaryStage) throws Exception{
        client=new ClientUDP();
        if(client==null) System.out.println("Null");
        FXMLLoader loader = new FXMLLoader();
        Parent root = loader.load(getClass().getResource("markup.fxml"));
        primaryStage.setTitle("Chat");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        primaryStage.setMinHeight(400.00);
        primaryStage.setMinWidth(500.00);
        primaryStage.getIcons().addAll(new Image("images"+ File.separator+"icon.png"));
        server = new ServerUDP(client, new Translator(iAm, controller));
        server.start();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        FileWorker.deleteExcessMessages();
        server.interrupt();
        timer.cancel();
        timer.purge();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                client.close();
            }
        });
    }

    public ServerUDP getServer() {
        return server;
    }

    public ClientUDP getClient() {
        return client;
    }

    public Timer getTimer() {
        return timer;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
