package org.kiyotoko.pong.menu;

import javax.annotation.Nonnull;

import org.kiyotoko.pong.App;
import org.kiyotoko.pong.Launcher;

import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public final class UITools {
    private UITools() {
        throw new UnsupportedOperationException();
    }

    public static final Color TEXT_COLOR = Color.WHITE;
    public static final Color FOCUS_COLOR = Color.GREENYELLOW;
    public static final Color ERROR_COLOR = Color.CRIMSON;
    public static final Color INFO_COLOR = Color.AQUA;

    public static final Font TEXT_FONT = Font.loadFont(Launcher.class.getResourceAsStream("PixelFont.otf"), 60);

    @Nonnull
    public static final Label createText(@Nonnull String msg) {
        Label text = new Label(msg.toUpperCase());
        text.setFocusTraversable(false);
        text.setTextFill(TEXT_COLOR);
        text.setFont(TEXT_FONT);
        return text;
    }

    @Nonnull
    public static final Label createButton(@Nonnull String msg, @Nonnull Runnable onAction) {
        Label button = new Label(msg.toUpperCase());
        button.setTextFill(TEXT_COLOR);
        button.setFont(Font.loadFont(App.class.getResourceAsStream("PixelFont.otf"), 60));
        button.setFocusTraversable(true);
        button.focusedProperty().addListener((overversable, oldValue, newValue) -> {
            button.setTextFill(newValue ? FOCUS_COLOR : TEXT_COLOR);
        });
        button.setOnMousePressed(event -> onAction.run());
        button.setOnMouseEntered(event -> button.requestFocus());
        button.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                onAction.run();
            }
        });
        return button;
    }
}
