package io.disc99.grpc.app.ui.hotel;

import com.google.protobuf.Empty;
import io.disc99.grpc.apps.inventory.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


@SpringBootApplication
@Controller
public class HotelUiApplication {

    public static void main(String[] args) {
        SpringApplication.run(HotelUiApplication.class, args);
    }

    @GetMapping("/")
    String index() {

        return "index";
    }

    @GetMapping("/hotel/{id}")
    String findBy(@PathVariable Integer id, Model model) {
        RoomId roomId = RoomId.newBuilder()
                .setId(id)
                .build();
        RoomDetail detail = stub().findBy(roomId);
        model.addAttribute("detail", detail);
        return "detail";
    }

    @GetMapping("/hotel/search")
    String search(Model model) {
        Empty empty = Empty.newBuilder().build();
        RoomSummaries summaries = stub().search(empty);
        model.addAttribute("summaries", summaries.getSummariesList());
        return "search";
    }

    InventoryServiceGrpc.InventoryServiceBlockingStub stub() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 6565)
                .usePlaintext(true)
                .build();
        return InventoryServiceGrpc.newBlockingStub(channel);
    }
}
