package org.kiyotoko.pong.menu;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.kiyotoko.pong.net.LocalGame;
import org.kiyotoko.pong.net.PlayerType;
import org.kiyotoko.pong.net.RemoteGame;

public class Menu extends Scene {
    public Menu(Group content) {
        super(content);

        Button single = new Button("Single", e -> ((Stage) getWindow()).setScene(new LocalGame(PlayerType.LOCAL, PlayerType.COMPUTER)));
        Button duo = new Button("Duo", e -> ((Stage) getWindow()).setScene(new LocalGame(PlayerType.LOCAL, PlayerType.LOCAL)));
        Button host = new Button("Host", e -> ((Stage) getWindow()).setScene(new LocalGame(PlayerType.LOCAL, PlayerType.NETWORK)));
        Button join = new Button("Join", e -> ((Stage) getWindow()).setScene(new RemoteGame()));

        VBox box = new VBox(10);
        box.getChildren().addAll(new BorderPane(single), new BorderPane(duo), new BorderPane(host), new BorderPane(join));
        content.getChildren().add(box);
        setFill(Color.BLACK);
    }
}
