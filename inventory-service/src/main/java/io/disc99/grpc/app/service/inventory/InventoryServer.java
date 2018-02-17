package io.disc99.grpc.app.service.inventory;

import com.google.protobuf.Empty;
import io.disc99.grpc.apps.inventory.*;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.util.HashMap;
import java.util.Map;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

public class InventoryServer {

    public static void main(String[] args) throws Exception {
        STORE.put(1, "Room 1");
        STORE.put(2, "Room 2");

        Server server = ServerBuilder.forPort(6565)
                .addService(new InventoryImpl())
                .build();

        server.start();

        server.awaitTermination();
    }

    static Map<Integer, String> STORE = new HashMap<>();

    static class InventoryImpl extends InventoryGrpc.InventoryImplBase {
        @Override
        public void add(Room request, StreamObserver<RoomId> responseObserver) {
            int id = STORE.size() + 1;
            STORE.put(id, request.getName());

            RoomId roomId = RoomId.newBuilder()
                    .setId(id)
                    .build();

            responseObserver.onNext(roomId);
            responseObserver.onCompleted();
        }

        @Override
        public void findBy(RoomId request, StreamObserver<RoomDetail> responseObserver) {
            int id = request.getId();

            RoomDetail room = RoomDetail.newBuilder()
                    .setId(id)
                    .setName(STORE.get(id))
                    .build();

            responseObserver.onNext(room);
            responseObserver.onCompleted();
        }

        @Override
        public void search(Empty request, StreamObserver<RoomSummaries> responseObserver) {
            RoomSummaries summaries = STORE.entrySet().stream()
                    .map(e -> RoomSummary.newBuilder()
                            .setId(e.getKey())
                            .setName(e.getValue())
                            .build())
                    .collect(collectingAndThen(toList(), s -> RoomSummaries.newBuilder().addAllSummaries(s).build()));

            responseObserver.onNext(summaries);
            responseObserver.onCompleted();
        }
    }
}
