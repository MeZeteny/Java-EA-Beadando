package hu.nje.beadando.model;

import java.io.Serializable;

public class Rate implements Serializable {
    private String date;
    private Double value;

    public Rate(String date, Double value) {
        this.date = date;
        this.value = value;
    }

    public Double getValue() {
        return value;
    }

    public String getDate() {
        return date;
    }

}
