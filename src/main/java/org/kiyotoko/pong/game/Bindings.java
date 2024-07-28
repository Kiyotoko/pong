package org.kiyotoko.pong.game;

import javafx.scene.input.KeyCode;
import org.kiyotoko.pong.App;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.Map;
import java.util.Properties;

public final class Bindings {
    private static final Logger logger = LoggerFactory.getLogger(Bindings.class);

    private static final String USER_PROPERTIES = "keys.properties";
    private static final String DEFAULT_PROPERTIES = "default.properties";

    private static class BindingsHelper {
        private static final Bindings LOADED = new Bindings();
    }

    private final EnumMap<Control, KeyCode> keyBindings = new EnumMap<>(Control.class);

    private Bindings() {
        Properties properties = new Properties(prepareDefaults());
        try (InputStream stream = new FileInputStream(USER_PROPERTIES)) {
            properties.load(stream);
        } catch (IOException ex) {
            logger.error("Could not load user key bindings", ex);
        } finally {
            loadFromProperties(properties);
        }
    }

    public static KeyCode getKeyCode(int player, String direction) {
        return BindingsHelper.LOADED.getKeyBindings().get(Control.getControl(player, direction));
    }

    private static Properties prepareDefaults() {
        Properties backup = new Properties();
        try (InputStream stream = App.class.getResourceAsStream(DEFAULT_PROPERTIES)) {
            if (stream != null) {
                if (!Files.exists(Path.of(USER_PROPERTIES))) {
                    Files.copy(stream, Path.of("keys.properties"));
                }
                backup.load(stream);
            } else {
                logger.error("Stream is null!", new NullPointerException());
            }
        } catch (IOException ex) {
            logger.error("Could not copy default key bindings", ex);
        }
        return backup;
    }

    public void loadFromProperties(Properties properties) {
        for (var control : Control.values()) {
            var value = properties.getProperty(control.name(), control.name());
            keyBindings.put(control, KeyCode.valueOf(value));
        }
    }

    public Map<Control, KeyCode> getKeyBindings() {
        return keyBindings;
    }
}
