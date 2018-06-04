package io.pivotal.dmfrey.eventStoreDemo.domain.service;

import java.util.UUID;

public class KeyGenerator {

    public UUID generate() {

        return UUID.randomUUID();
    }

}
