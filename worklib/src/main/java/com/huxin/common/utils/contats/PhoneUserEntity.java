package com.huxin.common.utils.contats;

import com.huxin.common.entity.IEntity;

public class PhoneUserEntity implements IEntity {
	private static final long serialVersionUID = 1L;
	private String name = "";
	private String mobiles = "";
	private String email = "";
	private String address = "";
	private String organization = "";

	public PhoneUserEntity() {
	}

	public PhoneUserEntity(String address, String email, String mobiles, String name, String organization) {
		this.address = address;
		this.email = email;
		this.mobiles = mobiles;
		this.name = name;
		this.organization = organization;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobiles() {
		return mobiles;
	}

	public void setMobiles(String mobiles) {
		this.mobiles = mobiles;
	}

	public String getName() {
		return name;
	}

	public void setNames(String name) {
		this.name = name;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	@Override
	public String toString() {
		return "PhoneUserEntity{" +
				"address='" + address + '\'' +
				", name='" + name + '\'' +
				", mobiles='" + mobiles + '\'' +
				", email='" + email + '\'' +
				", organization='" + organization + '\'' +
				'}';
	}
}
