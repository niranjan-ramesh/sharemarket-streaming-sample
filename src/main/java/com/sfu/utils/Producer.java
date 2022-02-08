package com.sfu.utils;

import com.sfu.entity.Stock;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.ZonedDateTime;
import java.util.*;

public class Producer {
    private KafkaProducer<String, Stock> producer;
    public Producer() {
        Properties producerProps = new Properties();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonPojoSerializer.class.getName());

        producer = new KafkaProducer<>(producerProps);
    }

    public void getProducer(String index, Long timeToSleep) {
        Map<String, Stock> data = getMockData(index);
//        return () -> {
            while (true) {
                data.forEach((key, value) -> {
//                    System.out.println(key);
                    value.setCurrentValue(value.getCurrentValue() + (long) Math.random() * (30 + 30) - 30);
                    value.setVolume(value.getVolume() + (long) Math.random() * (200 + 200) - 200);
                    value.setIndex("nifty");
                    producer.send(new ProducerRecord<>("input", key, value), (metadata, exception) -> {
//                        System.out.println("Partition: " + metadata.partition());
//                        System.out.println("Offset: " + metadata.offset());
                    });
                    producer.flush();
                    value.setCurrentValue(value.getCurrentValue() + (long) Math.random() * (30 + 30) - 30);
                    value.setVolume(value.getVolume() + (long) Math.random() * (200 + 200) - 200);
                    value.setIndex("sensex");
                    producer.send(new ProducerRecord<>("input", key, value), (metadata, exception) -> {
//                        System.out.println("Partition: " + metadata.partition());
//                        System.out.println("Offset: " + metadata.offset());
                    });
                    producer.flush();


                });
//                try {
////                    sleep(timeToSleep);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                break;
            }
//        };
    }

    private Map<String, Stock> getMockData(String index) {
        Map<String, Stock> stocks = new HashMap<>();

        List<String> symbols = Arrays.asList("MSFT", "AMZN", "UBER", "ABNB", "NVDA", "NUE", "IT", "EXR", "GOOGL", "AAPL");
        List<String> names = Arrays.asList("Microsoft", "Amazon", "Uber", "AirBNB", "NVidia Corp", "Nucor Corp.", "Gartner Inc.", "Extra Space Storage Inc.", "Google", "Apple");

        for(int i=0;i<symbols.size();i++) {
            Stock stock = new Stock();
            stock.setLastRefreshed(ZonedDateTime.now());
            stock.setVolume(50 + (long) (Math.random() * (500 - 50)));
            stock.setName(names.get(i));
            stock.setCurrentValue(300 + (long) (Math.random() * (10000 - 300)));
            stock.setIndex(index);
            stocks.put(symbols.get(i), stock);
        }
        System.out.println("index: "+index);
        System.out.println(stocks);

        return stocks;
    }
}
