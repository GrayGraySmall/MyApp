package com.example.myapp.Model;

/**
 * Created by asus on 2016/5/1.
 */
public class SimpleCarBean {
    String brand;
    String model;
    String licence;
    public SimpleCarBean(String brand,String model,String licence){
        this.brand = brand;
        this.model = model;
        this.licence = licence;
    }
    public SimpleCarBean(){

    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getLicence() {
        return licence;
    }

    public void setLicence(String licence) {
        this.licence = licence;
    }

}
