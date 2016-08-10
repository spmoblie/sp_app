package com.spshop.stylistpark.entity;

import java.io.Serializable;

public class BaseEntity implements Serializable {

    private static final long serialVersionUID = 2980439304361030908L;

    private int errCode;
    private String errInfo;
    private String data;


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

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

}
