package io.disc99.grpc.app.uaa;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UaaController {
    @GetMapping("/login")
    String login() {
        return "login";
    }
}

