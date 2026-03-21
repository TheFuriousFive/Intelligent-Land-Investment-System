package com.example.landapp.controller;

import com.example.landapp.entity.Owner;
import com.example.landapp.service.StripePaymentService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.net.Webhook;
import com.stripe.model.Event;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final StripePaymentService stripePaymentService;
    private final String webhookSecret;

    public PaymentController(
            StripePaymentService stripePaymentService,
            @Value("${stripe.webhook.secret}") String webhookSecret) {
        this.stripePaymentService = stripePaymentService;
        this.webhookSecret = webhookSecret;
    }

    // 1. Called by your Next.js frontend when the user clicks "Pay"
    @PostMapping("/{listingId}/checkout")
    public ResponseEntity<Map<String, String>> initiateCheckout(@PathVariable Long listingId) {
        try {
            // Grab the currently logged-in Owner from Spring Security
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Owner currentOwner = (Owner) authentication.getPrincipal();

            // Pass BOTH the listingId and the currentOwner to the service
            String sessionUrl = stripePaymentService.initiateListingPayment(listingId, currentOwner);

            // Return as a JSON object so Next.js can parse it easily
            return new ResponseEntity<>(Map.of("sessionUrl", sessionUrl), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 2. Called by Stripe's servers behind the scenes
    @PostMapping("/webhook")
    public ResponseEntity<Void> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        try {
            // This verifies that the request ACTUALLY came from Stripe and not a hacker
            Event event = Webhook.constructEvent(payload, sigHeader, webhookSecret);

            // Make sure you still have the capturePayment method in your StripePaymentService!
            stripePaymentService.capturePayment(event);

            return ResponseEntity.ok().build(); // Tell Stripe we got it!
        } catch (SignatureVerificationException e) {
            System.out.println("⚠️ Invalid Stripe webhook signature!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}