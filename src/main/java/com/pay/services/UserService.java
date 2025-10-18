package com.pay.services;

import java.math.BigDecimal;
import java.util.List;

import com.pay.mappers.UserMapper;
import com.pay.models.AccountEntity;
import com.pay.models.UserEntity;
import com.pay.resources.requests.UserRequest;
import com.pay.resources.responses.UserResponse;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class UserService {
    @Inject
    UserMapper mapper;

    public List<UserResponse> getAll(){
        return mapper.toDTOList(UserEntity.listAll());
    }

    public UserResponse getById(Long id){
        return mapper.toDTO(UserEntity.findById(id));
    }

    @Transactional
    public Long create(UserRequest request){
        UserEntity entity = mapper.toEntity(request);
        entity.persistAndFlush();
        AccountEntity accountEntity  = new AccountEntity();
        accountEntity.setUser(entity);
        accountEntity.setBalance(BigDecimal.ZERO);
        accountEntity.persistAndFlush();
        return entity.getId();
    }

    @Transactional
    public void update(UserRequest request, Long id){
        UserEntity entity = UserEntity.findById(id);
        entity.setName(request.getName());
        entity.setEmail(request.getEmail());
        entity.setType(request.getTypeCode());
        entity.persistAndFlush();
    }

    @Transactional
    public void delete(Long id){
        UserEntity user = UserEntity.findById(id);
        user.delete();
    } 
    
}
