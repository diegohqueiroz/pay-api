package com.pay.mappers;

import com.pay.models.UserEntity;
import com.pay.models.enums.UserType;
import com.pay.resources.dtos.UserDTO;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserMapper implements Mapper<UserEntity, UserDTO> {

    @Override
    public UserDTO toDTO(UserEntity entity) {
        if (entity == null) {
            return null;
        }
        UserType userType = UserType.toEnum(entity.getType());
        
        return new UserDTO(
            entity.getId(),
            entity.getName(),
            entity.getEmail(),
            userType
        );
    }

    @Override
    public UserEntity toEntity(UserDTO dto) {
        if (dto == null) {
            return null;
        }
        
        UserEntity entity = new UserEntity();
        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());
        entity.setType(dto.getTypeCode());
        return entity;
    }
}