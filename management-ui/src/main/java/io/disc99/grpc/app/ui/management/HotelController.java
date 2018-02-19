package io.disc99.grpc.app.ui.management;

import io.disc99.grpc.apps.hotel.Hotel;
import io.disc99.grpc.apps.hotel.HotelDetail;
import io.disc99.grpc.apps.hotel.HotelId;
import io.disc99.grpc.apps.hotel.HotelServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/hotel")
class HotelController {

    // TODO
    int hotelId = 1;

    @GetMapping("/edit")
    String edit(Model model) {
        HotelId id = HotelId.newBuilder()
                .setValue(hotelId)
                .build();
        HotelDetail detail = stub().findBy(id);
        HotelForm form = new HotelForm();
        form.setName(detail.getName());
        model.addAttribute("form", form);
        model.addAttribute("detail", detail);
        return "hotel/edit";
    }

    @PostMapping("/")
    String update(@ModelAttribute HotelForm form) {
        Hotel hotel = Hotel.newBuilder()
                .setName(form.getName())
                .build();
        stub().create(hotel);
        return "redirect:/index";
    }

    HotelServiceGrpc.HotelServiceBlockingStub stub() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 6566)
                .usePlaintext(true)
                .build();
        return HotelServiceGrpc.newBlockingStub(channel);
    }
}
