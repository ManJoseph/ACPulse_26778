package com.acpulse.repository;

import com.acpulse.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Integer> {
    List<Location> findByType(Location.LocationType type);
    List<Location> findByParentId(Integer parentId);

    Optional<Location> findByCode(String code);
}
