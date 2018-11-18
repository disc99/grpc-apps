package com.example.service.front;

import com.google.protobuf.Any;
import com.google.protobuf.Int32Value;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.rpc.BadRequest;
import com.google.rpc.Code;
import com.google.rpc.Status;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.examples.helloworld.GreeterGrpc;
import io.grpc.examples.helloworld.HelloReply;
import io.grpc.examples.helloworld.HelloRequest;
import io.grpc.protobuf.StatusProto;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@SpringBootApplication
@RestController
public class FrontServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FrontServiceApplication.class, args);
    }


    @GetMapping("/hello")
    String hello() {
        return call("Tom");
    }

    @GetMapping("/400")
    String m400() {
        return call("400");
    }

    @GetMapping("/404")
    String m404() {
        return call("404");
    }

    @GetMapping("/500")
    String m500() {
        return call("500");
    }

    @GetMapping("/native")
    String nativeRuntimeException() {
        return call("native");
    }

    String call(String number) {
        ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:6565")
                .usePlaintext(true)
                .build();

        GreeterGrpc.GreeterBlockingStub blockingStub = GreeterGrpc.newBlockingStub(channel);

        HelloRequest request = HelloRequest.newBuilder()
                .setName(number)
                .setAge(Int32Value.newBuilder().setValue(10).build())
                .build();

        try {
            HelloReply helloReply = blockingStub.sayHello(request);
            return new User(helloReply.getMessage()).toString();
        } catch (StatusRuntimeException e) {
            Status status = StatusProto.fromThrowable(e);
            if (status == null) {
                throw e;
            }

            switch (status.getCode()) {
                case Code.INVALID_ARGUMENT_VALUE:
                    return status.getDetailsList().stream()
                            .filter(detail -> detail.is(BadRequest.class))
                            .flatMap(this::messages)
                            .collect(toList()).toString();

                case Code.NOT_FOUND_VALUE:
                    throw e;

                case Code.INTERNAL_VALUE:
                    throw e;

                default:
                    throw e;
            }
        }
    }

    private Stream<String> messages(Any detail) {
        try {
            return detail.unpack(BadRequest.class).getFieldViolationsList().stream()
                    .map(BadRequest.FieldViolation::getDescription);
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }
    }
}

class User {
    String name;
    public User(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                '}';
    }
}