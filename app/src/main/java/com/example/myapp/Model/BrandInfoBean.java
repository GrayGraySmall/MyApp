package com.example.myapp.Model;

/*
 *����������Ʒ���� 
 * Ʒ��id����
 * model_info_id Ϊ�ͺŵı�ţ��ڸ�ֵʱ���п��ƣ������ݿ�����һ�����������ModelInfoBean��
 * */

public class BrandInfoBean {
	private int brand_id;
	private String BrandName = null; // Ʒ����
	private String Brandsign = null; // Ʒ��ͼ�꣨�������һ����ַ��
	/*
	 * ��Ӧ��getXX() setXX()����
	 */
	public void setBrand_id(int brand_id) {
		this.brand_id = brand_id;
	}

	public int getBrand_id() {
		return brand_id;
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
