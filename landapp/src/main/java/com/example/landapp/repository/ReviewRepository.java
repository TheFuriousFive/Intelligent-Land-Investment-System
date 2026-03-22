package com.example.landapp.repository;

import com.example.landapp.entity.Question;
import com.example.landapp.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByOwnerId(Long ownerId);
}
