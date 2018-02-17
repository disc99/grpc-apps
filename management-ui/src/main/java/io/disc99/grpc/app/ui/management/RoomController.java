package io.disc99.grpc.app.ui.management;

import com.google.protobuf.Empty;
import io.disc99.grpc.apps.inventory.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/room")
class RoomController {

    @GetMapping("/")
    String list(Model model) {
        Empty empty = Empty.newBuilder().build();
        RoomSummaries summaries = stub().search(empty);
        model.addAttribute("summaries", summaries.getSummariesList());
        return "room/list";
    }

    @GetMapping("/add")
    String add(Model model) {
        model.addAttribute("form", new RoomForm());

        return "room/add";
    }

    @GetMapping("/{id}")
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

    @PostMapping("/")
    String add(@ModelAttribute RoomForm form) {
        Room room = Room.newBuilder()
                .setName(form.getName())
                .build();
        stub().add(room);
        return "redirect:/room/";
    }

    InventoryServiceGrpc.InventoryServiceBlockingStub stub() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 6565)
                .usePlaintext(true)
                .build();
        return InventoryServiceGrpc.newBlockingStub(channel);
    }
}
