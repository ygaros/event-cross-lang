package edu.jakub.jureczko.storage.config;

import lombok.Getter;

import java.time.Duration;

@Getter
public enum PaymentStatus {
    INITIALIZED(Duration.ofHours(12)), IN_PROGRESS(Duration.ofHours(12)), FINISHED(Duration.ofHours(0));

    private final Duration duration;

    PaymentStatus(Duration duration) {
        this.duration = duration;
    }
}
