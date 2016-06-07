package com.spshop.stylistpark.entity;

public class UpdateVersionEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	private String description; //版本更新描绘
	private String version; //版本号
	private String url;
	private boolean force; //是否强制更新

	
	public UpdateVersionEntity() {
		super();
	}

	
	public UpdateVersionEntity(int errCode, String errInfo) {
		super(errCode, errInfo);
	}

	
	public UpdateVersionEntity(int errCode, String errInfo,
			String version, String url, boolean force) {
		super(errCode, errInfo);
		this.version = version;
		this.url = url;
		this.force = force;
	}

	
	public UpdateVersionEntity(int errCode, String errInfo,
			String description, String version, String url, boolean force) {
		super(errCode, errInfo);
		this.description = description;
		this.version = version;
		this.url = url;
		this.force = force;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public boolean isForce() {
		return force;
	}

	public void setForce(boolean force) {
		this.force = force;
	}

}
