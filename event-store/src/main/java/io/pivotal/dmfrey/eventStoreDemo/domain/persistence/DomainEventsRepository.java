package io.pivotal.dmfrey.eventStoreDemo.domain.persistence;

import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface DomainEventsRepository extends CrudRepository<DomainEventsEntity, String> {

}
