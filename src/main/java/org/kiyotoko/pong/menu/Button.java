package org.kiyotoko.pong.menu;

import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.kiyotoko.pong.App;

public class Button extends Label {
    public Button(String text, EventHandler<? super MouseEvent> onAction) {
        super(text.toUpperCase());

        setTextFill(Color.WHITE);
        setFont(Font.loadFont(App.class.getResourceAsStream("ARCADECLASSIC.TTF"), 68));
        setOnMousePressed(onAction);
    }
}
