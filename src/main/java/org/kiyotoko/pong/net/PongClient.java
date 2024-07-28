package org.kiyotoko.pong.net;

import com.google.protobuf.ByteString;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import javafx.scene.Group;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.kiyotoko.pong.game.Ball;
import org.kiyotoko.pong.game.Game;
import org.kiyotoko.pong.game.GameObject;
import org.kiyotoko.pong.game.Player;

public class PongClient {
    private final Game game = new Game(new Group()) {
        @Override
        public void updateAll() {
            super.updateAll();
            update(UpdateRequest.newBuilder().setToken(token).setUpPressed(upPressed).setDownPressed(downPressed).build());
        }
    };

    private final PongGrpc.PongBlockingStub blockingStub;

    public PongClient() {
        Channel channel = ManagedChannelBuilder.forAddress("localhost", 4242).usePlaintext().build();
        blockingStub = PongGrpc.newBlockingStub(channel);

        getGame().createLines();
        getGame().addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.UP || event.getCode() == KeyCode.W) {
                upPressed = true;
            }
            if (event.getCode() == KeyCode.DOWN || event.getCode() == KeyCode.S) {
                downPressed = true;
            }

            System.out.println(upPressed + " | " + downPressed);
        });
        getGame().addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            if (event.getCode() == KeyCode.UP || event.getCode() == KeyCode.W) {
                upPressed = false;
            }
            if (event.getCode() == KeyCode.DOWN || event.getCode() == KeyCode.S) {
                downPressed = false;
            }

            System.out.println(upPressed + " | " + downPressed);
        });
    }

    // Event data
    private boolean upPressed = false;
    private boolean downPressed = false;

    // Authentication data
    ByteString token;
    String playerId;

    public void join() {
        var reply = blockingStub.join(JoinRequest.newBuilder().build());
        token = reply.getToken();
        playerId = reply.getPlayerId();

        getGame().startTimeline();
    }

    public synchronized void update(UpdateRequest request) {
        var reply = blockingStub.update(request);

        for (PlayerOuterClass.Player player : reply.getPlayersList()) {
            var instance = game.getPlayers().computeIfAbsent(player.getPaddle().getObjectId(), id -> new Player(game));
            instance.setPoints(player.getScore());
            instance.setPosition(GameObject.fromVector(player.getPaddle().getPosition()));
            instance.setVelocity(GameObject.fromVector(player.getPaddle().getVelocity()));
        }

        for (var ball : reply.getBallsList()) {
            var instance = game.getBalls().computeIfAbsent(ball.getObjectId(), id -> new Ball(game));
            instance.setPosition(GameObject.fromVector(ball.getPosition()));
            instance.setVelocity(GameObject.fromVector(ball.getVelocity()));
        }
    }

    public Game getGame() {
        return game;
    }
}
