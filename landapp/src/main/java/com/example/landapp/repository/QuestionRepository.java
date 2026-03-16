
package com.example.landapp.repository;

import com.example.landapp.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    // Find all questions for a specific land listing
    List<Question> findByLandListing_Id(Long landListingId);

}