package io.disc99.grpc.app.service.hotel;

import com.google.protobuf.Empty;
import io.disc99.grpc.apps.hotel.*;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.util.HashMap;
import java.util.Map;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

public class HotelServer {

    public static void main(String[] args) throws Exception {
        STORE.put(1, "Hotel 1");
        STORE.put(2, "Hotel 2");

        Server server = ServerBuilder.forPort(6566)
                .addService(new HotelServiceImpl())
                .build();

        server.start();

        server.awaitTermination();
    }

    static Map<Integer, String> STORE = new HashMap<>();

    static class HotelServiceImpl extends HotelServiceGrpc.HotelServiceImplBase {
        @Override
        public void add(Hotel request, StreamObserver<HotelId> responseObserver) {
            int id = STORE.size() + 1;
            STORE.put(id, request.getName());

            HotelId roomId = HotelId.newBuilder()
                    .setId(id)
                    .build();

            responseObserver.onNext(roomId);
            responseObserver.onCompleted();
        }

        @Override
        public void findBy(HotelId request, StreamObserver<HotelDetail> responseObserver) {
            int id = request.getId();

            HotelDetail room = HotelDetail.newBuilder()
                    .setId(id)
                    .setName(STORE.get(id))
                    .build();

            responseObserver.onNext(room);
            responseObserver.onCompleted();
        }

        @Override
        public void search(Empty request, StreamObserver<HotelSummaries> responseObserver) {
            HotelSummaries summaries = STORE.entrySet().stream()
                    .map(e -> HotelSummary.newBuilder()
                            .setId(e.getKey())
                            .setName(e.getValue())
                            .build())
                    .collect(collectingAndThen(toList(), s -> HotelSummaries.newBuilder().addAllSummaries(s).build()));

            responseObserver.onNext(summaries);
            responseObserver.onCompleted();
        }
    }
}
