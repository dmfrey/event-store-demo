# Event Store Demo

We recently finished work on a system for the USAF in which we built an Event Source system. This application is a demo of the architecture we produced.

This application was unique in that we implemented the backend with Apache Kafka, MongoDb and MySQL. The final solution was based on MySQL, however, the reasons for not using the other two solutions were not technical. Kafka and MongoDb would not be available in the production environments, so we adjusted.

## About the Demo App

This application is a simple Kanban. It only allows for minimal board and story management.

The application is broken down into a series of microservices. A Eureka Discovery Server is needed to run the demo. The Spring Cloud cli is easy to setup and use and provides Eureka out of the box.

### Architecture

#### Kafka Architecture
![kafka architecture][kafka-architecture]

#### Event Store Architecture
![event store architecture][event-store-architecture]


### API

The API application is a common gateway layer between Command and Query applications. The lower applications are separated in typical CQRS fashion.

The applications is a Spring Boot 2 application and is simply a proxy service to the lower apps.

API Documentation is produced by Spring RestDocs and is available at [docs](http://localhost:8765/docs).

````
$ ./gradlew :api:bootRun
````

### Command

The Command application accepts the HTTP verbs POST, PATCH, PUT and DELETE. It represents the Command side of CQRS. A profile is required to run the app: `kafak`, `event-store`.

#### kafka Profile

This profile is configured to connect to a local Kakfa and Zookeeper.

Run the `kafka` profile
````
$ SPRING_PROFILES_ACTIVE=kafka ./gradlew :command:bootRun
````

#### event-store Profile

This profile is configured to connect to the `event-store` application.

Run the `event-store` profile
````
$ SPRING_PROFILES_ACTIVE=event-store ./gradlew :command:bootRun 
````

### Query

The Query application accepts the HTTP verb GET. It represents the Query side of CQRS. A profile is required to run the app: `kafak`, `event-store`.

#### kafka Profile

This profile is configured to connect to a local Kakfa and Zookeeper.

Run the `kafka` profile
````
$ SPRING_PROFILES_ACTIVE=kafka ./gradlew :query:bootRun
````

#### event-store Profile

This profile is configured to connect to the `event-store` application.

Run the `event-store` profile
````
$ SPRING_PROFILES_ACTIVE=event-store ./gradlew :query:bootRun 
````

### Event Store

This application sets up the Event Store with an H2 backend. It uses Spring Data JPA and will work with any compliant datasource.

````
$ ./gradlew :event-store:bootRun
````

[kafka-architecture]: images/Event%20Source%20Demo%20-%20Kafka.png "Kafka Architecture"
[event-store-architecture]: images/Event%20Source%20Demo%20-%20Event%20Store.png "Event Store Architecture"