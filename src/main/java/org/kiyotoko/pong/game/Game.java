package org.kiyotoko.pong.game;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.kiyotoko.pong.menu.Menu;
import org.kiyotoko.pong.menu.UITools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Game extends Scene {

    public static Game newSingleplayer() {
        Game game = new Game(new Group());

        var player = new Player(game);
        game.addEventHandler(KeyEvent.KEY_PRESSED, player.getEventHandler(
                Bindings.getKeyCode(1, Control.UP),
                Bindings.getKeyCode(1, Control.DOWN), true));
        game.addEventHandler(KeyEvent.KEY_RELEASED, player.getEventHandler(
                Bindings.getKeyCode(1, Control.UP),
                Bindings.getKeyCode(1, Control.DOWN), false));
        game.getPlayers().put(player.toString(), player);

        var computer = new Player(game);
        computer.enableComputerInput();
        game.getPlayers().put(computer.toString(), computer);

        Ball ball = new Ball(game);
        game.getBalls().put(ball.toString(), ball);
        game.startTimeline();

        return game;
    }

    public static Game newLocalMultiplayer() {
        Game game = new Game(new Group());

        for (int i = 1; i < 3; i++) {
            var player = new Player(game);
            game.addEventHandler(KeyEvent.KEY_PRESSED, player.getEventHandler(
                    Bindings.getKeyCode(i, Control.UP),
                    Bindings.getKeyCode(i, Control.DOWN), true));
            game.addEventHandler(KeyEvent.KEY_RELEASED, player.getEventHandler(
                    Bindings.getKeyCode(i, Control.UP),
                    Bindings.getKeyCode(i, Control.DOWN), false));
            game.getPlayers().put(player.toString(), player);
        }

        Ball ball = new Ball(game);
        game.getBalls().put(ball.toString(), ball);
        game.startTimeline();

        return game;
    }

    // Game objects
    private final List<GameObject> objects = new ArrayList<>();
    private final Map<String, Player> players = new LinkedHashMap<>();
    private final Map<String, Ball> balls = new LinkedHashMap<>();

    // Graphics
    private final Group content;
    private final BorderPane pane;
    private final Label info = UITools.createText("");
    private final Label exit = UITools.createButton("Exit", () -> exit());

    // Timeline
    private final Timeline timeline = new Timeline(new KeyFrame(Duration.millis(10),
            event -> updateAll()));

    public Game(Group content) {
        super(content, 720, 480);
        this.content = content;

        VBox box = new VBox(20);
        box.getChildren().addAll(new BorderPane(info), new BorderPane(exit));
        box.setAlignment(Pos.CENTER);

        this.pane = new BorderPane(box);
        pane.setPrefSize(getWidth(), getHeight());
        pane.setVisible(false);
        getContent().getChildren().add(pane);
        
        setFill(Color.BLACK);
        createLines();
    }

    public void updateAll() {
        for (var object : getGameObjects()) object.update();
    }

    public void createLines() {
        for (double y = (360 % 40 + 20.) * 0.5; y < getHeight(); y += 40.) {
            var line = new Rectangle(getWidth() * 0.5, y, 5, 20);
            line.setFill(Color.WHITE);
            getContent().getChildren().add(line);
        }
    }

    public void startTimeline() {
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    public void end(int winner) {
        timeline.stop();
        info("Player " + winner + " wins");
        pane.setVisible(true);
    }

    public void info(String msg) {
        info.setText(msg);
        info.setTextFill(UITools.INFO_COLOR);
    }

    public void error(String msg) {
        info.setText(msg);
        info.setTextFill(UITools.ERROR_COLOR);
    }

    public void exit() {
        ((Stage) getWindow()).setScene(new Menu(new Group()));
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

    public BorderPane getPane() {
        return pane;
    }

    public Timeline getTimeline() {
        return timeline;
    }
}
