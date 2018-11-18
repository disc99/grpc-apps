package com.example.service.backend;

import com.google.protobuf.Any;
import com.google.rpc.BadRequest;
import com.google.rpc.Status;
import io.grpc.ServerInterceptor;
import io.grpc.StatusRuntimeException;
import io.grpc.examples.helloworld.GreeterGrpc;
import io.grpc.examples.helloworld.HelloReply;
import io.grpc.examples.helloworld.HelloRequest;
import io.grpc.protobuf.StatusProto;
import io.grpc.stub.StreamObserver;
import io.grpc.util.TransmitStatusRuntimeExceptionInterceptor;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.lognet.springboot.grpc.GRpcGlobalInterceptor;
import org.lognet.springboot.grpc.GRpcService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.rpc.Code.INTERNAL_VALUE;
import static com.google.rpc.Code.INVALID_ARGUMENT_VALUE;
import static com.google.rpc.Code.NOT_FOUND_VALUE;

@SpringBootApplication
public class BackendServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendServiceApplication.class, args);
	}

	@GRpcService
	static class GreeterService extends GreeterGrpc.GreeterImplBase {

		GreeterLogic logic;

		GreeterService(GreeterLogic logic) {
			this.logic = logic;
		}

		@Override
		public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
			HelloReply reply = logic.findBy(request);

			responseObserver.onNext(reply);
			responseObserver.onCompleted();
		}
	}

	@Service
	static class GreeterLogic {
        public HelloReply findBy(HelloRequest request) {
            String name = request.getName();

            if ("400".equals(name)) {
                throw new InvalidException("Invalid!!",
						Arrays.asList("Require name", "Only over 20"));
            }

            if ("404".equals(name)) {
                throw new ResourceNotFoundException("Not found!!");
            }

            if ("500".equals(name)) {
                throw new InternalServerError("Internal error!!");
            }

            if ("native".equals(name)) {
                throw new RuntimeException("Native exception!!");
            }

            return HelloReply.newBuilder()
                    .setMessage("Hello " + request.getName())
                    .setDescription("Desc: " + request.getNickname())
                    .build();
        }
    }

	static class InternalServerError extends RuntimeException {
        InternalServerError(String message) {
            super(message);
        }
    }

	static class InvalidException extends RuntimeException {
		List<String> violations;
        InvalidException(String message, List<String> violations) {
            super(message);
            this.violations = violations;
        }
    }

	static class ResourceNotFoundException extends RuntimeException {
        ResourceNotFoundException(String message) {
            super(message);
        }
    }

	@Aspect
    @Component
    static class ApplicationAdvice {

        @AfterThrowing(value="execution(* com.example.service.backend..*.*(..))", throwing="e")
        public void handle(RuntimeException e) {
            String message = e.getMessage();
            String errorMessage = message == null ? e.toString() : message;

            if (e instanceof ResourceNotFoundException) {
                throw toStatusRuntimeException(NOT_FOUND_VALUE, errorMessage, null);
            }

            if (e instanceof InvalidException) {
                BadRequest badRequest = ((InvalidException) e).violations.stream()
                        .map(this::toFieldViolation)
                        .collect(Collectors.collectingAndThen(Collectors.toList(), this::toBadRequest));
                throw toStatusRuntimeException(INVALID_ARGUMENT_VALUE, errorMessage, badRequest);
            }

            if (e instanceof InternalServerError) {
                throw toStatusRuntimeException(INTERNAL_VALUE, errorMessage, null);
            }

            throw toStatusRuntimeException(INTERNAL_VALUE, errorMessage, null);
        }

        private BadRequest toBadRequest(List<BadRequest.FieldViolation> violations) {
            return BadRequest.newBuilder().addAllFieldViolations(violations).build();
        }

        private BadRequest.FieldViolation toFieldViolation(String violation) {
            return BadRequest.FieldViolation.newBuilder().setDescription(violation).build();
        }

        private <T extends com.google.protobuf.Message> StatusRuntimeException toStatusRuntimeException(int code, String message, T details) {
            Status.Builder builder = Status.newBuilder()
                    .setCode(code)
                    .setMessage(message);

            if (details != null) {
                builder.addDetails(Any.pack(details));
            }

            Status status = builder.build();
            return StatusProto.toStatusRuntimeException(status);
        }
    }

	@Bean
	@GRpcGlobalInterceptor
	ServerInterceptor transmitStatusRuntimeExceptionInterceptor() {
		return TransmitStatusRuntimeExceptionInterceptor.instance();
	}
}

