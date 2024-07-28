package org.kiyotoko.pong.net;

import javafx.scene.Group;
import org.kiyotoko.pong.game.Ball;
import org.kiyotoko.pong.game.Game;
import org.kiyotoko.pong.game.Player;

public class PongLocal {
    private final Game game = new Game(new Group());

    public PongLocal() {
        game.createLines();
        game.createPlayers();
        game.createBall();

        for (var object : game.getGameObjects()) {
            if (object instanceof Player) {
                game.getPlayers().put(object.toString(), (Player) object);
            } else {
                game.getBalls().put(object.toString(), (Ball) object);
            }
        }

        game.startTimeline();
    }

    public Game getGame() {
        return game;
    }
}
