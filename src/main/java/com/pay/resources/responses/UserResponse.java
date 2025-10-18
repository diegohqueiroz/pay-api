package com.pay.resources.responses;

import com.pay.models.enums.UserType;

public class UserResponse {
    public Long id;
    public String name;
    public String email;
    public String document;
    public Integer typeCode;
    public String typeDescription;

    public UserResponse() {
    }

    public UserResponse(Long id, String name, String email, String document, UserType userType) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.document = document;
        this.typeCode = userType.getCode();
        this.typeDescription = userType.getDescription();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getTypeDescription() {
        return typeDescription;
    }

    public String getDocument() {
        return document;
    }

}
