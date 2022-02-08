package com.sfu.entity;

public class AggStock {

    private long count;
    private long totalValue;
    private long totalVolume;
    private long averageValue;
    private long averageVolume;

    public AggStock() {
        count = 0;
        totalValue = 0;
        totalVolume = 0;
        averageValue = 0;
        averageVolume = 0;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public long getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(Long totalValue) {
        this.totalValue = totalValue;
    }

    public long getTotalVolume() {
        return totalVolume;
    }

    public void setTotalVolume(Long totalVolume) {
        this.totalVolume = totalVolume;
    }

    public long getAverageValue() {
        return averageValue;
    }

    public void setAverageValue(Long averageValue) {
        this.averageValue = averageValue;
    }

    public long getAverageVolume() {
        return averageVolume;
    }

    public void setAverageVolume(Long averageVolume) {
        this.averageVolume = averageVolume;
    }

}
