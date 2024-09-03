package org.kiyotoko.pong.menu;

import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import org.kiyotoko.pong.game.Game;
import org.kiyotoko.pong.net.LocalGame;

public class Menu extends Scene {
    public Menu(Group content) {
        super(content, 720, 480);

        var single = UITools.createButton("Single Player", () -> ((Stage) getWindow()).setScene(Game.newSingleplayer()));
        var duo = UITools.createButton("Local Multiplayer", () -> ((Stage) getWindow()).setScene(Game.newLocalMultiplayer()));
        var host = UITools.createButton("Host Online", () -> ((Stage) getWindow()).setScene(new LocalGame()));
        var join = UITools.createButton("Join Online", () -> ((Stage) getWindow()).setScene(new Join(new Group())));
        var exit = UITools.createButton("Exit Game", () -> ((Stage) getWindow()).close());

        VBox box = new VBox(10);
        box.getChildren().addAll(new BorderPane(single), new BorderPane(duo), new BorderPane(host), new BorderPane(join), new BorderPane(exit));
        box.setPrefSize(getWidth(), getHeight());
        box.setAlignment(Pos.CENTER);

        content.getChildren().add(box);
        setFill(Color.BLACK);
    }
}
