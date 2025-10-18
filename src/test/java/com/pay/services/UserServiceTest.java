package com.pay.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.pay.mappers.UserMapper;
import com.pay.models.UserEntity;
import com.pay.models.enums.UserType;
import com.pay.resources.requests.UserRequest;
import com.pay.resources.responses.UserResponse;

import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@QuarkusTest
public class UserServiceTest {
    private final Long USER_ID_DELETAR = 4L;
    private final Long USER_ID_ATUALIZAR = 3l;

    @Inject
    UserService userService;

    @Inject
    UserMapper mapper; 

    private UserRequest request;

    @BeforeEach
    @Transactional
    void setUp() {

        request = new UserRequest("Test User", "test@user.com", "12345678900",UserType.GENERAL);
    }

    @Test
    @Transactional
    void testCreateAndGetById() {
        Long id = userService.create(request);

        assertNotNull(id, "O ID deve ser gerado pelo sistema.");
        UserEntity entity = UserEntity.findById(id);
        
        assertNotNull(entity);
        assertEquals(id, entity.getId());
        assertEquals("Test User", entity.getName());
    }

    @Test
    @TestTransaction
    void testGetAll() {
        List<UserResponse> users = userService.getAll();
        assertEquals(5, users.size());
        
        userService.create(request);
        
        users = userService.getAll();
        
        assertNotNull(users);
        assertEquals(6, users.size());
    }

    @Test
    @TestTransaction
    void testUpdate() {
        UserRequest request = new UserRequest("Updated Name", "new@email.com", "12345678900", UserType.GENERAL);
        
        userService.update(request, USER_ID_ATUALIZAR);
        UserEntity entity = UserEntity.findById(USER_ID_ATUALIZAR);
        
        assertEquals("Updated Name", entity.getName());
        assertEquals("new@email.com", entity.getEmail());
    }
    
    @Test
    @TestTransaction
    void testDelete() {
        UserResponse userDTO = userService.getById(USER_ID_DELETAR);
        assertNotNull(userDTO);
        userService.delete(USER_ID_DELETAR);

        UserResponse userDTOAposDelecao = userService.getById(USER_ID_DELETAR);
        assertNull(userDTOAposDelecao, "O usuário deve ser null após a exclusão.");
    }
}