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
import org.kiyotoko.pong.net.PlayerOuterClass;

public class Player extends GameObject {
    private int points;

    private final Label scoreboard = new Label("" + points);
    private final BorderPane pane = new BorderPane(scoreboard);

    private final PlayerOuterClass.Player.Builder builder = PlayerOuterClass.Player.newBuilder();

    public Player(Game game) {
        super(game);

        getScoreboard().setTextFill(Color.WHITE);
        getScoreboard().setFont(Font.loadFont(App.class.getResourceAsStream("ARCADECLASSIC.TTF"), 56));

        getPane().setLayoutX(getGame().getWidth() * (0.25 + 0.5 * (getGame().getGameObjects().size() - 1)) - 50);
        getPane().setLayoutY((getGame().getHeight() - 100.0) * 0.5);
        getPane().setPrefSize(100.0, 100.0);
        getGame().getContent().getChildren().add(getPane());

        setPosition(new Point2D(getGame().getWidth() * (0.5 + 0.4 * (getGame().getGameObjects().size() * 2.0 - 3.0)) + 4.0, getGame().getHeight() * 0.5));

        getShape().setWidth(8.0);
        getShape().setHeight(100.0);
    }

    private boolean upPressed;
    private boolean downPressed;

    public EventHandler<KeyEvent> getEventHandler(
            KeyCode moveTop, KeyCode moveDown, boolean onAction) {
        return event -> {
            if (event.getCode() == moveTop) {
                upPressed = onAction;
            }
            if (event.getCode() == moveDown) {
                downPressed = onAction;
            }
            setVelocity(new Point2D(0.0, (upPressed ? 1.0 : 0.0) + (downPressed ? -1.0 : 0.0)));
        };
    }

    @Override
    public void setPosition(Point2D position) {
        super.setPosition(new Point2D(position.getX(),
                Math.max(Math.min(position.getY(), getGame().getHeight() - 50), 50)));
    }

    public void setPoints(int points) {
        this.points = points;
        getScoreboard().setText("" + points);
        if (points >= 11) {
            getGame().end(getGame().getGameObjects().indexOf(this) + 1);
        }
    }

    public int getPoints() {
        return points;
    }

    public Label getScoreboard() {
        return scoreboard;
    }

    public BorderPane getPane() {
        return pane;
    }

    public PlayerOuterClass.Player getPlayer() {
        return builder.setScore(points).setPaddle(getGameObject()).build();
    }
}
