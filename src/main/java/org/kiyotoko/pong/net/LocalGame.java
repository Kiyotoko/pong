package org.kiyotoko.pong.net;

import com.google.common.hash.Hashing;
import com.google.protobuf.ByteString;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.Status;
import io.grpc.StatusException;
import io.grpc.stub.StreamObserver;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import org.kiyotoko.pong.game.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class LocalGame extends Game {
    private static final Logger logger = LoggerFactory.getLogger(LocalGame.class);

    private final Server server = ServerBuilder.forPort(4242).addService(new PongService()).build();
    private final Map<ByteString, Player> connections = new HashMap<>(2);

    String password = "";

    public LocalGame(PlayerType... types) {
        super(new Group());

        try {
            server.start();
        } catch (IOException ex) {
            logger.error("Could not start server", ex);
        }

        for (var type : types) {
            var player = new Player(this);
            type.getAction().accept(this, player);
            getPlayers().put(player.toString(), player);
        }
        Ball ball = new Ball(this);
        getBalls().put(ball.toString(), ball);
        if (getConnections().size() == getPlayers().size()) startTimeline();

        Platform.runLater(() -> getWindow().setOnCloseRequest(e -> stop()));
    }

    public void stop() {
        server.shutdown();
    }

    @Override
    public void exit() {
        getWindow().setOnCloseRequest(e -> {});
        super.exit();
        stop();
    }

    private static final Random random = new Random();

    static ByteString nextToken() {
        return ByteString.copyFrom(
                Hashing.fingerprint2011().hashString("" + (random.nextDouble() * System.nanoTime()),
                        Charset.defaultCharset()).asBytes());
    }

    protected class PongService extends PongGrpc.PongImplBase {

        @Override
        public void join(JoinRequest request, StreamObserver<JoinReply> responseObserver) {
            if (request.getPassword().equals(password)) {
                if (getConnections().size() < 2) {
                    var token = nextToken();
                    var player = (Player) getGameObjects().get(getConnections().size());
                    getConnections().put(token, player);
                    if (getConnections().size() == 2) startTimeline();

                    responseObserver.onNext(JoinReply.newBuilder().setToken(token).setPlayerId(player.toString()).build());
                    responseObserver.onCompleted();
                } else {
                    responseObserver.onError(new StatusException(Status.OUT_OF_RANGE));
                }
            } else {
                responseObserver.onError(new StatusException(Status.UNAUTHENTICATED));
            }
        }

        @Override
        public void update(UpdateRequest request, StreamObserver<UpdateReply> responseObserver) {
            Player player = getConnections().get(request.getToken());
            if (player != null) {
                player.setVelocity(new Point2D(0.0, (request.getUpPressed() ? 1.0 : 0.0) +
                        (request.getDownPressed() ? -1.0 : 0.0)));

                responseObserver.onNext(UpdateReply.newBuilder().addAllPlayers(getPlayers().values()
                        .stream().map(Player::getPlayer).collect(Collectors.toList())).addAllBalls(getBalls()
                        .values().stream().map(Ball::getGameObject).collect(Collectors.toList())).build());
                responseObserver.onCompleted();
            } else {
                responseObserver.onError(new StatusException(Status.UNAUTHENTICATED));
            }
        }
    }

    public Map<ByteString, Player> getConnections() {
        return connections;
    }
}
