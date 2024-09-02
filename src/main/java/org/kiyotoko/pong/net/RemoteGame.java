package org.kiyotoko.pong.net;

import com.google.protobuf.ByteString;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import javafx.scene.Group;
import javafx.scene.input.KeyEvent;
import org.kiyotoko.pong.game.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoteGame extends Game {
    private static final Logger logger = LoggerFactory.getLogger(RemoteGame.class);

    private final PongGrpc.PongBlockingStub blockingStub;

    public RemoteGame(String address) {
        super(new Group());

        Channel channel = ManagedChannelBuilder.forAddress(address, 4242).usePlaintext().build();
        blockingStub = PongGrpc.newBlockingStub(channel);

        for (int p = 1; p < 4; p++) {
            addEventHandlers(p);
        }
        join();
    }

    private void addEventHandlers(final int player) {
        var up = Bindings.getKeyCode(player, Control.UP);
        var down = Bindings.getKeyCode(player, Control.DOWN);
        addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == up) {
                upPressed = true;
            }
            if (event.getCode() == down) {
                downPressed = true;
            }
        });
        addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            if (event.getCode() == up) {
                upPressed = false;
            }
            if (event.getCode() == down) {
                downPressed = false;
            }
        });
    }

    @Override
    public void updateAll() {
        super.updateAll();
        update(UpdateRequest.newBuilder().setToken(token).setUpPressed(upPressed).setDownPressed(downPressed).build());
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

        logger.info("Game joined with id={} and token={}", playerId, token);
        startTimeline();
    }

    public synchronized void update(UpdateRequest request) {
        var reply = blockingStub.update(request);

        for (PlayerOuterClass.Player player : reply.getPlayersList()) {
            var instance = getPlayers().computeIfAbsent(player.getPaddle().getObjectId(), id -> new Player(this));
            instance.setPoints(player.getScore());
            instance.setPosition(GameObject.fromVector(player.getPaddle().getPosition()));
            instance.setVelocity(GameObject.fromVector(player.getPaddle().getVelocity()));
        }

        for (var ball : reply.getBallsList()) {
            var instance = getBalls().computeIfAbsent(ball.getObjectId(), id -> new Ball(this));
            instance.setPosition(GameObject.fromVector(ball.getPosition()));
            instance.setVelocity(GameObject.fromVector(ball.getVelocity()));
        }
    }
}
