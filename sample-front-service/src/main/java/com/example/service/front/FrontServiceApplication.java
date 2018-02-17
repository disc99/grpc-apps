package com.example.service.front;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import io.grpc.*;
import io.grpc.examples.helloworld.Error;
import io.grpc.examples.helloworld.GreeterGrpc;
import io.grpc.examples.helloworld.HelloReply;
import io.grpc.examples.helloworld.HelloRequest;
import io.grpc.protobuf.ProtoUtils;
import io.grpc.stub.StreamObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@RestController
@EnableEurekaClient
public class FrontServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FrontServiceApplication.class, args);
    }

    @GetMapping("/hello")
    String hello() {
        HelloRequest request = HelloRequest.newBuilder().setName("Tom").build();
        return blockingStub().sayHelloUnary(request).toString();
    }

    @GetMapping("/err")
    String err() {
        HelloRequest request = HelloRequest.newBuilder().setName("Tom").build();
        try {
            return blockingStub().sayHello(request).toString();
        } catch (StatusRuntimeException e) {
            Status status = Status.fromThrowable(e);
            Metadata metadata = Status.trailersFromThrowable(e);
            Error error = metadata.get(ProtoUtils.keyForProto(Error.getDefaultInstance()));
            return String.format("ERROR -------\nstatus=%s\nmetadata=%s\nerror=%s", status, metadata, error);
        }
    }


    @GetMapping("/unary")
    String unary() {
        HelloRequest request = HelloRequest.newBuilder().setName("Tom").build();
        return blockingStub().sayHelloUnary(request).toString();
    }

    @GetMapping("/serverStreaming")
    String serverStreaming() {
        HelloRequest request = HelloRequest.newBuilder().setName("Tom").build();
        Iterator<HelloReply> replies = blockingStub().sayHelloServerStreaming(request);
        List<HelloReply> response = new ArrayList<>();
        while (replies.hasNext()) {
            response.add(replies.next());
        }
        return response.toString();
    }

    @GetMapping("/clientStreaming")
    String clientStreaming() throws Exception {
        HelloRequest request = HelloRequest.newBuilder().setName("Tom").build();
        CountDownLatch finishLatch = new CountDownLatch(1);
        List<HelloReply> response = new ArrayList<>();
        StreamObserver<HelloRequest> streamObserver = stub().sayHelloClientStreaming(new StreamObserver<HelloReply>() {
            @Override
            public void onNext(HelloReply reply) {
                response.add(reply);
            }
            @Override
            public void onError(Throwable t) {
                // ...
            }
            @Override
            public void onCompleted() {
                finishLatch.countDown();
            }
        });
        streamObserver.onNext(request);
        streamObserver.onNext(request);
        streamObserver.onNext(request);
        streamObserver.onCompleted();
        finishLatch.await(10, TimeUnit.SECONDS);
        return response.toString();
    }

    @GetMapping("/bidirectionalStreaming")
    String bidirectionalStreaming() throws Exception {
        HelloRequest request = HelloRequest.newBuilder().setName("Tom").build();
        CountDownLatch finishLatch = new CountDownLatch(1);
        List<HelloReply> response = new ArrayList<>();
        StreamObserver<HelloRequest> streamObserver = stub().sayHelloBidirectionalStreaming(new StreamObserver<HelloReply>() {
            @Override
            public void onNext(HelloReply reply) {
                response.add(reply);
            }
            @Override
            public void onError(Throwable t) {
                // ...
            }
            @Override
            public void onCompleted() {
                finishLatch.countDown();
            }
        });
        streamObserver.onNext(request);
        streamObserver.onNext(request);
        streamObserver.onNext(request);
        streamObserver.onCompleted();
        finishLatch.await(10, TimeUnit.SECONDS);
        return response.toString();
    }


    private GreeterGrpc.GreeterBlockingStub blockingStub() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 6565)
                .usePlaintext(true)
                .build();
        return GreeterGrpc.newBlockingStub(channel);
    }

    private GreeterGrpc.GreeterStub stub() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 6565)
                .usePlaintext(true)
                .build();
        return GreeterGrpc.newStub(channel);
    }
}
