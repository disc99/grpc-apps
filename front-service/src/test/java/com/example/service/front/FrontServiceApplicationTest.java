package com.example.service.front;

import io.grpc.ManagedChannel;
import io.grpc.examples.helloworld.GreeterGrpc;
import io.grpc.examples.helloworld.HelloReply;
import io.grpc.examples.helloworld.HelloRequest;
import io.grpc.stub.StreamObserver;
import io.grpc.testing.GrpcServerRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mockito;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class FrontServiceApplicationTest {
    @Rule
    public GrpcServerRule grpcServerRule = new GrpcServerRule().directExecutor();

    @Test
    public void test() {
        GreeterGrpc.GreeterImplBase serviceImpl = Mockito.spy(new GreeterGrpc.GreeterImplBase() {});
        grpcServerRule.getServiceRegistry().addService(serviceImpl);

        ArgumentCaptor<HelloRequest> requestCaptor = ArgumentCaptor.forClass(HelloRequest.class);
        String testName = "test name";

        DemoClient client = new DemoClient(grpcServerRule.getChannel());;
        client.hello(testName);

        Mockito.verify(serviceImpl)
                .sayHello(requestCaptor.capture(), Matchers.any());
        assertEquals(testName, requestCaptor.getValue().getName());
    }


    static class DemoClient {
        public DemoClient(ManagedChannel channel) {
            this.channel = channel;
        }

        ManagedChannel channel;

        public void hello(String name) {
            HelloRequest request = HelloRequest.newBuilder().setName(name).build();
            GreeterGrpc.newStub(channel).sayHello(request, new StreamObserver<HelloReply>() {
                @Override
                public void onNext(HelloReply value) {

                }

                @Override
                public void onError(Throwable t) {

                }

                @Override
                public void onCompleted() {

                }
            });
        }
    }
}
