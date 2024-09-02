package org.kiyotoko.pong.menu;

import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.kiyotoko.pong.Launcher;
import org.kiyotoko.pong.net.RemoteGame;

public class Join extends Scene {

    private final Label message = UITools.createText("Connect Online");

    public Join(Group content) {
        super(content, 720, 480);
        Font font = Font.loadFont(Launcher.class.getResourceAsStream("PixelFont.otf"), 50);

        TextField address = new TextField();
        address.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
        address.setPromptText("Enter Address");
        address.setContextMenu(new ContextMenu());
        address.setAlignment(Pos.CENTER);
        address.setFont(font);
        address.setStyle("-fx-text-fill: white;");
        address.applyCss();
        
        var connect = UITools.createButton("Connect", () -> {
            final String text = address.getText();
            if (text.isBlank()) error("Address can not be blank");
            if (!(text.matches("([0-2][0-9][0-9]|3[0-9][0-9]|[0-9]?[0-9]).([0-2][0-9][0-9]|3[0-9][0-9]|[0-9]?[0-9]).([0-2][0-9][0-9]|3[0-9][0-9]|[0-9]?[0-9]).([0-2][0-9][0-9]|3[0-9][0-9]|[0-9]?[0-9])")
            || text.equals("localhost"))) error("Address is not valid");
            else try {
                ((Stage) getWindow()).setScene(new RemoteGame(text));
            } catch (Throwable throwable) {
                Throwable cause = throwable;
                while (cause.getCause() != null) {
                    cause = cause.getCause();
                }
                error(cause.getMessage());
            }
        });
        var cancel = UITools.createButton("Cancel", () -> ((Stage) getWindow()).setScene(new Menu(new Group())));

        VBox box = new VBox(10);
        box.getChildren().addAll(new BorderPane(message), new BorderPane(address), new BorderPane(connect),  new BorderPane(cancel));
        box.setPrefSize(getWidth(), getHeight());
        box.setAlignment(Pos.CENTER);

        content.getChildren().add(box);
        connect.requestFocus();
        setFill(Color.BLACK);
    }

    public void error(String msg) {
        message.setText(msg);
        message.setTextFill(Color.CRIMSON);
    }
}
