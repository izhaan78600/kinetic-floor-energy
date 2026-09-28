package com.metro.kinetic.controller;

import com.metro.kinetic.model.StationSector;
import com.metro.kinetic.model.Telemetry;
import com.metro.kinetic.service.KineticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/kinetic")
@CrossOrigin(origins = "*")
public class KineticController {

    @Autowired
    private KineticService kineticService;

    // ==========================================
    // STATION SECTORS
    // ==========================================
    @PostMapping("/sectors")
    public ResponseEntity<?> createSector(@RequestBody StationSector sector) {
        try {
            if (sector.getStationName() == null || sector.getSectorName() == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Station name and sector name are required"));
            }
            StationSector saved = kineticService.addSector(sector);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/sectors")
    public ResponseEntity<List<StationSector>> getSectors() {
        return ResponseEntity.ok(kineticService.getAllSectors());
    }

    @DeleteMapping("/sectors/{id}")
    public ResponseEntity<?> deleteSector(@PathVariable Long id) {
        kineticService.deleteSector(id);
        return ResponseEntity.ok(Map.of("message", "Sector deleted successfully"));
    }

    // ==========================================
    // TELEMETRY
    // ==========================================
    @PostMapping("/telemetry")
    public ResponseEntity<?> recordTelemetry(@RequestBody Map<String, Object> payload) {
        try {
            Long sectorId = Long.valueOf(payload.get("sectorId").toString());
            Long stepCount = Long.valueOf(payload.get("stepCount").toString());

            LocalDate date = LocalDate.now();
            if (payload.get("date") != null && !payload.get("date").toString().isEmpty()) {
                date = LocalDate.parse(payload.get("date").toString());
            }

            Double energyKwh = null;
            if (payload.get("energyKwh") != null && !payload.get("energyKwh").toString().isEmpty()) {
                energyKwh = Double.valueOf(payload.get("energyKwh").toString());
            }

            Telemetry saved = kineticService.recordTelemetry(sectorId, date, stepCount, energyKwh);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/telemetry")
    public ResponseEntity<List<Telemetry>> getAllTelemetry() {
        return ResponseEntity.ok(kineticService.getAllTelemetry());
    }

    @PostMapping("/telemetry/simulate")
    public ResponseEntity<?> simulateFootstepBurst() {
        try {
            Telemetry simulated = kineticService.simulateFootstepBurst();
            Map<String, Object> resp = new HashMap<>();
            resp.put("message", "Surge captured: " + simulated.getStepCount() + " steps harvested (" + simulated.getEnergyKwh() + " kWh)!");
            resp.put("telemetry", simulated);
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getKineticStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalSteps", kineticService.getTotalStepCount());
        stats.put("totalEnergyKwh", kineticService.getTotalEnergyGeneratedKwh());
        return ResponseEntity.ok(stats);
    }
}
