package com.example.service.backend;

import com.google.rpc.DebugInfo;
import io.grpc.*;
import io.grpc.examples.helloworld.GreeterGrpc;
import io.grpc.examples.helloworld.HelloReply;
import io.grpc.examples.helloworld.HelloRequest;
import io.grpc.protobuf.ProtoUtils;
import io.grpc.stub.StreamObserver;
import org.lognet.springboot.grpc.GRpcService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendServiceApplication.class, args);
	}

	@GRpcService
	public static class GreeterService extends  GreeterGrpc.GreeterImplBase {
		@Value("${grpc.port}")
		int gport;

		@Override
		public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
			HelloReply reply = HelloReply.newBuilder().setMessage("Hello " + request.getName()).build();
			responseObserver.onNext(reply);
			responseObserver.onCompleted();
		}

		@Override
		public void sayHelloUnary(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
//			Metadata metadata = new Metadata();
//			Metadata.Key<DebugInfo> key = ProtoUtils.keyForProto(DebugInfo.getDefaultInstance());
//			DebugInfo values = DebugInfo.newBuilder()
//					.addStackEntries("stack_entry_1")
//					.addStackEntries("stack_entry_2")
//					.addStackEntries("stack_entry_3")
//					.setDetail("detailed error info.").build();
//			metadata.put(key, values);
//			StatusRuntimeException error = Status.INVALID_ARGUMENT
//					.withDescription("ERROR DESC")
//					.asRuntimeException(metadata);
//			throw error;
//            responseObserver.onError(error);

            HelloReply reply = HelloReply.newBuilder().setMessage("Hello " + request.getName()
//                    + " server.port="+port
                    + " grpc.port=" + gport).build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
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
					responseObserver.onNext(reply);
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
					System.out.println("Call onNext");
					HelloReply reply = HelloReply.newBuilder().setMessage("Hello " + request.getName()).build();
					responseObserver.onNext(reply);
					responseObserver.onNext(reply);
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
