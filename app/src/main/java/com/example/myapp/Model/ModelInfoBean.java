package com.example.myapp.Model;

/*
 * ���Ƕ�ӦƷ���µ��ͺű��Ӧ����
 * */
public class ModelInfoBean {

	private int id = 0;
	private String Model = null; // �����ͺ�
	private String Level = null; // ������
	private int GasVolume = 0; // �����С
	private int brand_id = 0; // ����Ʒ��

	/*
	 * ��Ӧ��getXX(), setXX()��С
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	public String getModel() {
		return Model;
	}

	public void setModel(String model) {
		Model = model;
	}

	public String getLevel() {
		return Level;
	}

	public void setLevel(String level) {
		Level = level;
	}

	public int getGasVolume() {
		return GasVolume;
	}

	public void setGasVolume(int gasVolume) {
		GasVolume = gasVolume;
	}

	public int getBrand_id() {
		return brand_id;
	}

	public void setBrand_id(int brand_id) {
		this.brand_id = brand_id;
	}
}
