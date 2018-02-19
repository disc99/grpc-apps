package io.disc99.grpc.app.service.hotel;

import com.google.common.collect.ImmutableMap;
import io.disc99.grpc.apps.hotel.Criteria;
import io.disc99.grpc.apps.hotel.*;
import io.disc99.grpc.apps.room.*;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.util.HashMap;
import java.util.Map;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

public class HotelServer {

    public static void main(String[] args) throws Exception {
        HOTEL_STORE.put(1, "Hotel 1");
        HOTEL_STORE.put(2, "Hotel 2");

        ROOM_STORE.put(1, new HashMap<>());
        ROOM_STORE.put(2, ImmutableMap.of(1, "Room 1", 2, "Room 2"));

        Server server = ServerBuilder.forPort(6566)
                .addService(new HotelServiceImpl())
                .addService(new RoomServiceImpl())
                .build();

        server.start();

        server.awaitTermination();
    }

    static Map<Integer, String> HOTEL_STORE = new HashMap<>();
    static Map<Integer, Map<Integer, String>> ROOM_STORE = new HashMap<>();

    static class HotelServiceImpl extends HotelServiceGrpc.HotelServiceImplBase {
        @Override
        public void create(Hotel request, StreamObserver<HotelId> responseObserver) {
            int id = HOTEL_STORE.size() + 1;
            HOTEL_STORE.put(id, request.getName());

            HotelId roomId = HotelId.newBuilder()
                    .setValue(id)
                    .build();

            responseObserver.onNext(roomId);
            responseObserver.onCompleted();
        }

        @Override
        public void findBy(HotelId request, StreamObserver<HotelDetail> responseObserver) {
            int id = request.getValue();

            HotelDetail room = HotelDetail.newBuilder()
                    .setId(id)
                    .setName(HOTEL_STORE.get(id))
                    .build();

            responseObserver.onNext(room);
            responseObserver.onCompleted();
        }

        @Override
        public void search(Criteria request, StreamObserver<HotelSummaries> responseObserver) {
            HotelSummaries summaries = HOTEL_STORE.entrySet().stream()
                    .map(e -> HotelSummary.newBuilder()
                            .setId(e.getKey())
                            .setName(e.getValue())
                            .build())
                    .collect(collectingAndThen(toList(), s -> HotelSummaries.newBuilder().addAllSummaries(s).build()));

            responseObserver.onNext(summaries);
            responseObserver.onCompleted();
        }
    }

    static class RoomServiceImpl extends RoomServiceGrpc.RoomServiceImplBase {
        @Override
        public void add(Room request, StreamObserver<RoomId> responseObserver) {
            int hotelId = request.getHotelId();
            Map<Integer, String> rooms = ROOM_STORE.get(hotelId);
            int id = rooms.size() + 1;
            rooms.put(id, request.getName());
            ROOM_STORE.put(hotelId, rooms);

            RoomId roomId = RoomId.newBuilder()
                    .setValue(id)
                    .setHotelId(request.getHotelId())
                    .build();

            responseObserver.onNext(roomId);
            responseObserver.onCompleted();
        }

        @Override
        public void findBy(RoomId request, StreamObserver<RoomDetail> responseObserver) {
            int id = request.getValue();
            int hotelId = request.getHotelId();

            RoomDetail room = RoomDetail.newBuilder()
                    .setId(id)
                    .setName(ROOM_STORE.get(hotelId).get(id))
                    .setId(hotelId)
                    .build();

            responseObserver.onNext(room);
            responseObserver.onCompleted();
        }

        @Override
        public void search(io.disc99.grpc.apps.room.Criteria request, StreamObserver<RoomSummaries> responseObserver) {
            Map<Integer, String> rooms = ROOM_STORE.get(request.getHotelId());
            RoomSummaries summaries = rooms.entrySet().stream()
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
