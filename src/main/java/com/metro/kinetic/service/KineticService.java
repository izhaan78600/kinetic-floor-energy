package com.metro.kinetic.service;

import com.metro.kinetic.model.StationSector;
import com.metro.kinetic.model.Telemetry;
import com.metro.kinetic.repository.StationSectorRepository;
import com.metro.kinetic.repository.TelemetryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@Service
public class KineticService {

    @Autowired
    private StationSectorRepository sectorRepo;

    @Autowired
    private TelemetryRepository telemetryRepo;

    private final Random random = new Random();

    // ==========================================
    // STATION SECTORS
    // ==========================================
    public StationSector addSector(StationSector sector) {
        return sectorRepo.save(sector);
    }

    public List<StationSector> getAllSectors() {
        return sectorRepo.findAll();
    }

    public StationSector getSectorById(Long id) {
        return sectorRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Sector not found: " + id));
    }

    public void deleteSector(Long id) {
        sectorRepo.deleteById(id);
    }

    // ==========================================
    // TELEMETRY
    // ==========================================
    public Telemetry recordTelemetry(Long sectorId, LocalDate date, Long stepCount, Double energyKwh) {
        if (date == null) date = LocalDate.now();
        if (stepCount == null || stepCount <= 0) {
            throw new IllegalArgumentException("Step count must be greater than zero");
        }
        // Auto-calculate energy if not provided: 0.00026 kWh per step
        if (energyKwh == null || energyKwh <= 0.0) {
            energyKwh = Math.round(stepCount * 0.00026 * 1000.0) / 1000.0;
        }

        Telemetry telemetry = new Telemetry(sectorId, date, stepCount, energyKwh);
        return telemetryRepo.save(telemetry);
    }

    public List<Telemetry> getAllTelemetry() {
        return telemetryRepo.findAllByOrderByIdDesc();
    }

    public Double getTotalEnergyGeneratedKwh() {
        return telemetryRepo.sumTotalEnergyKwh();
    }

    public Long getTotalStepCount() {
        return telemetryRepo.sumTotalStepCount();
    }

    public Telemetry simulateFootstepBurst() {
        List<StationSector> sectors = sectorRepo.findAll();
        if (sectors.isEmpty()) {
            throw new IllegalStateException("No station sectors available to simulate");
        }
        StationSector randomSector = sectors.get(random.nextInt(sectors.size()));
        long steps = 1500L + random.nextInt(3500); // 1,500 - 5,000 footsteps
        double factor = 0.00024 + (random.nextDouble() * 0.00005);
        double kwh = Math.round(steps * factor * 1000.0) / 1000.0;

        return recordTelemetry(randomSector.getId(), LocalDate.now(), steps, kwh);
    }
}
