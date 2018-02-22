package io.pivotal.dmfrey.eventStoreDemo.domain.client.kafka.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.pivotal.dmfrey.eventStoreDemo.domain.events.DomainEvent;
import io.pivotal.dmfrey.eventStoreDemo.domain.model.Board;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.Consumed;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Serialized;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
//@EnableKafka
//@EnableKafkaStreams
//@EnableAutoConfiguration
public class KafkaConfig {

    public static final String BOARD_EVENTS_SNAPSHOTS = "board-events-snapshots";

//    @Value( "${spring.cloud.stream.kafka.binder.zkNodes:localhost:9092}" )
//    private String zookeeperNodes;

//    @Bean
//    public KafkaTemplate<String, DomainEvent> kafkaTemplate( final ProducerFactory<String,DomainEvent> producerFactory ) {
//
//        return new KafkaTemplate<>( producerFactory );
//    }

//    @Bean
//    public JsonSerializer<DomainEvent> jsonSerializer( final ObjectMapper mapper ) {
//
//        return new JsonSerializer<>( mapper );
//    }

//    @Bean
//    public JsonDeserializer<DomainEvent> jsonDeserializer( final ObjectMapper mapper ) {
//
//        return new JsonDeserializer<>( DomainEvent.class, mapper );
//    }

//    @Bean
//    public ProducerFactory<String, DomainEvent> producerFactory( final JsonSerializer<DomainEvent> jsonSerializer ) {
//
//        DefaultKafkaProducerFactory<String, DomainEvent> producerFactory = new DefaultKafkaProducerFactory<>( config() );
//        producerFactory.setValueSerializer( jsonSerializer );
//
//        return producerFactory;
//    }

//    private Map<String, Object> config() {
//
//        Map<String, Object> config = new HashMap<>();
//        config.put( ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, zookeeperNodes );
//        config.put( ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class );
//        config.put( ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class );
//
//
//        return config;
//    }

//    @Bean( name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME )
//    public StreamsConfig streamsConfig( final JsonSerializer<DomainEvent> jsonSerializer, final JsonDeserializer<DomainEvent> jsonDeserializer ) {
//
//        Map<String, Object> config = new HashMap<>();
//        config.put( StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, zookeeperNodes );
//        config.put( StreamsConfig.APPLICATION_ID_CONFIG, "board-event-publisher" );
//
//        return new StreamsConfig( config );
//    }

//    @Bean
//    public KTable<String, Board> kTable( final StreamsBuilder builder, final ObjectMapper mapper ) {
//
//        Serde<DomainEvent> domainEventSerde = new JsonSerde<>( DomainEvent.class, mapper );
//        Serde<Board> boardSerde = new JsonSerde<>( Board.class, mapper );
//
//        return builder
//                        .stream( "board-events", Consumed.with( Serdes.String(), domainEventSerde ) )
//                        .groupBy( (s, domainEvent) -> domainEvent.getBoardUuid().toString(), Serialized.with( Serdes.String(), domainEventSerde ) )
//                        .aggregate(
//                                Board::new,
//                                (s, domainEvent, board) -> board.handleEvent( domainEvent ),
//                                boardSerde,
//                                BOARD_EVENTS_SNAPSHOTS
//                        );
//    }

}
