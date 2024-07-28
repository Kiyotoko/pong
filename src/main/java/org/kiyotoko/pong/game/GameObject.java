package org.kiyotoko.pong.game;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.kiyotoko.pong.net.GameObjectOuterClass;
import org.kiyotoko.pong.net.Vector2DOuterClass;

public class GameObject {
    private final Game game;
    private final Rectangle shape = new Rectangle();

    private Point2D position = Point2D.ZERO;
    private Point2D velocity = Point2D.ZERO;

    private final GameObjectOuterClass.GameObject.Builder builder = GameObjectOuterClass.GameObject.newBuilder();

    private static Vector2DOuterClass.Vector2D toVector(Point2D point) {
        return Vector2DOuterClass.Vector2D.newBuilder().setX(point.getX()).setY(point.getY()).build();
    }

    public static Point2D fromVector(Vector2DOuterClass.Vector2D vector) {
        return new Point2D(vector.getX(), vector.getY());
    }

    public GameObject(Game game) {
        this.game = game;
        this.builder.setObjectId(toString());

        getShape().setFill(Color.WHITE);
        getGame().getContent().getChildren().add(getShape());
        getGame().getGameObjects().add(this);
    }

    public void update() {
        setPosition(getPosition().add(getVelocity()));
    }

    public Game getGame() {
        return game;
    }

    public Rectangle getShape() {
        return shape;
    }

    public void setPosition(Point2D position) {
        this.position = position;

        getShape().setLayoutX(position.getX() - getShape().getWidth() * 0.5);
        getShape().setLayoutY(position.getY() - getShape().getHeight() * 0.5);
    }

    public Point2D getPosition() {
        return position;
    }

    public void setVelocity(Point2D velocity) {
        this.velocity = velocity;
        this.builder.setPosition(toVector(velocity));
    }

    public Point2D getVelocity() {
        return velocity;
    }

    public GameObjectOuterClass.GameObject getGameObject() {
        return builder.setPosition(toVector(getPosition())).build();
    }
}
