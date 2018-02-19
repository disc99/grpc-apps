//package io.disc99.grpc.app.service.inventory;
//
//import com.google.common.collect.ImmutableMap;
//import io.disc99.grpc.apps.inventory.*;
//import io.grpc.Server;
//import io.grpc.ServerBuilder;
//import io.grpc.stub.StreamObserver;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import static java.util.stream.Collectors.collectingAndThen;
//import static java.util.stream.Collectors.toList;
//
//public class InventoryServer {
//
//    public static void main(String[] args) throws Exception {
//        STORE.put(1, new HashMap<>());
//        STORE.put(2, ImmutableMap.of(1, "Room 1", 2, "Room 2"));
//
//        Server server = ServerBuilder.forPort(6565)
//                .addService(new InventoryServiceImpl())
//                .build();
//
//        server.start();
//
//        server.awaitTermination();
//    }
//
//    static Map<Integer, Map<Integer, String>> STORE = new HashMap<>();
//
//    static class InventoryServiceImpl extends InventoryServiceGrpc.InventoryServiceImplBase {
//        @Override
//        public void add(Room request, StreamObserver<RoomId> responseObserver) {
//            int hotelId = request.getHotelId();
//            Map<Integer, String> rooms = STORE.get(hotelId);
//            int id = rooms.size() + 1;
//            rooms.put(id, request.getName());
//            STORE.put(hotelId, rooms);
//
//            RoomId roomId = RoomId.newBuilder()
//                    .setId(id)
//                    .build();
//
//            responseObserver.onNext(roomId);
//            responseObserver.onCompleted();
//        }
//
//        @Override
//        public void findBy(RoomId request, StreamObserver<RoomDetail> responseObserver) {
//            int id = request.getId();
//            int hotelId = request.getHotelId();
//
//            RoomDetail room = RoomDetail.newBuilder()
//                    .setId(id)
//                    .setName(STORE.get(hotelId).get(id))
//                    .setHotelId(hotelId)
//                    .build();
//
//            responseObserver.onNext(room);
//            responseObserver.onCompleted();
//        }
//
//        @Override
//        public void search(Criteria request, StreamObserver<RoomSummaries> responseObserver) {
//            Map<Integer, String> rooms = STORE.get(request.getHotelId());
//            RoomSummaries summaries = rooms.entrySet().stream()
//                    .map(e -> RoomSummary.newBuilder()
//                            .setId(e.getKey())
//                            .setName(e.getValue())
//                            .build())
//                    .collect(collectingAndThen(toList(), s -> RoomSummaries.newBuilder().addAllSummaries(s).build()));
//
//            responseObserver.onNext(summaries);
//            responseObserver.onCompleted();
//        }
//    }
//}
