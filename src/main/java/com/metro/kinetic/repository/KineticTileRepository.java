package com.metro.kinetic.repository;

import com.metro.kinetic.model.KineticTile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KineticTileRepository extends JpaRepository<KineticTile, Long> {
}
