package com.example.myapp.Model;

/*
 * ���������࣬�������������˽������
 * �Լ���Ӧ��get(), set()����
 * */
public class CarBean {
	private String License = null; // ���ƺ�
	private String TranState = null; // ������״��
	private String LightState = null; // ����״��
	private double CurGas = 0; // ��ǰ����
	private int Curdis = 0; // ��ʻ���
	private String name = null; // ����������users�����
	private String engineNum = null; // �����ţ�engine�����
	private int brand_id = 0; // ��Ӧ��Ʒ�Ʊ�ţ�brand�����
	private int model_id = 0;

	/*
	 * ����getXX() �� setXX()����
	 */

	public int getModel_id() {
		return model_id;
	}

	public void setModel_id(int model_id) {
		this.model_id = model_id;
	}

	public String getLicense() {
		return License;
	}

	public void setLicense(String license) {
		License = license;
	}

	public String getTranState() {
		return TranState;
	}

	public void setTranState(String tranState) {
		TranState = tranState;
	}

	public String getLightState() {
		return LightState;
	}

	public void setLightState(String lightState) {
		LightState = lightState;
	}

	public double getCurGas() {
		return CurGas;
	}

	public void setCurGas(double curGas) {
		CurGas = curGas;
	}

	public int getCurdis() {
		return Curdis;
	}

	public void setCurdis(int curdis) {
		Curdis = curdis;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEngineNum() {
		return engineNum;
	}

	public void setEngineNum(String engineNum) {
		this.engineNum = engineNum;
	}

	public int getBrand_id() {
		return brand_id;
	}

	public void setBrand_id(int brand_id) {
		this.brand_id = brand_id;
	}

}
