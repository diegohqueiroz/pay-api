package com.pay.mappers;

import com.pay.models.UserEntity;
import com.pay.models.enums.UserType;
import com.pay.resources.requests.UserRequest;
import com.pay.resources.responses.UserResponse;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserMapper implements Mapper<UserEntity, UserRequest, UserResponse> {

    @Override
    public UserResponse toDTO(UserEntity entity) {
        if (entity == null) {
            return null;
        }
        UserType userType = UserType.toEnum(entity.getType());
        
        return new UserResponse(
            entity.getId(),
            entity.getName(),
            entity.getEmail(),
            entity.getDocument(),
            userType
        );
    }

    @Override
    public UserEntity toEntity(UserRequest request) {
        if (request == null) {
            return null;
        }
        
        UserEntity entity = new UserEntity();
        entity.setName(request.getName());
        entity.setEmail(request.getEmail());
        entity.setType(request.getTypeCode());
        return entity;
    }
}