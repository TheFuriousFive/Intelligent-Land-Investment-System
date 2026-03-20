package com.example.landapp.service;

import com.example.landapp.entity.LandListing;
import com.example.landapp.entity.Owner;
import com.example.landapp.entity.VerificationStatus;
import com.example.landapp.repository.LandListingRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class StripePaymentService {

    private final LandListingRepository landRepository;
    private final String frontendUrl;

    public StripePaymentService(
            LandListingRepository landRepository,
            @Value("${frontend.url:http://localhost:3000}") String frontendUrl) {
        this.landRepository = landRepository;
        this.frontendUrl = frontendUrl;
    }

    @Transactional
    public String initiateListingPayment(Long listingId, Owner currentOwner) {
        LandListing listing = landRepository.findById(listingId)
                .orElseThrow(() -> new RuntimeException("Listing Not Found With Id: " + listingId));

        // Security check: Only the owner who created the listing can pay for it
        if (!currentOwner.getId().equals(listing.getOwner().getId())) {
            throw new RuntimeException("Unauthorised: This listing does not belong to you.");
        }

        // Generate the Stripe URLs for your Next.js frontend
        String successUrl = frontendUrl + "/owner/payment/success?session_id={CHECKOUT_SESSION_ID}";
        String cancelUrl = frontendUrl + "/owner/payment/failure";

        String sessionUrl = getCheckoutSession(listing, currentOwner, successUrl, cancelUrl);

        // Update status to indicate we are waiting for the webhook
        listing.setVerificationStatus(VerificationStatus.PENDING_PAYMENT);
        landRepository.save(listing);

        return sessionUrl;
    }

    private String getCheckoutSession(LandListing listing, Owner owner, String successUrl, String failureUrl) {
        log.info("Creating Stripe session for Land Listing Id: {}", listing.getId());

        try {
            // Optional: Create a Stripe Customer (Good for tracking payments later)
            CustomerCreateParams customerParams = CustomerCreateParams.builder()
                    .setName(owner.getFirstName() + " " + owner.getLastName())
                    .setEmail(owner.getEmail())
                    .build();
            Customer customer = Customer.create(customerParams);

            SessionCreateParams sessionParams = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setBillingAddressCollection(SessionCreateParams.BillingAddressCollection.REQUIRED)
                    .setCustomer(customer.getId())
                    .setSuccessUrl(successUrl)
                    .setCancelUrl(failureUrl)
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setQuantity(1L) // Always 1 fee per listing
                                    .setPriceData(
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                    .setCurrency("lkr") // Sri Lankan Rupees
                                                    .setUnitAmount(500000L) // Rs. 5000.00
                                                    .setProductData(
                                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                    .setName("Land Verification Fee: " + listing.getTitle())
                                                                    .setDescription("Location: " + listing.getLocation())
                                                                    .build()
                                                    )
                                                    .build()
                                    )
                                    .build()
                    )
                    .build();

            Session session = Session.create(sessionParams);

            // Save the session ID to the database so the Webhook can verify it
            listing.setPaymentSessionId(session.getId());
            landRepository.save(listing);

            log.info("Session created successfully for Listing Id: {}", listing.getId());
            return session.getUrl();

        } catch (StripeException e) {
            log.error("Stripe API Error: ", e);
            throw new RuntimeException("Failed to create Stripe checkout session", e);
        }
    }

    // ---------------------------------------------------------
    // THE WEBHOOK LOGIC: What happens when the money clears
    // ---------------------------------------------------------
    @Transactional
    public void capturePayment(Event event) {
        // We only care about the event when a checkout is successfully completed
        if ("checkout.session.completed".equals(event.getType())) {

            // Extract the session object from Stripe's JSON payload
            Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
            if (session == null) {
                log.error("Webhook Error: Could not deserialize Stripe session.");
                return;
            }

            String sessionId = session.getId();

            // Find the exact land listing in your database that matches this payment
            LandListing listing = landRepository.findByPaymentSessionId(sessionId)
                    .orElseThrow(() -> new RuntimeException("Listing not found for Stripe session: " + sessionId));

            // BOOM! PAYMENT SUCCESSFUL!
            // Move it out of PENDING_PAYMENT and into the Authenticator's Queue!
            listing.setVerificationStatus(VerificationStatus.PENDING_VERIFICATION);
            landRepository.save(listing);

            log.info("✅ Payment confirmed via Webhook! Listing {} is now PENDING_VERIFICATION.", listing.getId());
        } else {
            // Optional: Log other events if you want to see what else Stripe sends
            log.info("Unhandled Stripe event type: {}", event.getType());
        }
    }
}
