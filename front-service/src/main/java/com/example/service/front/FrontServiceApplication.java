package com.example.service.front;

import io.grpc.*;
import io.grpc.examples.helloworld.GreeterGrpc;
import io.grpc.examples.helloworld.HelloRequest;
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
            return stub().sayError(request).toString();
        } catch (StatusRuntimeException e) {
            Status status = Status.fromThrowable(e);
            Metadata metadata = Status.trailersFromThrowable(e);
            return String.format("ERROR -------\nstatus=%s\nmetadata=%s", status, metadata);
        }
    }

    private GreeterGrpc.GreeterBlockingStub stub() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 6565)
                .usePlaintext(true)
                .build();
        return GreeterGrpc.newBlockingStub(channel);
    }
}
