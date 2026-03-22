package com.example.landapp.repository;

import com.example.landapp.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {

    // Find an answer by the Question ID it belongs to
    Optional<Answer> findByLandListing_Id(Long landListingId);

    Optional<Answer> findByQuestionId(Long questionId);
}
