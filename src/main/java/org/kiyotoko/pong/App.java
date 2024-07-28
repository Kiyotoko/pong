package org.kiyotoko.pong;

import javafx.application.Application;
import javafx.stage.Stage;
import org.kiyotoko.pong.net.PongClient;
import org.kiyotoko.pong.net.PongServer;

public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        PongServer server = new PongServer();
        PongClient client1 = new PongClient();
        PongClient client2 = new PongClient();

        Stage stage1 = new Stage();
        stage1.setScene(client1.getGame());
        stage1.setTitle("Player 1");
        stage1.show();

        Stage stage2 = new Stage();
        stage2.setScene(client2.getGame());
        stage2.setTitle("Player 2");
        stage2.show();

        stage.setScene(server.getGame());
        stage.setTitle("Server Monitor");
        stage.show();

        client1.join();
        client2.join();
        server.getGame().startTimeline();
    }
}
