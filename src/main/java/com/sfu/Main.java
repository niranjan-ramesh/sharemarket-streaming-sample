package com.sfu;

import com.sfu.entity.AggFinanceChart;
import com.sfu.entity.AggStock;
import com.sfu.entity.Stock;
import com.sfu.utils.*;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.WindowStore;

import java.time.Duration;
import java.util.*;

public class Main {

    public static void main(String[] args) {

        StreamsBuilder builder = new StreamsBuilder();

        Serde<Stock> stockSerde = Serdes.serdeFrom(new JsonPojoSerializer<>(), new JsonPojoDeserializer<>(Stock.class));
        Serde<AggFinanceChart> aggSerde = Serdes.serdeFrom(new JsonPojoSerializer<>(), new JsonPojoDeserializer<>(AggFinanceChart.class));
        Serde<AggStock> aggStockSerde = Serdes.serdeFrom(new JsonPojoSerializer<>(), new JsonPojoDeserializer<>(AggStock.class));

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "ws-stock-application-7");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        KStream<String, Stock> input = builder.stream("input", Consumed.with(Serdes.String(), stockSerde));

        Map<String, KStream<String, Stock>> splitBranches = input.split(Named.as("branch-"))
                .branch((key, value) -> value.getIndex().equalsIgnoreCase("sensex"), Branched.as("sensex"))
                .branch((key, value) -> value.getIndex().equalsIgnoreCase("nifty"), Branched.as("nifty"))
                .defaultBranch();

        KStream<String, Stock> sensex = splitBranches.get("branch-sensex");
        KStream<String, Stock> nifty = splitBranches.get("branch-nifty");

        Aggregator<String, Stock, AggStock> stockAggregator = (aggKey, stock, aggValue) -> {
            aggValue.setCount(1l + aggValue.getCount());
            aggValue.setTotalVolume(stock.getVolume() + aggValue.getTotalVolume());
            aggValue.setTotalValue(stock.getCurrentValue() + aggValue.getTotalValue());
            return aggValue;
        };

        ValueMapper<AggStock, AggStock> aggregrateMapper = (value -> {
            value.setAverageValue(value.getTotalValue() / value.getCount());
            value.setAverageVolume(value.getTotalVolume() / value.getCount());
            return value;
        });

        KTable<Windowed<String>, AggStock> sensexAgg = sensex.groupByKey()
                .windowedBy(TimeWindows.ofSizeWithNoGrace(Duration.ofMinutes(15l)))
                .aggregate(() -> (new AggStock()),
                        stockAggregator,
                        Materialized.<String, AggStock, WindowStore<Bytes,byte[]>>as("agg-sensex-state-store").withValueSerde(aggStockSerde))
                .mapValues(aggregrateMapper);

        KTable<Windowed<String>, AggStock> niftyAgg = nifty.groupByKey()
                .windowedBy(TimeWindows.ofSizeWithNoGrace(Duration.ofMinutes(15l)))
                .aggregate(() -> (new AggStock()),
                        stockAggregator,
                        Materialized.<String, AggStock, WindowStore<Bytes,byte[]>>as("agg-nifty-state-store").withValueSerde(aggStockSerde))
                .mapValues(aggregrateMapper);

        sensex.to("sensex-events", Produced.with(Serdes.String(), stockSerde));
        nifty.to("nifty-events", Produced.with(Serdes.String(), stockSerde));

        sensexAgg.toStream((wk, v) -> wk.key()).to("sensex-agg", Produced.with(Serdes.String(), aggStockSerde));
        niftyAgg.toStream((wk, v) -> wk.key()).to("nifty-agg", Produced.with(Serdes.String(), aggStockSerde));

        KTable<String, Stock> sensexTable = builder.table("sensex-events", Consumed.with(Serdes.String(), stockSerde));
        KTable<String, Stock> niftyTable = builder.table("nifty-events", Consumed.with(Serdes.String(), stockSerde));

        KTable<String, AggFinanceChart> aggTable = sensexTable.join(niftyTable, (sensexChart, niftyChart) -> {
                AggFinanceChart chart = new AggFinanceChart();
                chart.setName(sensexChart.getName());
                chart.setNiftyLastRefreshed(niftyChart.getLastRefreshed());
                chart.setNiftyValue(niftyChart.getCurrentValue());
                chart.setNiftyVolume(niftyChart.getVolume());
                chart.setSensexLastRefreshed(sensexChart.getLastRefreshed());
                chart.setSensexValue(sensexChart.getCurrentValue());
                chart.setSensexVolume(sensexChart.getVolume());
                return chart;
            }
        );

        aggTable.toStream().to("output-chart", Produced.with(Serdes.String(), aggSerde));

        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.start();

        Producer producer = new Producer();
        producer.getProducer();
    }



}
