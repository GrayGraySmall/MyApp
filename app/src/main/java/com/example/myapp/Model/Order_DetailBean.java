package com.example.myapp.Model;

import java.io.Serializable;

/**
 * 订单查询类
 *
 * @author HarveyShine
 *
 *
 */
public class Order_DetailBean implements Serializable {
    private String name;// 姓名
    private String LicenseNum;// 车牌号
    private String time;// 订单生成时间
    private String GasQuality;// 油质量
    private int GasQuantity;// 油数量
    private double GasPrice;// 油单价
    private String PayState;// 支付状态
    private String GasStation; // 加油站位置

    public Order_DetailBean(String LicenseNum, String PayState, String time, int count) {

    }

    public String getGasStation() {
        return GasStation;
    }

    public void setGasStation(String gasStation) {
        GasStation = gasStation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLicenseNum() {
        return LicenseNum;
    }

    public void setLicenseNum(String licenseNum) {
        LicenseNum = licenseNum;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getGasQuality() {
        return GasQuality;
    }

    public void setGasQuality(String gasQuality) {
        GasQuality = gasQuality;
    }

    public int getGasQuantity() {
        return GasQuantity;
    }

    public void setGasQuantity(int gasQuantity) {
        GasQuantity = gasQuantity;
    }

    public double getGasPrice() {
        return GasPrice;
    }

    public void setGasPrice(double gasPrice) {
        GasPrice = gasPrice;
    }

    public String getPayState() {
        return PayState;
    }

    public void setPayState(String payState) {
        PayState = payState;
    }

    public Order_DetailBean(String name, String licenseNum, String time,
                            String gasQuality, int gasQuantity, double gasPrice, String payState,
                            String GasStation) {
        this.name = name;
        LicenseNum = licenseNum;
        this.time = time;
        GasQuality = gasQuality;
        GasQuantity = gasQuantity;
        GasPrice = gasPrice;
        PayState = payState;
        this.GasStation = GasStation;
    }

    public Order_DetailBean() {
        this.name = null;
        LicenseNum = null;
        this.time = null;
        GasQuality = null;
        GasQuantity = 0;
        GasPrice = 0;
        PayState = null;
    }

    public Order_DetailBean(String licenseNum, String time, int gasQuantity,
                            double gasPrice, String payState) {
        super();
        LicenseNum = licenseNum;
        this.time = time;
        GasQuantity = gasQuantity;
        GasPrice = gasPrice;
        PayState = payState;
    }
}
