package com.example.landapp.service;

import com.example.landapp.dto.AnswerResponseDTO;
import com.example.landapp.dto.LandListingResponseDTO;
import com.example.landapp.dto.QuestionResponseDTO;
import com.example.landapp.dto.ReviewResponseDTO;
import com.example.landapp.entity.Answer;
import com.example.landapp.entity.LandListing;
import com.example.landapp.entity.Question;
import com.example.landapp.entity.Review;
import com.example.landapp.mapper.LandListingMapper;
import com.example.landapp.repository.AnswerRepository;
import com.example.landapp.repository.LandListingRepository;
import com.example.landapp.repository.QuestionRepository;
import com.example.landapp.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LandListingService {

    @Autowired
    private LandListingRepository landListingRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository; // We need this now!

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private LandListingMapper landListingMapper;

    // 1. GET SINGLE LISTING DETAILS
    public LandListingResponseDTO getListingById(Long listingId) {
        LandListing listing = landListingRepository.findById(listingId)
                .orElseThrow(() -> new RuntimeException("Listing not found with ID: " + listingId));

        return landListingMapper.toResponseDTO(listing);
    }

    // 2. GET QUESTIONS AND NESTED ANSWERS
    public List<QuestionResponseDTO> getListingQuestions(Long listingId) {
        // Fetch all questions for this land
        List<Question> questions = questionRepository.findByLandListing_Id(listingId);

        return questions.stream().map(question -> {
            QuestionResponseDTO dto = new QuestionResponseDTO();
            dto.setId(question.getId());
            dto.setContent(question.getContent()); // Inherited from Message
            dto.setCreatedAt(question.getCreatedAt());

            // TODO: Add 'private String investorName;' to QuestionResponseDTO and uncomment this!
            // if (question.getInvestor() != null) {
            //     dto.setInvestorName(question.getInvestor().getFirstName());
            // }

            // NEW FIX: Manually hunt down the answer using the AnswerRepository!
            Optional<Answer> answerOpt = answerRepository.findByQuestionId(question.getId());

            if (answerOpt.isPresent()) {
                Answer answer = answerOpt.get();
                AnswerResponseDTO answerDto = new AnswerResponseDTO();
                answerDto.setId(answer.getId());
                answerDto.setContent(answer.getContent()); // Inherited from Message
                answerDto.setAnsweredAt(answer.getAnsweredAt());
                answerDto.setLandListingId(listingId);

                dto.setAnswer(answerDto);
            }

            return dto;
        }).collect(Collectors.toList());
    }

    // 3. GET OWNER REVIEWS FOR THIS LISTING
    public List<ReviewResponseDTO> getListingReviews(Long listingId) {
        LandListing listing = landListingRepository.findById(listingId)
                .orElseThrow(() -> new RuntimeException("Listing not found with ID: " + listingId));

        // Fetch all reviews using the new repository method we just added
        List<Review> reviews = reviewRepository.findByOwnerId(listing.getOwner().getId());

        return reviews.stream().map(review -> {
            ReviewResponseDTO dto = new ReviewResponseDTO();
            dto.setId(review.getId());
            dto.setRating(review.getRating());
            dto.setComment(review.getComment());
            dto.setCreatedAt(review.getCreatedAt());

            if (review.getInvestor() != null) {
                dto.setInvestorName(review.getInvestor().getFirstName());
            }

            return dto;
        }).collect(Collectors.toList());
    }
}