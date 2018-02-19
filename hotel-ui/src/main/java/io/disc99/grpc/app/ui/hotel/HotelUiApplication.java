package io.disc99.grpc.app.ui.hotel;

import io.disc99.grpc.apps.hotel.HotelServiceGrpc;
import io.disc99.grpc.apps.hotel.HotelSummaries;
import io.disc99.grpc.apps.room.RoomDetail;
import io.disc99.grpc.apps.room.RoomId;
import io.disc99.grpc.apps.room.RoomServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@SpringBootApplication
@Controller
public class HotelUiApplication {

    public static void main(String[] args) {
        SpringApplication.run(HotelUiApplication.class, args);
    }

    // TODO
    int hotelId = 1;

    @GetMapping("/")
    String index() {

        return "index";
    }

    @GetMapping("/hotel/{id}")
    String findBy(@PathVariable Integer id, Model model) {
        RoomId roomId = RoomId.newBuilder()
                .setValue(id)
                .setHotelId(hotelId)
                .build();
        RoomDetail detail = room().findBy(roomId);
        model.addAttribute("detail", detail);
        return "detail";
    }

    @GetMapping("/hotel/{id}/room/{roomId}")
    String findBy(@PathVariable Integer id, @PathVariable Integer roomId, Model model) {
        RoomId ri = RoomId.newBuilder()
                .setValue(roomId)
                .setHotelId(id)
                .build();
        RoomDetail detail = room().findBy(ri);
        model.addAttribute("detail", detail);
        return "detail";
    }

    @GetMapping("/hotel/search")
    String search(Model model) {
        io.disc99.grpc.apps.hotel.Criteria criteria = io.disc99.grpc.apps.hotel.Criteria.newBuilder().build();
        HotelSummaries summaries = hotel().search(criteria);
        model.addAttribute("summaries", summaries.getSummariesList());
        return "search";
    }

    RoomServiceGrpc.RoomServiceBlockingStub room() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 6566)
                .usePlaintext(true)
                .build();
        return RoomServiceGrpc.newBlockingStub(channel);
    }

    HotelServiceGrpc.HotelServiceBlockingStub hotel() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 6566)
                .usePlaintext(true)
                .build();
        return HotelServiceGrpc.newBlockingStub(channel);
    }
}
