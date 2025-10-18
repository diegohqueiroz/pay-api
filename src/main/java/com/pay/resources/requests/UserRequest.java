package com.pay.resources.requests;

import com.pay.models.enums.UserType;

public class UserRequest {
    public String name;
    public String email;
    public String document;
    public Integer typeCode;

    public UserRequest() {
    }

    public UserRequest(String name, String email, String document, UserType userType) {
        this.name = name;
        this.email = email;
        this.document = document;
        this.typeCode = userType.getCode();
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Integer getTypeCode() {
        return typeCode;
    }

    public String getDocument() {
        return document;
    }

}
