package com.gamingwallet.webhook.service.impl;

import com.gamingwallet.transaction.entity.Transaction;
import com.gamingwallet.transaction.enums.TransactionStatus;
import com.gamingwallet.transaction.repository.TransactionRepository;
import com.gamingwallet.user.entity.User;
import com.gamingwallet.user.repository.UserRepository;
import com.gamingwallet.webhook.dto.SpotflowWebhookRequest;
import com.gamingwallet.webhook.service.WebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebhookServiceImpl implements WebhookService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final JdbcTemplate jdbcTemplate;


    @Override
    @Transactional
    public void processWebhook(SpotflowWebhookRequest request) {

        // #1 correct event name
        if (!"payment_successful".equals(request.getEvent())) {
            log.info("Ignoring unhandled webhook event type: {}", request.getEvent());
            return;
        }

        String reference = request.getData().getReference();
        String eventId   = request.getData().getId(); // Spotflow's unique per-delivery id

        Transaction transaction = transactionRepository
                .findByReference(reference)
                .orElseThrow(() ->
                        new IllegalArgumentException("Unknown reference in webhook: " + reference));

        // #2 — idempotency gate: attempt to claim this event_id atomically
        try {
            jdbcTemplate.update(
                    "INSERT INTO processed_webhooks (event_id, reference) VALUES (?, ?)",
                    eventId, reference
            );
        } catch (DataIntegrityViolationException duplicate) {
            log.info("Duplicate webhook delivery for event {} — already credited, ignoring", eventId);
            return;
        }

        User user = transaction.getUser();
        user.setWalletBalance(user.getWalletBalance().add(transaction.getAmount()));
        transaction.setTransactionStatus(TransactionStatus.SUCCESS);
        transaction.setSpotflowReference(request.getData().getSpotflowReference());

        userRepository.save(user);
        transactionRepository.save(transaction);

        log.info("Wallet credited {} for user {} via webhook event {}",
                transaction.getAmount(), user.getId(), eventId);
    }
}
