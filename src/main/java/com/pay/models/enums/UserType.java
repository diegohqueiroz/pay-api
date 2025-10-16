package com.pay.models.enums;

public enum UserType {
    SIMPLE("Comum",1),
    COMPANY("Lojista",2);

    private String description;
    private Integer code;

    private UserType(String description, Integer code) {
        this.description = description;
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public Integer getCode() {
        return code;
    }

    public static UserType toEnum(Integer code) {
        if (code == null) {
            return null;
        }
        for (UserType userType : UserType.values()) {
            if (code.equals(userType.getCode())) {
                return userType;
            }
        }
        throw new IllegalArgumentException("Tipo de usuário inválido: " + code);
    }
}
