# Event Store Demo

We recently finished work on a system in which we built an Event Source system. This application is a demo of the architecture we produced.

This application was unique in that we implemented the backend with Apache Kafka, MongoDb and MySQL. The final solution was based on MySQL, however, the reasons for not using the other two solutions were not technical. Kafka and MongoDb would not be available in the production environments, so we adjusted.

## About the Demo App

This application is a simple Kanban. It only allows for minimal board and story management.

The core view of application is a `Board`. It internal state is able to be derived from any list of `DomainEvent`s supplied to it.  The snippet below illustrates this.

````
public class Board {

  private String name; [1]
  
  public String getName() {
    return this.name;
  }

  public void renameBoard( final String name ) { [2]
      if( null == name || “”.equals( name ) { [3]
          throw new IllegalArgumentException( “name is not valid” );
      }
      boardRenamed( new BoardRenamed( name, this.boardUuid, Instant.now() );
  }

  private Board boardRenamed( final BoardRenamed event ) { [4]
      this.name = name;
      this.changes.add( event ); [5]
      return this;
  }

  public static Board createFrom( final UUID boardUuid, final Collection<DomainEvent> domainEvents ) { [6]
      return ofAll( domainEvents ).foldLeft( new Board( boardUuid ), Board::handleEvent );
  }

  public Board handleEvent( final DomainEvent event ) {
      return API.Match( domainEvent ).of(
         Case( $( instanceOf( BoardInitialized.class ) ), this::boardInitialized ),
         Case( $( instanceOf( BoardRenamed.class ) ), this::boardRenamed ),
         ...
         Case( $(), this )
      );
  }
}

````

The `Board` snippet shows the following:
1. The `Board` has an internal state attribute: `name`
2. A command, `renameBoard` can be acted upon to initiate and internal state changes
3. Invariants can be validated before recording a new `DomainEvent`
4. A `DomainEvent` triggers the internal state change
5. `DomainEvents` are recorded in a change log 
6. The internal state can be rebuilt from a collection of `DomainEvent`s that represent the application state for a `Board`

The key thing to note here is that a `Board` is not a POJO with just getters and setters. Folding all of the `DomainEvent`s in sequence makes up the internal application state. This could represent the current application state today, or the `DomainEvent`s could filtered by time to replay the application state to a prior point in time.

#### Anatomy of an Event

`DomainEvent`s are represented as JSON.  JSON is easy to understand, read, query and parse. Since this is a Spring Boot application, written in Java, the `DomainEvent`s are marshaled to JSON with Jackson.  However, tracking events in the event store does not assume you will use Java.  Any language could be used to read and write events from/to the event store.

There is some context that needs to be derived from each event, however. Loosely, these are "header" fields in the payload that direct processors how to handle the events.
````
{
    “eventType”: “BoardInitialized”,
    “boardUuid”: "ff4795e1-2514-4f5a-90e2-cd33dfadfbf2",
    “occurredOn”: "2018-02-23T03:49:52.313Z",
    ....
}
````

In the above event, the type of event can be determined as can the UUID of the `Board` in which it applies.  Also note the `occurredOn` date.  It is the means by which prior states can be derived. An application could lookup a list of `DomainEvent` and apply a filter up to a supplied `occurredOn` date to see what the application state was at that point in time. 

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

The API application is a common gateway layer between Command and Query applications. The lower applications are separated in typical CQRS fashion. It is a Spring Boot 2 application and is simply a proxy service to the lower apps.

API Documentation is produced by Spring RestDocs and is available at [docs](http://localhost:8765/docs/index.html). In order to see this page, you must run the Spring Boot fat jar.
````
$ java -jar api/build/libs/api-0.0.1-SNAPSHOT.jar
````

The demo application currently has 2 flavors: `kafka` and `event-store`. Each is described in further detail below.

### Kafka Architecture

The Command application will accept HTTP verbs POST, PATCH, PUT and DELETE through the API application or directly.  See below for example curl commands.  The Query application will accept HTTP GET requests for views of a `Board`.

As the Command application accepts new requests, they are validated and turned into `DomainEvent`s. Each `DomainEvent` is pushed to the Kafka Topic.

Once the `DomainEvent`s are in the topic, the command and query applications each subscribe to the events as a `KStream`, which is used to build the `KTable` aggregate view respective to each application.

A `KStream` is an unbounded stream of data, in this case, `DomainEvent`s.  The stream acts both as a persistence layer as well as a notification layer. Events in the stream can then subscribed to, filtered, transformed, etc in any number of ways.

A `KTable` uses the above `KStream` as input and first groups all of the events by their `boardUuid`.  The grouped events are continually updated as new events are to the stream by the Command application. The groups allow the data to be aggregated, essentiall folding the events in sequence to produce a `Board` view that is respective to each application.

For instance, the Query application does not necessarily care about the maintaining a list of events in the materialized view, where as the Command application does, so that it can track the new change events from the existing ones.

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

All of the implemented capabilities are available in the API Documentation at [docs](http://localhost:8765/docs/index.html). Below are some simple `curl` commands to interact with the API. These can also be ported to a rest client, such as Postman. Also note, these examples use dummy UUIDs. The documentation and the real services need to work with actual UUIDs.

### Create a new Board

````
$ curl 'http://localhost:8765/boards' -i -X POST
````

Look for a `LOCATION` header to contain the URL of the new board.

### Rename an existing Board
````
$ curl 'http://localhost:8765/boards/00000000-0000-0000-0000-000000000000' -i -X PATCH
````

### Add a Story to an existing Board
````
$ curl 'http://localhost:8765/boards/00000000-0000-0000-0000-000000000000/stories' -i -X POST -d 'name=Test+Story'
````

### Update an existing Story on an existing Board
````
$ curl 'http://localhost:8765/boards/00000000-0000-0000-0000-000000000000/stories/00000000-0000-0000-0000-000000000000' -i -X PUT -d 'name=Test+Story+Updated'
````

### Delete an existing Story on an existing Board
````
$ curl 'http://localhost:8765/boards/00000000-0000-0000-0000-000000000000/stories/00000000-0000-0000-0000-000000000000' -i -X DELETE
````

## Source Code

The code for the application can be found on [GitHub](https://github.com/dmfrey/event-store-demo).


[kafka-architecture]: images/Event%20Source%20Demo%20-%20Kafka.png "Kafka Architecture"
[event-store-architecture]: images/Event%20Source%20Demo%20-%20Event%20Store.png "Event Store Architecture"
