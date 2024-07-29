package org.kiyotoko.pong;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.glavo.png.javafx.PNGJavaFXUtils;
import org.kiyotoko.pong.menu.Menu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class App extends Application {
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    @Override
    public void start(Stage stage) {
        stage.setScene(new Menu(new Group()));
        stage.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.F12) {
                var image = stage.getScene().snapshot(null);
                var folder = Path.of("screenshots");
                if (!Files.exists(folder)) {
                    try {
                        Files.createDirectory(folder);
                    } catch (IOException ex) {
                        logger.error("Failed to create directory", ex);
                    }
                }
                try {
                    PNGJavaFXUtils.writeImage(image, Path.of( "screenshots", System.nanoTime() + ".png"));
                } catch (Exception ex) {
                    logger.error("Failed to write screenshot", ex);
                }
            }
        });
        stage.setTitle("Pong");
        stage.show();
    }
}
