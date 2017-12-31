package com.example.service.backend;

import com.google.rpc.DebugInfo;
import io.grpc.*;
import io.grpc.examples.helloworld.Error;
import io.grpc.examples.helloworld.GreeterGrpc;
import io.grpc.examples.helloworld.HelloReply;
import io.grpc.examples.helloworld.HelloRequest;
import io.grpc.protobuf.ProtoUtils;
import io.grpc.stub.StreamObserver;
import io.grpc.util.TransmitStatusRuntimeExceptionInterceptor;
import org.lognet.springboot.grpc.GRpcGlobalInterceptor;
import org.lognet.springboot.grpc.GRpcService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BackendServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendServiceApplication.class, args);
	}

	@Bean
	@GRpcGlobalInterceptor
	ServerInterceptor transmitStatusRuntimeExceptionInterceptor() {
		return TransmitStatusRuntimeExceptionInterceptor.instance();
	}

	@GRpcService
	public static class GreeterService extends GreeterGrpc.GreeterImplBase {
		@Value("${grpc.port}")
		int gport;

        @Override
        public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
            HelloReply reply = HelloReply.newBuilder().setMessage("Hello " + request.getName() + " grpc.port=" + gport).build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }

		@Override
		public void sayHelloUnary(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
            HelloReply reply = HelloReply.newBuilder().setMessage("Hello " + request.getName() + " grpc.port=" + gport).build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
		}

		@Override
		public void sayError(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
			Error error = Error.newBuilder()
					.setMessage("my error")
					.setDetail("error detail")
					.build();
			Metadata metadata = new Metadata();
			Metadata.Key<Error> key = ProtoUtils.keyForProto(error);
			metadata.put(key, error);

			throw Status.INTERNAL
					.withDescription("server error")
					.asRuntimeException(metadata);
		}

		@Override
		public void sayHelloServerStreaming(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
			HelloReply reply = HelloReply.newBuilder().setMessage("Hello " + request.getName()).build();
			responseObserver.onNext(reply);
			responseObserver.onNext(reply);
			responseObserver.onNext(reply);
			responseObserver.onCompleted();
		}

		@Override
		public StreamObserver<HelloRequest> sayHelloClientStreaming(StreamObserver<HelloReply> responseObserver) {
			return new StreamObserver<HelloRequest>() {
				@Override
				public void onNext(HelloRequest request) {
					HelloReply reply = HelloReply.newBuilder().setMessage("Hello " + request.getName()).build();
					responseObserver.onNext(reply);
					responseObserver.onCompleted();
				}
				@Override
				public void onError(Throwable t) {
					// ...
				}
				@Override
				public void onCompleted() {
					// ...
				}
			};
		}

		@Override
		public StreamObserver<HelloRequest> sayHelloBidirectionalStreaming(StreamObserver<HelloReply> responseObserver) {
			return new StreamObserver<HelloRequest>() {
				@Override
				public void onNext(HelloRequest request) {
					HelloReply reply = HelloReply.newBuilder().setMessage("Hello " + request.getName()).build();
					responseObserver.onNext(reply);
					responseObserver.onCompleted();
				}
				@Override
				public void onError(Throwable t) {
					// ...
				}
				@Override
				public void onCompleted() {
					// ...
				}
			};
		}
	}

}
