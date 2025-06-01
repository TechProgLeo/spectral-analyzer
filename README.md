🌈 Spectral Analyzer Backend (Spring Boot)
This is the backend service for the ESP32-based Spectral Analyzer System, built using Spring Boot and integrated with InfluxDB for time-series data storage and processing.

It serves as the communication bridge between the frontend (React.js) and the ESP32-S3 device, handling real-time command dispatching and processing of spectral data.

📁 Project Structure
graphql
Kopieren
Bearbeiten
src/
├── config/                 # Configuration classes (CORS, InfluxDB)
├── controller/             # REST controllers for ESP32 & Spectral Data
├── service/                # Business logic for sending commands & processing data
└── resources/
    └── application.properties
🔧 Requirements
Java 17+

Maven 3.6+

Spring Boot 3.x

InfluxDB 2.x

ESP32-S3 device running firmware (see ESP32 Firmware Repo)

React Frontend (running on http://localhost:3001)

⚙️ Configuration
Set these values in your application.properties:

properties
Kopieren
Bearbeiten
influxdb.url=http://192.168.43.146:8086
influxdb.token=YOUR_INFLUXDB_TOKEN
influxdb.org=d8481950cdbfec67
influxdb.bucket=From_ESP32S3
Make sure your ESP32 has a static IP (default is 192.168.43.226) and is reachable from the machine running this backend.

🧠 Features
✅ Command API
Sends a command to the ESP32 via TCP.

Endpoint: POST /api/esp32/command

Payload:

json
Kopieren
Bearbeiten
{
  "command": "s"
}
Supported commands:

s: Start measurement cycle

p: Measure reflectance nominator (user-placed leaf)

f: Reset to ready state

📊 Spectral Data API
Returns calculated reflectance and transcendence values per wavelength (from 410nm to 940nm) based on measurements from InfluxDB.

Endpoint: GET /api/spectral/data

Response:

json
Kopieren
Bearbeiten
[
  {
    "wavelength": 410.0,
    "reflectance": 58.3,
    "transcendence": 17.1
  },
  ...
]
The values are normalized (%) using pre-defined denominators and corrected for dark values.

🔐 CORS Policy
The application allows requests from http://localhost:3001 (React frontend):

java
Kopieren
Bearbeiten
registry.addMapping("/api/**")
        .allowedOrigins("http://localhost:3001")
        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
        .allowCredentials(true);
🛰️ How It Works
Frontend sends a command to /api/esp32/command.

Spring Boot Backend establishes a TCP socket to ESP32 and sends the command.

ESP32 executes a measurement routine and sends sensor data to InfluxDB.

Frontend queries /api/spectral/data to visualize reflectance & transcendence.

🧪 Example Workflow
Send "s" to initiate baseline measurements.

Wait for ESP32 to change state (to WAIT_FOR_LEAF).

Send "p" after placing the leaf to collect reflectance.

After measurement, fetch results from /api/spectral/data.

🚀 Run Locally
bash
Kopieren
Bearbeiten
# Build and run
mvn spring-boot:run
📡 Dependencies
Spring Web

InfluxDB Java Client

Lombok (optional)

📂 Related Repositories
🔌 ESP32 Firmware (C++)

💻 React Frontend (Coming Soon)

🛠️ TODOs
 Add authentication

 WebSocket support for real-time updates

 Extend support for additional sensor types

👨‍💻 Maintainers
Lasha (TU Dortmund) – Embedded, Backend, and Data Processing

📝 License
MIT License – see LICENSE.md for details.
