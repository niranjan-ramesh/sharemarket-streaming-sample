package com.sfu.entity;

import java.time.ZonedDateTime;

public class AggFinanceChart {

    private String name;
    private ZonedDateTime niftyLastRefreshed;
    private ZonedDateTime sensexLastRefreshed;
    private Long niftyValue;
    private Long sensexValue;
    private Long niftyVolume;
    private Long sensexVolume;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ZonedDateTime getNiftyLastRefreshed() {
        return niftyLastRefreshed;
    }

    public void setNiftyLastRefreshed(ZonedDateTime niftyLastRefreshed) {
        this.niftyLastRefreshed = niftyLastRefreshed;
    }

    public ZonedDateTime getSensexLastRefreshed() {
        return sensexLastRefreshed;
    }

    public void setSensexLastRefreshed(ZonedDateTime sensexLastRefreshed) {
        this.sensexLastRefreshed = sensexLastRefreshed;
    }

    public Long getNiftyValue() {
        return niftyValue;
    }

    public void setNiftyValue(Long niftyValue) {
        this.niftyValue = niftyValue;
    }

    public Long getSensexValue() {
        return sensexValue;
    }

    public void setSensexValue(Long sensexValue) {
        this.sensexValue = sensexValue;
    }

    public Long getNiftyVolume() {
        return niftyVolume;
    }

    public void setNiftyVolume(Long niftyVolume) {
        this.niftyVolume = niftyVolume;
    }

    public Long getSensexVolume() {
        return sensexVolume;
    }

    public void setSensexVolume(Long sensexVolume) {
        this.sensexVolume = sensexVolume;
    }


}
