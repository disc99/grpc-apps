package com.example.service.front;

import io.grpc.*;
import io.grpc.examples.helloworld.Error;
import io.grpc.examples.helloworld.GreeterGrpc;
import io.grpc.examples.helloworld.HelloReply;
import io.grpc.examples.helloworld.HelloRequest;
import io.grpc.protobuf.ProtoUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class FrontServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FrontServiceApplication.class, args);
    }

    @GetMapping("/hello")
    String hello() {
        HelloRequest request = HelloRequest.newBuilder().setName("Tom").build();
        return stub().sayHelloUnary(request).toString();
    }

    @GetMapping("/err")
        String err() {
            HelloRequest request = HelloRequest.newBuilder().setName("Tom").build();
            try {
                return stub().sayHello(request).toString();
            } catch (StatusRuntimeException e) {
                Status status = Status.fromThrowable(e);
                Metadata metadata = Status.trailersFromThrowable(e);
                Error error = metadata.get(ProtoUtils.keyForProto(Error.getDefaultInstance()));
                return String.format("ERROR -------\nstatus=%s\nmetadata=%s\nerror=%s", status, metadata, error);
            }
        }

    GRpcTemplate gRpcTemplate = new GRpcTemplate();

    @GetMapping("/template")
    String template() {
        HelloRequest request = HelloRequest.newBuilder().setName("Tom").build();
        HelloReply reply = gRpcTemplate.unary(channel -> GreeterGrpc.newBlockingStub(channel).sayHelloUnary(request));
        return reply.toString();
    }

    private GreeterGrpc.GreeterBlockingStub stub() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 6565)
                .usePlaintext(true)
                .build();
        return GreeterGrpc.newBlockingStub(channel);
    }
}
