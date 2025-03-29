package TUDortmund.spectral_analyzer_backend.controller;

import TUDortmund.spectral_analyzer_backend.service.ESP32Service;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/esp32")
@CrossOrigin(origins = "http://localhost:3001") // Allow frontend requests
public class ESP32Controller {

    private final ESP32Service esp32Service;

    public ESP32Controller(ESP32Service esp32Service) {
        this.esp32Service = esp32Service;
    }

    @PostMapping("/command")
    public String sendESPCommand(@RequestBody Map<String, String> request) {
        String command = request.get("command");
        if (command == null || command.isEmpty()) {
            return "Error: No command provided!";
        }
        return esp32Service.sendCommand(command);
    }
}
