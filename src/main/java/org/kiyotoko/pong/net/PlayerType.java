package org.kiyotoko.pong.net;

import javafx.scene.input.KeyEvent;
import org.kiyotoko.pong.game.Bindings;
import org.kiyotoko.pong.game.Control;
import org.kiyotoko.pong.game.Player;

import java.util.function.BiConsumer;

public enum PlayerType {
    LOCAL((game, player) -> {
        game.getConnections().put(LocalGame.nextToken(), player);
        var p = game.getConnections().size();
        game.addEventHandler(KeyEvent.KEY_PRESSED, player.getEventHandler(
                Bindings.getKeyCode(p, Control.UP),
                Bindings.getKeyCode(p, Control.DOWN), true));
        game.addEventHandler(KeyEvent.KEY_RELEASED, player.getEventHandler(
                Bindings.getKeyCode(p, Control.UP),
                Bindings.getKeyCode(p, Control.DOWN), false));
    }),
    NETWORK((game, player) -> {}),
    COMPUTER((game, player) -> {
        game.getConnections().put(LocalGame.nextToken(), player);
        player.enableComputerInput();
    });

    private final BiConsumer<LocalGame, Player> action;

    PlayerType(BiConsumer<LocalGame, Player> action) {
        this.action = action;
    }

    public BiConsumer<LocalGame, Player> getAction() {
        return action;
    }
}
