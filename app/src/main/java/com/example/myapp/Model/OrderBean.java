package com.example.myapp.Model;
/*
 * 这是订单类，包含订单的各种属性
 */
public class OrderBean {
	private int order_id = 0; // 订单号
	private String GasQuality = null; // 质量
	private int GasQuantity = 0; // 数量
	private double GasPrice = 0; // 单价
	private String time = null; // 订单生成日期
	private String License = null; // 订单的车牌号
	private String PayState = null;// 支付状态
	private String PayStation = null;//加油站位置

	public String getPayStation() {
		return PayStation;
	}

	public void setPayStation(String payStation) {
		PayStation = payStation;
	}

	public OrderBean() {

	}

	public OrderBean(int order_id, String gasQuality, int gasQuantity,
					 double gasPrice, String time, String license, String payState,
					 String payStation) {
		this.order_id = order_id;
		GasQuality = gasQuality;
		GasQuantity = gasQuantity;
		GasPrice = gasPrice;
		this.time = time;
		License = license;
		PayState = payState;
		PayStation = payStation;
	}

	public OrderBean(String gasQuality, int gasQuantity,
					 double gasPrice, String time, String license, String payState,
					 String payStation) {
		GasQuality = gasQuality;
		GasQuantity = gasQuantity;
		GasPrice = gasPrice;
		this.time = time;
		License = license;
		PayState = payState;
		PayStation = payStation;
	}

	public String getPayState() {
		return PayState;
	}

	public void setPayState(String payState) {
		PayState = payState;
	}

	/*
	 * 对应的getXX()和setXX()方法
	 */
	public int getOrder_id() {
		return order_id;
	}

	public void setOrder_id(int order_id) {
		this.order_id = order_id;
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

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getLicense() {
		return License;
	}

	public void setLicense(String license) {
		License = license;
	}

}
