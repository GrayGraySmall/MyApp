package com.example.myapp.Model;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author HarveyShine 汽车详细信息类
 */
public class DetailBean implements Serializable {
	private String name = null;
	private String tel = null;
	private String username = null;
	private String LicenseNum = null; // 车牌号
	private String TranState = null;
	private String LightState = null;
	private double CurGas = 0; // 当前油量
	private int CurDis = 0; // 行驶里程
	private String EngineNum = null; // 引擎的编号
	private String EngineState = null; // 发动机状态
	private String Model = null; // 汽车型号
	private String Level = null; // 车身级别
	private int GasVolume = 0; // 油箱大小
	private String BrandName = null; // 品牌名
	private String Brandsign = null; // 品牌图标（保存的是一个地址）

	public DetailBean(String name, String tel, String username, String license,
					  String TranState, String lightState, double curGas, int curdis,
					  String engineNum, String engineState, String model, String level,
					  int gasVolume, String brandName, String brandsign) {
		super();
		this.name = name;
		this.tel = tel;
		this.username = username;
		LicenseNum = license;
		this.TranState = TranState;
		this.LightState = lightState;
		CurGas = curGas;
		this.CurDis = curdis;
		this.EngineNum = engineNum;
		this.EngineState = engineState;
		Model = model;
		Level = level;
		GasVolume = gasVolume;
		BrandName = brandName;
		Brandsign = brandsign;
	}

	public DetailBean() {

	}
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getLicenseNum() {
		return LicenseNum;
	}

	public void setLicenseNum(String license) {
		LicenseNum = license;
	}

	public String getTranState() {
		return TranState;
	}

	public void setTranState(String TranState) {
		this.TranState = TranState;
	}

	public String getLightState() {
		return LightState;
	}

	public void setLightState(String lightState) {
		this.LightState = lightState;
	}

	public double getCurGas() {
		return CurGas;
	}

	public void setCurGas(double curGas) {
		CurGas = curGas;
	}

	public int getCurdis() {
		return CurDis;
	}

	public void setCurdis(int curdis) {
		this.CurDis = curdis;
	}

	public String getEngineNum() {
		return EngineNum;
	}

	public void setEngineNum(String engineNum) {
		this.EngineNum = engineNum;
	}

	public String getEngineState() {
		return EngineState;
	}

	public void setEngineState(String engineState) {
		this.EngineState = engineState;
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

	public String getBrandName() {
		return BrandName;
	}

	public void setBrandName(String brandName) {
		BrandName = brandName;
	}

	public String getBrandsign() {
		return Brandsign;
	}

	public void setBrandsign(String brandsign) {
		Brandsign = brandsign;
	}

}
