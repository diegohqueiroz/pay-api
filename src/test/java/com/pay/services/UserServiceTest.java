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
import com.pay.resources.dtos.UserDTO;

import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@QuarkusTest
public class UserServiceTest {
    private final String USER_ID_DELETAR = "user004-4126-4ca4-a412-307c25d64499";
    private final String USER_ID_ATUALIZAR = "user003-4126-4ca4-a412-307c25d64499";

    @Inject
    UserService userService;

    @Inject
    UserMapper mapper; 

    private UserDTO baseUserDTO;

    @BeforeEach
    @Transactional
    void setUp() {
        // UserEntity.deleteAll();

        baseUserDTO = new UserDTO(null, "Test User", "test@user.com", UserType.SIMPLE);
    }

    @Test
    @Transactional
    void testCreateAndGetById() {
        String id = userService.create(baseUserDTO);

        assertNotNull(id, "O ID deve ser gerado pelo sistema.");
        UserEntity entity = UserEntity.findById(id);
        
        assertNotNull(entity);
        assertEquals(id, entity.getId());
        assertEquals("Test User", entity.getName());
    }

    @Test
    @TestTransaction
    void testGetAll() {
        List<UserDTO> users = userService.getAll();
        assertEquals(5, users.size());
        
        userService.create(baseUserDTO);
        
        users = userService.getAll();
        
        assertNotNull(users);
        assertEquals(6, users.size());
    }

    @Test
    @TestTransaction
    void testUpdate() {
        UserDTO updateDTO = new UserDTO(USER_ID_ATUALIZAR, "Updated Name", "new@email.com", UserType.SIMPLE);
        
        userService.update(updateDTO, USER_ID_ATUALIZAR);
        UserEntity entity = UserEntity.findById(USER_ID_ATUALIZAR);
        
        assertEquals("Updated Name", entity.getName());
        assertEquals("new@email.com", entity.getEmail());
    }
    
    @Test
    @TestTransaction
    void testDelete() {
        UserDTO userDTO = userService.getById(USER_ID_DELETAR);
        assertNotNull(userDTO);
        userService.delete(USER_ID_DELETAR);

        UserDTO userDTOAposDelecao = userService.getById(USER_ID_DELETAR);
        assertNull(userDTOAposDelecao, "O usuário deve ser null após a exclusão.");
    }
}