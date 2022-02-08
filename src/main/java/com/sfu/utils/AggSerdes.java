package com.sfu.utils;

import com.sfu.entity.AggFinanceChart;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

public class AggSerdes extends Serdes.WrapperSerde<AggFinanceChart>{

    public AggSerdes() {
        super(new JsonPojoSerializer<>(), new JsonPojoDeserializer<AggFinanceChart>());
    }

}
