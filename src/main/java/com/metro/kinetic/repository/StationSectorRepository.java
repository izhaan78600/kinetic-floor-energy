package com.metro.kinetic.repository;

import com.metro.kinetic.model.StationSector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StationSectorRepository extends JpaRepository<StationSector, Long> {
}
