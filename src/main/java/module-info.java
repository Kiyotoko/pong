module pong {
    // Graphics
    requires javafx.base;
    requires javafx.controls;
    requires javafx.graphics;

    // Proto buffer
    requires com.google.protobuf;
    requires com.google.common;

    // gRPC
    requires io.grpc;
    requires io.grpc.stub;
    requires io.grpc.protobuf;
    requires annotations.api;
    requires org.slf4j;
    requires java.desktop;
    requires ch.qos.logback.core;
}