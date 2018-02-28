# Event Store Demo

We recently finished work on a system for the USAF in which we built an Event Source system. This application is a demo of the architecture we produced.

This application was unique in that we implemented the backend with Apache Kafka, MongoDb and MySQL. The final solution was based on MySQL, however, the reasons for not using the other two solutions were not technical. Kafka and MongoDb would not be available in the production environments, so we adjusted.

## About the Demo App

This application is a simple Kanban. It only allows for minimal board and story management.

### Setup

The application is broken down into a series of microservices. A Eureka Discovery Server is needed to run the demo. The Spring Cloud cli is easy to setup and use and provides Eureka out of the box.
````
$ spring cloud eureka
````

### Architecture

#### API

The API application is a common gateway layer between Command and Query applications. The lower applications are separated in typical CQRS fashion.

The applications is a Spring Boot 2 application and is simply a proxy service to the lower apps.

API Documentation is produced by Spring RestDocs and is available at [docs](http://localhost:8765/docs/index.html).

````
$ ./gradlew :api:bootRun
````

The demo application currently has 2 flavors: `kafka` and `event-store`. Each is described in further detail below.

#### Kafka Architecture

The Command application will accept HTTP verbs POST, PATCH, PUT and DELETE through the API application or directly.  See below for example curl commands.  The Query application will accept HTTP GET requests for views of a `Board`.

As the Command application accepts new requests, they are validated and turned into `DomainEvent`s. Each `DomainEvent` is pushed to the Kafka Topic.

Once the `DomainEvent`s are in the topic, the command and query applications each subscribe to the events as a `KStream`, which is used to build the `KTable` aggregate view respective to each application.

A `KStream` is an unbounded stream of data, in this case, `DomainEvent`s.  The stream acts both as a persistence layer as well as a notification layer. Events in the stream can then subscribed to, filtered, transformed, etc in any number of ways.

A `KTable` uses the above `KStream` as input and first groups all of the events by their `boardUuid`.  The grouped events are continually updated as new events are to the stream by the Command application. The groups allow the data to be aggregated, essentiall folding the events in sequence to produce a `Board` view that respective to each application.

For instance, the Query application doesn't necessarily care about the maintaining an exact sequence of events in the materialized view, where as the Command application does, so that it can track the new change events from the existing ones.

![kafka architecture][kafka-architecture]

Start Zookeeper and Kafka
````
$ zkServer start
$ kafka-server-start /usr/local/etc/kafka/server.properties
````

Run Command and Query applications in the `kafka` profile
````
$ SPRING_PROFILES_ACTIVE=kafka ./gradlew :command:bootRun
$ SPRING_PROFILES_ACTIVE=kafka ./gradlew :query:bootRun
````

Monitor the Kafka Topics
````
$ kafka-console-consumer --bootstrap-server localhost:9092 --topic board-events --from-beginning
$ kafka-console-consumer --bootstrap-server localhost:9092 --topic command-board-events-group-board-events-snapshots-changelog --from-beginning
$ kafka-console-consumer --bootstrap-server localhost:9092 --topic query-board-events-group-board-events-snapshots-changelog --from-beginning
````

#### Event Store Architecture

TODO: Define the Command and Query applications in the event-store profile

![event store architecture][event-store-architecture]

Run Command, Query and Event Store applications in the `event-store` profile
````
$ SPRING_PROFILES_ACTIVE=event-store ./gradlew :command:bootRun
$ SPRING_PROFILES_ACTIVE=event-store ./gradlew :query:bootRun
$ ./gradlew :event-store:bootRun
````


### Command

The Command application accepts the HTTP verbs POST, PATCH, PUT and DELETE. It represents the Command side of CQRS. A profile is required to run the app: `kafak`, `event-store`.

### Query

The Query application accepts the HTTP verb GET. It represents the Query side of CQRS. A profile is required to run the app: `kafak`, `event-store`.

### Event Store

This application sets up the Event Store with an H2 backend. It uses Spring Data JPA and will work with any compliant datasource.


[kafka-architecture]: images/Event%20Source%20Demo%20-%20Kafka.png "Kafka Architecture"
[event-store-architecture]: images/Event%20Source%20Demo%20-%20Event%20Store.png "Event Store Architecture"