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
import javafx.scene.input.KeyEvent;

import org.kiyotoko.pong.game.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class LocalGame extends Game {
    private static final Logger logger = LoggerFactory.getLogger(LocalGame.class);

    private final Server server;
    private final Map<ByteString, Player> connections = new HashMap<>(2);

    String password = "";

    public LocalGame() {
        super(new Group());

        // Creates game data
        var player = new Player(this);
        getConnections().put(LocalGame.nextToken(), player);
        var p = getConnections().size();
        addEventHandler(KeyEvent.KEY_PRESSED, player.getEventHandler(
                Bindings.getKeyCode(p, Control.UP),
                Bindings.getKeyCode(p, Control.DOWN), true));
        addEventHandler(KeyEvent.KEY_RELEASED, player.getEventHandler(
                Bindings.getKeyCode(p, Control.UP),
                Bindings.getKeyCode(p, Control.DOWN), false));
        getPlayers().put(player.toString(), player);

        var remote = new Player(this);
        getPlayers().put(remote.toString(), remote);

        Ball ball = new Ball(this);
        getBalls().put(ball.toString(), ball);
        getPane().setVisible(true);

        // Start server
        Server host = null;        
        try {
            host = ServerBuilder.forPort(4242).addService(new PongService()).build();
            host.start();
            logger.info("Started server");
        } catch (IOException ex) {
            error("Could not start server");
            logger.error("Could not start server", ex);
        }
        this.server = host;
    
        // Get external ip address
        try {
            info(getExternalIpAddress());
        } catch (SocketException ex) {
            error(ex.getMessage());
            logger.error(ex.getMessage(), ex);
        }

        // Sets the event handler later, so that the window is initialisised
        Platform.runLater(() -> getWindow().setOnCloseRequest(e -> stop()));
    }

    private static String getExternalIpAddress() throws SocketException {
        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
        while (networkInterfaces.hasMoreElements()) {
            NetworkInterface networkInterface = networkInterfaces.nextElement();

            // Skip loopback and non-active interfaces
            if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                continue;
            }

            // Get the IP addresses for this interface
            Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
            while (inetAddresses.hasMoreElements()) {
                InetAddress inetAddress = inetAddresses.nextElement();

                // Skip loopback addresses
                if (!inetAddress.isLoopbackAddress() && inetAddress.isSiteLocalAddress()) {
                    return inetAddress.getHostAddress();
                }
            }
        }
        throw new SocketException("No external IP address found.");
    }

    public void stop() {
        if (server != null) {
            server.shutdown();
            logger.info("Stopped server");
        }
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
                    logger.info("Player joined ({} -> {})", token, player);
                    getConnections().put(token, player);
                    startTimeline();
                    getPane().setVisible(false);
                    
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
