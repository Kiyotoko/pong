package org.kiyotoko.pong.net;

import com.google.common.hash.Hashing;
import com.google.protobuf.ByteString;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.Status;
import io.grpc.StatusException;
import io.grpc.stub.StreamObserver;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import org.kiyotoko.pong.game.Ball;
import org.kiyotoko.pong.game.Game;
import org.kiyotoko.pong.game.Player;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class PongServer {
    private final Server server = ServerBuilder.forPort(4242).addService(new PongService()).build();
    private final Map<ByteString, Player> players = new HashMap<>(2);
    private final Game game = new Game(new Group());

    String password = "";

    public PongServer() throws IOException {
        server.start();
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
    }

    public void stop() {
        server.shutdown();
    }

    public Game getGame() {
        return game;
    }

    protected class PongService extends PongGrpc.PongImplBase {
        private final Random random = new Random();

        @Override
        public void join(JoinRequest request, StreamObserver<JoinReply> responseObserver) {
            if (request.getPassword().equals(password)) {
                if (players.size() < 2) {
                    var token = ByteString.copyFrom(
                            Hashing.fingerprint2011().hashString("" + (random.nextDouble() * System.nanoTime()),
                                    Charset.defaultCharset()).asBytes());
                    var player = (Player) game.getGameObjects().get(players.size());
                    players.put(token, player);

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
            Player player = players.get(request.getToken());
            if (player != null) {
                player.setVelocity(new Point2D(0.0, (request.getUpPressed() ? 1.0 : 0.0) +
                        (request.getDownPressed() ? -1.0 : 0.0)));

                responseObserver.onNext(UpdateReply.newBuilder().addAllPlayers(game.getPlayers().values()
                        .stream().map(Player::getPlayer).collect(Collectors.toList())).addAllBalls(game.getBalls()
                        .values().stream().map(Ball::getGameObject).collect(Collectors.toList())).build());
                responseObserver.onCompleted();
            } else {
                responseObserver.onError(new StatusException(Status.UNAUTHENTICATED));
            }
        }
    }
}
