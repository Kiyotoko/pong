package org.kiyotoko.pong.game;

import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.kiyotoko.pong.App;
import org.kiyotoko.pong.menu.UITools;
import org.kiyotoko.pong.net.PlayerOuterClass;

public class Player extends GameObject {
    private int score;

    private final Label scoreboard = new Label("" + score);
    private final BorderPane pane = new BorderPane(scoreboard);

    private final PlayerOuterClass.Player.Builder builder = PlayerOuterClass.Player.newBuilder();

    public Player(Game game) {
        super(game);

        getScoreboard().setTextFill(UITools.TEXT_COLOR);
        getScoreboard().setFont(Font.loadFont(App.class.getResourceAsStream("PixelFont.otf"), 56));

        getPane().setLayoutX(getGame().getWidth() * (0.25 + 0.5 * (getGame().getGameObjects().size() - 1)) - 50);
        getPane().setLayoutY((getGame().getHeight() - 100.0) * 0.5);
        getPane().setPrefSize(100.0, 100.0);
        getGame().getContent().getChildren().add(getPane());

        setPosition(new Point2D(getGame().getWidth() * (0.5 + 0.4 * (getGame().getGameObjects().size() * 2.0 - 3.0)) + 4.0, getGame().getHeight() * 0.5));

        getShape().setWidth(8.0);
        getShape().setHeight(100.0);
    }

    @Override
    public void update() {
        super.update();
        if (computer) {
            createComputerInput();
        }
    }

    private boolean upPressed;
    private boolean downPressed;
    private boolean computer;

    public EventHandler<KeyEvent> getEventHandler(
            KeyCode moveUp, KeyCode moveDown, boolean onAction) {
        return event -> {
            if (event.getCode() == moveUp) {
                upPressed = onAction;
            }
            if (event.getCode() == moveDown) {
                downPressed = onAction;
            }
            setVelocity(new Point2D(0.0, (upPressed ? -1.0 : 0.0) + (downPressed ? 1.0 : 0.0)));
        };
    }

    private void createComputerInput() {
        Player opponent = null;
        for (var player : getGame().getPlayers().values()) {
            if (player.getPosition().distance(getPosition()) > 100) {
                opponent = player;
                break;
            }
        }
        double corrigation = 0;
        if (opponent != null) {
            if (opponent.getPosition().getY() <= getGame().getHeight() * 0.5) {
                corrigation = 3;
            } else {
                corrigation = -3;
            }
        }

        for (var ball : getGame().getBalls().values()) {
            if (ball.getPosition().getY() < getPosition().getY() + corrigation) {
                upPressed = true;
                downPressed = false;
            } else {
                upPressed = false;
                downPressed = true;
            }
            setVelocity(new Point2D(0.0, (upPressed ? -1.0 : 0.0) + (downPressed ? 1.0 : 0.0)));
        }
    }

    public void enableComputerInput() {
        computer = true;
    }

    @Override
    public void setPosition(Point2D position) {
        super.setPosition(new Point2D(position.getX(),
                Math.max(Math.min(position.getY(), getGame().getHeight() - 50), 50)));
    }

    public void setPoints(int points) {
        this.score = points;
        getScoreboard().setText("" + points);
        if (points >= 11) {
            getGame().end(getGame().getGameObjects().indexOf(this) + 1);
        }
    }

    public int getPoints() {
        return score;
    }

    public Label getScoreboard() {
        return scoreboard;
    }

    public BorderPane getPane() {
        return pane;
    }

    public PlayerOuterClass.Player getPlayer() {
        return builder.setScore(score).setPaddle(getGameObject()).build();
    }
}
