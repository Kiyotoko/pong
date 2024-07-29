package org.kiyotoko.pong;

import javafx.geometry.Point2D;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.function.BiConsumer;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TestReflection {
    @Test
    void angle() {
        BiConsumer<Double, Point2D> test = (expected, input) -> {
            var theta = Math.atan2(
                    input.getY(),
                    input.getX()
            );

            Assertions.assertEquals(expected, Math.toDegrees(theta));
        };

        test.accept(0., new Point2D(1, 0));
        test.accept(90., new Point2D(0, 1));
        test.accept(180., new Point2D(-1, 0));
        test.accept(-90., new Point2D(0, -1));
        test.accept(45., new Point2D(1, 1));
        test.accept(-45., new Point2D(1, -1));
        test.accept(135., new Point2D(-1, 1));
        test.accept(-135., new Point2D(-1, -1));
    }

    @Test
    void reflection() {
        BiConsumer<Double, Point2D> test = (expected, input) -> {
            var theta = Math.atan2(
                    -input.getY(),
                    input.getX()
            ) + Math.signum(input.getX() * input.getY()) * Math.PI * 0.5;

            Assertions.assertEquals(round(angle(expected)), round(angle(Math.toDegrees(theta))));
        };

        test.accept(135., new Point2D(-1, 1));
        test.accept(26.565, new Point2D(1, 2));
    }

    @Test
    void velocity() {
        BiConsumer<Point2D, Point2D> test = (Point2D expected, Point2D input) -> {
            var theta = Math.atan2(
                    -input.getY(),
                    input.getX()
            ) + Math.signum(input.getX() * input.getY()) * Math.PI * 0.5;

            Assertions.assertEquals(round(expected.normalize()), round(new Point2D(Math.cos(theta), -Math.sin(theta))));
        };

        test.accept(new Point2D(-1, 1), new Point2D(-1, -1));
        test.accept(new Point2D(1, 1), new Point2D(1, -1));
        test.accept(new Point2D(1, -1), new Point2D(1, 1));
        test.accept(new Point2D(-1, -1), new Point2D(-1, 1));
        test.accept(new Point2D(-3, -1), new Point2D(-1, 3));
        test.accept(new Point2D(3, -1), new Point2D(1, 3));
        test.accept(new Point2D(1, -3), new Point2D(3, 1));
        test.accept(new Point2D(-3, 1), new Point2D(-1, -3));
        test.accept(new Point2D(2, -5), new Point2D(5, 2));
    }

    static Point2D round(Point2D value) {
        return new Point2D(round(value.getX()), round(value.getX()));
    }

    static double round(double value) {
        return Math.round(value * 1e3) / 1e3;
    }

    static double angle(double value) {
        return value < 0 ? value + 360 : value % 360;
    }
}
