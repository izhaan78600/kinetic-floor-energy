package com.metro.kinetic.repository;

import com.metro.kinetic.model.TileVendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TileVendorRepository extends JpaRepository<TileVendor, Long> {
}
