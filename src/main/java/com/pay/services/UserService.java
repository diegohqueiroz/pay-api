package com.pay.services;

import java.util.List;

import com.pay.mappers.UserMapper;
import com.pay.models.UserEntity;
import com.pay.resources.dtos.UserDTO;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class UserService {
    @Inject
    UserMapper mapper;

    public List<UserDTO> getAll(){
        return mapper.toDTOList(UserEntity.listAll());
    }

    public UserDTO getById(String id){
        return mapper.toDTO(UserEntity.findById(id));
    }

    @Transactional
    public String create(UserDTO user){
        UserEntity entity = mapper.toEntity(user);
        entity.persistAndFlush();
        return entity.getId();
    }

    @Transactional
    public void update(UserDTO user, String id){
        UserEntity entity = UserEntity.findById(id);
        entity.setName(user.getName());
        entity.setEmail(user.getEmail());
        entity.setType(user.getTypeCode());
        entity.persistAndFlush();
    }

    @Transactional
    public void delete(String id){
        UserEntity user = UserEntity.findById(id);
        user.delete();
    } 
    
}
