package mutsa.delivery.controller;

import mutsa.delivery.global.apiPayload.GlobalResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    @GetMapping
    public GlobalResponse<String> health() {
        return GlobalResponse.onSuccess("OK");
    }
}
