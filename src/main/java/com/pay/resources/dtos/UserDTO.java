package com.pay.resources.dtos;

import com.pay.models.enums.UserType;

public class UserDTO {
    public String id;
    public String name;
    public String email;
    public Integer typeCode;
    public String typeDescription;

    public UserDTO() {
    }

    public UserDTO(String id, String name, String email, UserType userType) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.typeCode = userType.getCode();
        this.typeDescription = userType.getDescription();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

}
