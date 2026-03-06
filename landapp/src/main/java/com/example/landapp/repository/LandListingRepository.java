package com.example.landapp.repository;

import com.example.landapp.entity.LandListing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LandListingRepository extends JpaRepository<LandListing, Long> {

    // Spring Boot writes the SQL for this automatically!
    List<LandListing> findByOwnerId(Long ownerId);


}