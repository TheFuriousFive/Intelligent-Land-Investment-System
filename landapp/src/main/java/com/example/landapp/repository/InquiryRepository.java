package com.example.landapp.repository;

import com.example.landapp.entity.Inquiry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InquiryRepository extends JpaRepository<Inquiry, Long> {

    // For the Owner's inbox
    List<Inquiry> findByOwnerIdOrderByCreatedAtDesc(Long ownerId);

    // For the Investor's history/dashboard
    List<Inquiry> findByInvestorIdOrderByCreatedAtDesc(Long investorId);

    boolean existsByInvestorIdAndLandListingId(Long investorId, Long landListingId);
}