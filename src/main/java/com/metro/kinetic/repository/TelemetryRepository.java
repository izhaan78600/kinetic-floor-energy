package com.metro.kinetic.repository;

import com.metro.kinetic.model.Telemetry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TelemetryRepository extends JpaRepository<Telemetry, Long> {

    @Query("SELECT COALESCE(SUM(t.energyKwh), 0.0) FROM Telemetry t")
    Double sumTotalEnergyKwh();

    @Query("SELECT COALESCE(SUM(t.stepCount), 0L) FROM Telemetry t")
    Long sumTotalStepCount();

    List<Telemetry> findAllByOrderByIdDesc();

    List<Telemetry> findTop10ByOrderByIdDesc();

    @Query("SELECT t.date, SUM(t.stepCount), SUM(t.energyKwh) FROM Telemetry t GROUP BY t.date ORDER BY t.date ASC")
    List<Object[]> findDailyAggregated();

    @Query("SELECT s.sectorName, SUM(t.stepCount), SUM(t.energyKwh) FROM Telemetry t JOIN t.sector s GROUP BY s.sectorName")
    List<Object[]> findSectorAggregated();
}
