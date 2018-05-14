package com.example.myapp.Model;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by asus on 2016/5/24.
 */
public class GasStation implements Serializable{
    private String name;
    private String address;
    private double latitude;
    private double longitude;
    private int distance;
    private Map<String,Double> price;

    public GasStation(String name, String address, double latitude, double longitude, int distance) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = distance;
    }

    public GasStation(String name, String address, double latitude, double longitude, int distance, Map<String, Double> price) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = distance;
        this.price = price;
    }

    public GasStation() {

    }

    public Map<String, Double> getPrice() {
        return price;
    }

    public void setPrice(Map<String, Double> price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }
    @Override
    public String toString(){
        return name + " " + address + " " + latitude + " " + longitude + " " + distance ;
    }
}
