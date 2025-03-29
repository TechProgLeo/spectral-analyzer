package TUDortmund.spectral_analyzer_backend.service;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.QueryApi;
import com.influxdb.query.FluxTable;
import com.influxdb.query.FluxRecord;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SpectralDataService {
    private static final String URL = "http://localhost:8086";
    private static final String TOKEN = "DpDSv3TBlXvO4CnuvRMEWv_wRqhs372Zwth1AtquQpO7MUaSnUEk9kzpgS3jwkdcuwuQ82STrRcC-glVjTDwXQ==";
    private static final String ORG = "d8481950cdbfec67";
    private static final String BUCKET = "From_ESP32S3";

    private final InfluxDBClient client;

    public SpectralDataService() {
        this.client = InfluxDBClientFactory.create(URL, TOKEN.toCharArray(), ORG, BUCKET);
    }

    private static final Map<String, Double> REFLECTANCE_DENOMINATORS = Map.ofEntries(
            Map.entry("410nm", 5723.51),
            Map.entry("435nm", 1184.32),
            Map.entry("460nm", 3616.86),
            Map.entry("485nm", 703.64),
            Map.entry("510nm", 1411.50),
            Map.entry("535nm", 1416.80),
            Map.entry("560nm", 190.34),
            Map.entry("585nm", 240.29),
            Map.entry("610nm", 1823.46),
            Map.entry("645nm", 403.14),
            Map.entry("680nm", 1141.23),
            Map.entry("705nm", 77.06),
            Map.entry("730nm", 212.79),
            Map.entry("760nm", 395.14),
            Map.entry("810nm", 469.74),
            Map.entry("860nm", 1108.08),
            Map.entry("900nm", 95.09),
            Map.entry("940nm", 28.98)
    );

    public List<Map<String, Object>> processSpectralData() {
        List<String> wavelengthsStr = new ArrayList<>(REFLECTANCE_DENOMINATORS.keySet());
        List<Map<String, Object>> results = new ArrayList<>();

        for (String wavelengthStr : wavelengthsStr) {
            double wavelength = parseWavelength(wavelengthStr);
            double rd = REFLECTANCE_DENOMINATORS.getOrDefault(wavelengthStr, 1.0);
            double rd_dark = fetchLastValue(wavelengthStr, "Reflectance_Denominator_Dark");
            double rn = fetchLastValue(wavelengthStr, "Reflectance_Nominator");
            double rn_dark = fetchLastValue(wavelengthStr, "Reflectance_Nominator_Dark");
            double td = fetchLastValue(wavelengthStr, "Transcendence_Denominator");
            double td_dark = fetchLastValue(wavelengthStr, "Transcendence_Denominator_Dark");
            double tn = fetchLastValue(wavelengthStr, "Transcendence_Nominator");
            double tn_dark = fetchLastValue(wavelengthStr, "Transcendence_Nominator_Dark");

            double reflectance = Math.max(0.0, Math.min(1.0, (rn - rn_dark) / Math.max((rd - rd_dark), 1e-6)));
            double transcendence = Math.max(0.0, Math.min(1.0, (tn - tn_dark) / Math.max((td - td_dark), 1e-6)));

            Map<String, Object> result = new HashMap<>();
            result.put("wavelength", wavelength);
            result.put("reflectance", reflectance * 100);
            result.put("transcendence", transcendence * 100);
            results.add(result);
        }
        return results;
    }

    private List<FluxTable> queryInfluxDB(String field, String measurementType) {
        String fluxQuery = String.format("""
        from(bucket: "%s")
          |> range(start: -1h)
          |> filter(fn: (r) => r["_measurement"] == "spectral_data")
          |> filter(fn: (r) => r["_field"] == "%s")
          |> filter(fn: (r) => r["measurement_type"] == "%s")
          |> sort(columns: ["_time"], desc: true)
          |> limit(n: 1)
        """, BUCKET, field, measurementType);

        QueryApi queryApi = client.getQueryApi();
        return queryApi.query(fluxQuery);
    }

    private double fetchLastValue(String field, String measurementType) {
        List<FluxTable> tables = queryInfluxDB(field, measurementType);

        for (FluxTable table : tables) {
            for (FluxRecord record : table.getRecords()) {
                Object value = record.getValue();
                if (value instanceof Number) {
                    return ((Number) value).doubleValue();
                }
            }
        }
        return 0.0; // Default if no valid data is found
    }



    private double parseWavelength(String wavelengthStr) {
        Pattern pattern = Pattern.compile("(\\d+)nm");
        Matcher matcher = pattern.matcher(wavelengthStr);
        return matcher.find() ? Double.parseDouble(matcher.group(1)) : 0.0;
    }
}
