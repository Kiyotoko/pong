package org.kiyotoko.pong.game;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;
import org.kiyotoko.pong.App;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Game extends Scene {
    private final List<GameObject> objects = new ArrayList<>();
    private final Map<String, Player> players = new HashMap<>();
    private final Map<String, Ball> balls = new HashMap<>();

    private final Group content;

    private final Timeline timeline = new Timeline(new KeyFrame(Duration.millis(10),
            event -> updateAll()));

    public Game(Group content) {
        super(content, 720, 360);
        this.content = content;
        setFill(Color.BLACK);
    }

    public void updateAll() {
        for (var object : getGameObjects()) object.update();
    }

    public void clear() {
        getGameObjects().clear();
        getContent().getChildren().clear();
    }

    public void createLines() {
        for (double y = (360 % 40 + 20.) * 0.5; y < getHeight(); y += 40.) {
            var line = new Rectangle(getWidth() * 0.5, y, 5, 20);
            line.setFill(Color.WHITE);
            getContent().getChildren().add(line);
        }
    }

    public void createPlayers() {
        Player player1 = new Player(this);
        addEventHandler(KeyEvent.KEY_PRESSED, player1.getEventHandler(KeyCode.S, KeyCode.W, true));
        addEventHandler(KeyEvent.KEY_RELEASED, player1.getEventHandler(KeyCode.S, KeyCode.W, false));

        Player player2 = new Player(this);
        addEventHandler(KeyEvent.KEY_PRESSED, player2.getEventHandler(KeyCode.DOWN, KeyCode.UP, true));
        addEventHandler(KeyEvent.KEY_RELEASED, player2.getEventHandler(KeyCode.DOWN, KeyCode.UP, false));
    }

    public void createBall() {
        Ball ball = new Ball(this);
        ball.setVelocity(new Point2D(1.0, 1.0));
        ball.setPosition(new Point2D(getWidth() * 0.5, getHeight() * 0.5));
    }

    public void startTimeline() {
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    public void start() {
        createLines();
        createPlayers();
        createBall();
        startTimeline();
    }

    public void restart() {
        clear();
        start();
    }

    public void end(int winner) {
        timeline.stop();

        Label label = new Label("PLAYER " + winner + " WINS");
        label.setTextFill(Color.WHITE);
        label.setFont(Font.loadFont(App.class.getResourceAsStream("ARCADECLASSIC.TTF"), 72));

        Label restart = new Label("RESTART");
        restart.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> restart());
        restart.setTextFill(Color.WHITE);
        restart.setFont(Font.loadFont(App.class.getResourceAsStream("ARCADECLASSIC.TTF"), 72));

        VBox box = new VBox(20);
        box.getChildren().addAll(new BorderPane(label), new BorderPane(restart));
        box.setAlignment(Pos.CENTER);

        BorderPane pane = new BorderPane(box);
        pane.setPrefSize(getWidth(), getHeight());
        getContent().getChildren().add(pane);
    }

    public List<GameObject> getGameObjects() {
        return objects;
    }

    public Map<String, Player> getPlayers() {
        return players;
    }

    public Map<String, Ball> getBalls() {
        return balls;
    }

    public Group getContent() {
        return content;
    }
}
