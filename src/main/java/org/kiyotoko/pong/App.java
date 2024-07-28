package org.kiyotoko.pong;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.stage.Stage;
import org.kiyotoko.pong.menu.Menu;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        stage.setScene(new Menu(new Group()));
        stage.setTitle("Pong");
        stage.show();
    }
}
