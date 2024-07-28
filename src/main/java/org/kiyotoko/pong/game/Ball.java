package org.kiyotoko.pong.game;

import javafx.geometry.Point2D;
import java.util.List;

public class Ball extends GameObject {

    public Ball(Game game) {
        super(game);

        getShape().setWidth(8);
        getShape().setHeight(8);

        setPosition(new Point2D(getGame().getWidth() * 0.5, getGame().getHeight() * 0.5));
        setVelocity(new Point2D(1.0, 1.0));
    }

    @Override
    public void setPosition(Point2D position) {
        super.setPosition(position);
        for (var player : List.copyOf(getGame().getPlayers().values()) ) {
            if (getShape().getBoundsInParent().intersects(player.getShape().getBoundsInParent())) {
                setVelocity(new Point2D(-getVelocity().getX(), getVelocity().getY()).normalize().multiply(2.8));
                super.setPosition(getPosition().add(getVelocity()));
            }
        }
        if (getPosition().getY() < 4 || getPosition().getY() > getGame().getHeight() - 4) {
            setVelocity(new Point2D(getVelocity().getX(), -getVelocity().getY()));
        }
        if (getPosition().getX() < 0 || getPosition().getX() > getGame().getWidth()) {
            for (var player : List.copyOf(getGame().getPlayers().values())) {
                if (Math.abs(player.getPosition().getX() - getPosition().getX()) > 100) {
                    player.setPoints(player.getPoints() + 1);
                    setPosition(new Point2D(getGame().getWidth() * 0.5, getGame().getHeight() * 0.5));
                    setVelocity(new Point2D(-getVelocity().getX(), -getVelocity().getY()).normalize()
                                    .multiply(Math.sqrt(2)));
                    break;
                }
            }
        }
    }
}
