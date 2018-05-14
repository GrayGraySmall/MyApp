package com.example.myapp.Model;

import java.io.Serializable;

/**
 * Created by HarveyShine on 2016/5/20.
 */
public class SimpleOrderBean implements Serializable{
    private String LicenseNum;//车牌号
    private String time;//订单生成时间
    private int Count; //总价
    private String PayState;//支付状态

    public int getCount() {
        return Count;
    }

    public String getLicenseNum() {
        return LicenseNum;
    }

    public String getPayState() {
        return PayState;
    }

    public String getTime() {
        return time;
    }

    public void setCount(int count) {
        Count = count;
    }

    public void setLicenseNum(String licenseNum) {
        LicenseNum = licenseNum;
    }

    public void setPayState(String payState) {
        PayState = payState;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public SimpleOrderBean(String licenseNum, String payState, String time, int count){
        this.LicenseNum = licenseNum;
        this.PayState = payState;
        this.time = time;
        this.Count = count;
    }
}
