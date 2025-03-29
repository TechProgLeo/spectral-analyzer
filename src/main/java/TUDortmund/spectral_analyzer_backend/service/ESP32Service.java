package TUDortmund.spectral_analyzer_backend.service;

import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.net.Socket;

@Service
public class ESP32Service {
    private static final String ESP_IP = "192.168.43.226"; // ESP32's IP
    private static final int ESP_PORT = 8080; // ESP32's Port

    public String sendCommand(String command) {
        try (Socket socket = new Socket(ESP_IP, ESP_PORT);
             OutputStream outputStream = socket.getOutputStream()) {

            command += "\r\n"; // Ensure proper command termination
            outputStream.write(command.getBytes());
            outputStream.flush();

            return "Sent command: " + command.trim();
        } catch (Exception e) {
            return "Failed to send command: " + e.getMessage();
        }
    }
}
