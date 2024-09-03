package org.kiyotoko.pong.game;

import javafx.geometry.Point2D;
import java.util.List;

public class Ball extends GameObject {

    private static final double SPEED = 3.4;

    public Ball(Game game) {
        super(game);

        getShape().setWidth(8);
        getShape().setHeight(8);

        setPosition(new Point2D(getGame().getWidth() * 0.5 - 4, getGame().getHeight() * 0.5 - 4));
        setVelocity(new Point2D(1.0, 0.0));
    }

    private int paddleCooldown = 0;
    private int wallCooldown = 0;

    @Override
    public void setPosition(Point2D position) {
        super.setPosition(position);
        countDown();
        checkPlayers();
        checkWalls();

        if (getPosition().getX() < 0.0 || getPosition().getX() > getGame().getWidth()) {
            for (var player : List.copyOf(getGame().getPlayers().values())) {
                if (Math.abs(player.getPosition().getX() - getPosition().getX()) > 100.0) {
                    player.setPoints(player.getPoints() + 1);
                    setPosition(new Point2D(getGame().getWidth() * 0.5 - 4, getGame().getHeight() * 0.5 - 4));
                    setVelocity(new Point2D(Math.signum(getVelocity().getX()), 0.0));
                    break;
                }
            }
        }
    }

    private void countDown() {
        paddleCooldown--;
        wallCooldown--;
    }

    private void checkPlayers() {
        if (paddleCooldown < 0) {
            for (var player : List.copyOf(getGame().getPlayers().values()) ) {
                if (getShape().getBoundsInParent().intersects(player.getShape().getBoundsInParent())) {
                    var theta = Math.atan2(
                            player.getShape().getBoundsInParent().getCenterY() - getShape().getBoundsInParent().getCenterY(),
                            player.getShape().getBoundsInParent().getCenterX() - getShape().getBoundsInParent().getCenterX()
                    );

                    setVelocity(new Point2D(-Math.cos(theta), -Math.sin(theta)).multiply(SPEED));
                    paddleCooldown = 100;
                    wallCooldown = 0;
                }
            }
        }
    }

    private void checkWalls() {
        if (wallCooldown < 0 && (getPosition().getY() < 4.0 || getPosition().getY() > getGame().getHeight() - 4.0)) {
            var theta = Math.atan2(
                    -getVelocity().getY(),
                    getVelocity().getX()
            ) + Math.signum(getVelocity().getX() * getVelocity().getY()) * Math.PI * 0.5;

            setVelocity(new Point2D(Math.cos(theta), -Math.sin(theta)).multiply(SPEED));
            wallCooldown = 100;
            paddleCooldown = 0;
        }
    }
}
