package TUDortmund.spectral_analyzer_backend.controller;

import TUDortmund.spectral_analyzer_backend.service.SpectralDataService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/spectral")
public class SpectralDataController {

    private final SpectralDataService spectralDataService;

    public SpectralDataController(SpectralDataService spectralDataService) {
        this.spectralDataService = spectralDataService;
    }

    @GetMapping("/data")
    public List<Map<String, Object>> getSpectralData() {
        return spectralDataService.processSpectralData();
    }
}
