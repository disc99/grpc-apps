package com.example.service.backend;

import com.example.service.backend.BackendServiceApplication.GreeterService;
import io.grpc.examples.helloworld.GreeterGrpc;
import io.grpc.examples.helloworld.HelloReply;
import io.grpc.examples.helloworld.HelloRequest;
import io.grpc.testing.GrpcServerRule;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class BackendServiceApplicationTest {
    @Rule
    public GrpcServerRule grpcServerRule = new GrpcServerRule().directExecutor();

    @Test
    public void test() {
        grpcServerRule.getServiceRegistry().addService(new GreeterService());
        GreeterGrpc.GreeterBlockingStub blockingStub = GreeterGrpc.newBlockingStub(grpcServerRule.getChannel());

        String testName = "test name";
        HelloReply reply = blockingStub.sayHello(HelloRequest.newBuilder().setName(testName).build());

        assertEquals("Hello " + testName, reply.getMessage());
    }
}