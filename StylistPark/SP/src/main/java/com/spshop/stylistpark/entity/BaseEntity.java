package com.spshop.stylistpark.entity;

import java.io.Serializable;

public class BaseEntity implements Serializable {

    private static final long serialVersionUID = 2980439304361030908L;

    private int errCode; //响应状态码
    private String errInfo; //状态码描述

    private int pageSize; //每页加载数量
    private int dataTotal; //加载数据总量


    public BaseEntity() {
        super();
    }


    public BaseEntity(int errCode, String errInfo) {
        super();
        this.errCode = errCode;
        this.errInfo = errInfo;
    }

    public String getEntityId() {
        return "";
    };

    public int getErrCode() {
        return errCode;
    }

    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }

    public String getErrInfo() {
        return errInfo;
    }

    public void setErrInfo(String errInfo) {
        this.errInfo = errInfo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getDataTotal() {
        return dataTotal;
    }

    public void setDataTotal(int dataTotal) {
        this.dataTotal = dataTotal;
    }

    @Override
    public String toString() {
        return "BaseEntity{" +
                "errCode=" + errCode +
                ", errInfo='" + errInfo + '\'' +
                ", pageSize=" + pageSize +
                ", dataTotal=" + dataTotal +
                '}';
    }
}
