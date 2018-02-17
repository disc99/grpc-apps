package io.disc99.grpc.app.ui.management;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
class RootController {
    @GetMapping("/")
    String index() {
        return "index";
    }
}
