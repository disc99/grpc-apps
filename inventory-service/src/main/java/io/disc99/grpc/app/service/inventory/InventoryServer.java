package io.disc99.grpc.app.service.inventory;

import io.disc99.grpc.apps.inventory.InventoryGrpc;
import io.disc99.grpc.apps.inventory.Room;
import io.disc99.grpc.apps.inventory.RoomDetail;
import io.disc99.grpc.apps.inventory.RoomId;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.util.HashMap;
import java.util.Map;

public class InventoryServer {

    public static void main(String[] args) throws Exception {
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
    }
}
