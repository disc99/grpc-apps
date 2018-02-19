package io.disc99.grpc.app.ui.management;

import io.disc99.grpc.apps.room.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/room")
class RoomController {
    // TODO
    int hotelId = 1;

    @GetMapping("/")
    String list(Model model) {
        Criteria criteria = Criteria.newBuilder().setHotelId(hotelId).build();
        RoomSummaries summaries = stub().search(criteria);
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
                .setValue(id)
                .setHotelId(hotelId)
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
                .setHotelId(hotelId)
                .setName(form.getName())
                .build();
        stub().add(room);
        return "redirect:/room/";
    }

    @PostMapping("/{id}")
    String update(@PathVariable Integer id, Model model) {
        RoomId roomId = RoomId.newBuilder()
                .setValue(id)
                .setHotelId(hotelId)
                .build();
        RoomDetail detail = stub().findBy(roomId);
        model.addAttribute("detail", detail);
        RoomForm form = new RoomForm();
        form.setName(detail.getName());
        model.addAttribute("form", form);
        return "room/edit";
    }


    RoomServiceGrpc.RoomServiceBlockingStub stub() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 6566)
                .usePlaintext(true)
                .build();
        return RoomServiceGrpc.newBlockingStub(channel);
    }
}
