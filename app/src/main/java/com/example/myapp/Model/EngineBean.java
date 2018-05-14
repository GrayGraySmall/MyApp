package com.example.myapp.Model;

/*
 * ����һ��������
 * engineNum��ΪCar�����
 * engineState������״̬
 */
public class EngineBean {
	private String engineNum = null;		//����ı��
	private String engineState = null;		//������״̬

	/*
	 * ����getXX(),setXX()����
	 */
	public String getEngineNum() {
		return engineNum;
	}

	public void setEngineNum(String engineNum) {
		this.engineNum = engineNum;
	}

	public String getEngineState() {
		return engineState;
	}

	public void setEngineState(String engineState) {
		this.engineState = engineState;
	}
}
