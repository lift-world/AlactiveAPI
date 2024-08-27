package app.alactive.api.fetchers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HealthController {
    @GetMapping("/health")
    fun health(): String = "All Izz Well"
}
