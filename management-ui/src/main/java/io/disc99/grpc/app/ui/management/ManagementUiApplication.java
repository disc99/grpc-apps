package io.disc99.grpc.app.ui.management;

import com.google.protobuf.Empty;
import io.disc99.grpc.apps.inventory.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;


@SpringBootApplication
@Controller
public class ManagementUiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ManagementUiApplication.class, args);
    }

    @GetMapping("/")
    String index() {
        return "index";
    }

    @GetMapping("/room/")
    String list(Model model) {
        Empty empty = Empty.newBuilder().build();
        RoomSummaries summaries = stub().search(empty);
        model.addAttribute("summaries", summaries.getSummariesList());
        return "room/list";
    }

    @GetMapping("/room/add")
    String add(Model model) {
        model.addAttribute("form", new RoomForm());

        return "room/add";
    }

    @GetMapping("/room/{id}")
    String findBy(@PathVariable Integer id, Model model) {
        RoomId roomId = RoomId.newBuilder()
                .setId(id)
                .build();
        RoomDetail detail = stub().findBy(roomId);
        model.addAttribute("detail", detail);
        RoomForm form = new RoomForm();
        form.setName(detail.getName());
        model.addAttribute("form", form);
        return "room/edit";
    }

    @PostMapping("/room/")
    String add(@ModelAttribute RoomForm form) {
        Room room = Room.newBuilder()
                .setName(form.getName())
                .build();
        stub().add(room);
        return "redirect:/room/";
    }

    InventoryGrpc.InventoryBlockingStub stub() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 6565)
                .usePlaintext(true)
                .build();
        return InventoryGrpc.newBlockingStub(channel);
    }
}

