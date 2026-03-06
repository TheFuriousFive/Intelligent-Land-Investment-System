
package com.example.landapp.repository;

import com.example.landapp.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    // Find all questions for a specific land listing
    List<Question> findByListingId(Long listingId);

    //find all unanswered questions relevant to owner
    //useful for "owner dashboard "
    List<Question> findByListingOwnerIdAndAnswerIsNull(Long ownerId);
}