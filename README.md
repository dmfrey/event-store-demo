# Event Store Demo

We recently finished work on a system for the USAF in which we built an Event Source system. This application is a demo of the architecture we produced.

This application was unique in that we implemented the backend with Apache Kafka, MongoDb and MySQL. The final solution was based on MySQL, however, the reasons for not using the other two solutions were not technical. Kafka and MongoDb would not be available in the production environments, so we adjusted.

## About the Demo App

This application is a simple Kanban. It only allows for minimal board and story management.

### Build the Demo App

All of the applications are sub projects of a parent Gradle build script.  Building the apps is easy with the included Gradle Wrapper.
````
$ ./gradlew build
````

### Setup

The application is broken down into a series of microservices. A Eureka Discovery Server is needed to run the demo. The Spring Cloud cli is easy to setup and use and provides Eureka out of the box.

Install the Spring Boot cli

Mac OS - Homebrew
````
$ brew tap pivotal/tap
$ brew install springboot
$ spring --version
````

Linux - SDKMan
````
$ curl -s "https://get.sdkman.io" | bash
$ source "$HOME/.sdkman/bin/sdkman-init.sh"
$ sdk install springboot
$ spring --version
````

Install the Spring Cloud cli extension
````
$ spring install org.springframework.cloud:spring-cloud-cli:1.4.0.RELEASE
````

Start Eureka
````
$ spring cloud eureka
````

## Architecture

### API

The API application is a common gateway layer between Command and Query applications. The lower applications are separated in typical CQRS fashion.

The applications is a Spring Boot 2 application and is simply a proxy service to the lower apps.

API Documentation is produced by Spring RestDocs and is available at [docs](http://localhost:8765/docs/index.html).

````
$ ./gradlew :api:bootRun
````

The demo application currently has 2 flavors: `kafka` and `event-store`. Each is described in further detail below.

### Kafka Architecture

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

### Event Store Architecture

The Command application will accept HTTP verbs POST, PATCH, PUT and DELETE through the API application or directly.  See below for example curl commands.  The Query application will accept HTTP GET requests for views of a `Board`.

As the Command application accepts new requests, they are validated and turned into `DomainEvent`s. Each `DomainEvent` is posted to the Event Store application. The Event Store will broadcast the `Domain Event` to the Event Notification Channel on RabbitMQ.

Once the `DomainEvent`s are in the Event Store, the command and query applications can each perform an HTTP GET to get the `Domain Event`s for a `Board` which is used to build the aggregate view respective to each application.

The Query application will also respond to the `Domain Event` notification from the rabbitmq channel by removing the changed `Board` from the local cache.

![event store architecture][event-store-architecture]

Start RabbitMQ
This is platform dependent.  For example, RabbitMQ is available in Homebrew on Mac OS or there is a repository for Debian and Ubuntu.

Run Command, Query and Event Store applications in the `event-store` profile
````
$ SPRING_PROFILES_ACTIVE=event-store ./gradlew :command:bootRun
$ SPRING_PROFILES_ACTIVE=event-store ./gradlew :query:bootRun
$ ./gradlew :event-store:bootRun
````

Monitor the database
Go to the [h2 console](http://localhost:9082/h2-console). Connect to the database `jdbc:h2:mem:testdb`. There are two tables of interest: `domain_events` and `domain_event`.

## Using the API

All of the implemented capabilities are available in the API Documentation at [docs](http://localhost:8765/docs/index.html). Below are some simple `curl` commands to interact with the API. These can also be ported to a rest client, such as Postman.

### Create a new Board

````
$ curl 'http://localhost:8765/boards' -i -X POST
````

### Rename an existing Board
````
$ curl 'http://localhost:8765/boards/00000000-0000-0000-0000-000000000000' -i -X PATCH
````

### Add a Story to an existing Board
````
$ curl 'http://localhost:8765/boards/c882c8f3-1b1c-4ab3-9086-1470b3ba0cb1/stories' -i -X POST -d 'name=Test+Story'
````

### Update an existing Story on an existing Board
````
$ curl 'http://localhost:8765/boards/4b1d138a-f4ac-4b64-8579-9dda99373ad9/stories/68e1da93-7993-499e-94a6-a0e6f79987ff' -i -X PUT -d 'name=Test+Story+Updated'
````

### Delete an existing Story on an existing Board
````
$ curl 'http://localhost:8765/boards/5701b111-7a76-475d-90e1-08e5aa4366a9/stories/a25c1d6e-085a-4201-b241-396948d1bf3f' -i -X DELETE
````


[kafka-architecture]: images/Event%20Source%20Demo%20-%20Kafka.png "Kafka Architecture"
[event-store-architecture]: images/Event%20Source%20Demo%20-%20Event%20Store.png "Event Store Architecture"
