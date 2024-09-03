package org.kiyotoko.pong.net;

import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.input.KeyEvent;

import java.util.concurrent.TimeUnit;

import org.kiyotoko.pong.game.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoteGame extends Game {
    private static final Logger logger = LoggerFactory.getLogger(RemoteGame.class);

    private final ManagedChannel channel;
    private final PongGrpc.PongBlockingStub blockingStub;

    public RemoteGame(String address) {
        super(new Group());

        // Create channel and stub
        channel = ManagedChannelBuilder.forAddress(address, 4242).usePlaintext().build();
        blockingStub = PongGrpc.newBlockingStub(channel);

        // Simply add all handlers
        for (int p = 1; p < 4; p++) {
            addEventHandlers(p);
        }
        join(); // Joins the server

        // Sets the event handler later, so that the window is initialisised
        Platform.runLater(() -> getWindow().setOnCloseRequest(e -> stop()));
    }

    /**
     * Adds the key bindings from the specified player to the game.
     * 
     * @param player the key binding number of the player to add
     * @throws IllegalArgumentException if the player is outside of the range of 1 to 4
     */
    private void addEventHandlers(final int player) {
        if (player < 1 || player > 4) throw new IllegalArgumentException("Must be in the range of 1 to 4");
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

    /**
     * Updates the game locally, then synchronize the game with the server.
     */
    @Override
    public void updateAll() {
        super.updateAll();
        update(UpdateRequest.newBuilder().setToken(token).setUpPressed(upPressed).setDownPressed(downPressed).build());
    }

    @Override
    public void exit() {
        super.exit();
        stop();
    }

    /**
     * Stops the network channel.
     */
    private void stop() {
        channel.shutdown();
        try {
            // Wait for the channel to terminate, with a timeout of 5 seconds
            if (!channel.awaitTermination(5, TimeUnit.SECONDS)) {
                // If the channel is still not terminated, forcefully shutdown
                channel.shutdownNow();
            }
        } catch (InterruptedException ex) {
            // In case of interruption, forcefully shutdown and preserve the interruption status
            channel.shutdownNow();
            logger.error(ex.getMessage(), ex);
        }
    }

    // Event data
    private boolean upPressed = false;
    private boolean downPressed = false;

    // Authentication data
    ByteString token;
    String playerId;

    /**
     * Joins the server.
     * 
     * @throws Exception if it could not connect to the server
     */
    public void join() {
        try {
            var reply = blockingStub.join(JoinRequest.newBuilder().build());
            token = reply.getToken();
            playerId = reply.getPlayerId();

            logger.info("Game joined with id={} and token={}", playerId, token);
            startTimeline();
        } catch (Exception ex) {
            stop();
            logger.error("Could not join game", ex);
            throw ex;
        }
    }

    /**
     * Synchronizes this game with the state of the server. If a connection error happens,
     * it will stop the timeline and show the menu.
     * 
     * @param request the update request containing the inputs from this player
     */
    public synchronized void update(UpdateRequest request) {
        try {
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
        } catch (Exception ex) {
            error("Connection Error: " + ex.getMessage());
            logger.error("Connection error", ex);
            getTimeline().stop();
            getPane().setVisible(true);;
        }
    }
}
