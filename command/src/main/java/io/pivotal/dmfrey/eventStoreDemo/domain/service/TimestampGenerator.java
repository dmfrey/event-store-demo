package io.pivotal.dmfrey.eventStoreDemo.domain.service;

import java.time.Instant;

public class TimestampGenerator {

    public Instant generate() {

        return Instant.now();
    }

}
