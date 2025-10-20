package com.pay.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.pay.models.NotificationEntity;
import com.pay.resources.clients.NotificationClient;

import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

@QuarkusTest
public class NotificationServiceTest {

    @Inject
    private NotificationService service;

    @Inject
    NotificationClient mockNotificationClient;
    
    private final String VALID_TRANSACTION_TRANFER_ID = "transac004-4126-4ca4-a412-000000000002";
    private final String INVALID_TRANSACTION_ID = "transac999-4126-4ca4-a412-307c25d64499";
    private final String PAYEE_EMAIL = "maria@example.com";
    private final String PAYEE_EMAIL_VALID = "";
    private final String PAYEE_EMAIL_TRANSFER = "danilo@silva.com";

    @Test
    @DisplayName("CREATE: Deve criar a entidade NotificationEntity com sucesso")
    @TestTransaction
    void testCreate_Success() {
        NotificationEntity notification = service.create(VALID_TRANSACTION_TRANFER_ID);

        assertNotNull(notification.getId(), "A notificação deve ter um ID gerado.");
        assertEquals(PAYEE_EMAIL_TRANSFER, notification.getEmail(), "O email deve ser o do usuário de destino.");
        assertFalse(notification.getSend(), "O flag 'send' deve ser FALSE inicialmente.");
        assertEquals(1, NotificationEntity.count(), "Deve haver uma notificação persistida.");
            }

    @Test
    @DisplayName("CREATE: Deve lançar RuntimeException se a Transação não for encontrada")
    void testCreate_TransactionNotFound() {
        assertThrows(RuntimeException.class, () -> {
            service.create(INVALID_TRANSACTION_ID);
        }, "Deve lançar exceção para transação inexistente.");
        
        assertEquals(0, NotificationEntity.count(), "Nenhuma notificação deve ser persistida.");
    }

    @Test
    @DisplayName("SEND: Deve enviar a notificação e atualizar o status para TRUE")
    @TestTransaction
    void testSend_Success() {
        NotificationEntity unsentNotification = new NotificationEntity();
        unsentNotification.setEmail(PAYEE_EMAIL);
        unsentNotification.setMessage("Teste de envio");
        unsentNotification.setSend(false);
        unsentNotification.persistAndFlush();

        service.send(unsentNotification);

        unsentNotification = NotificationEntity.findById(unsentNotification.getId());

        assertTrue(unsentNotification.getSend(), "O status 'send' deve ser TRUE após o envio.");
    }

    @Test
    @DisplayName("SEND: Deve falhar o envio e manter o status Send como FALSE, atualizando o UpdatedAt")
    @TestTransaction
    void testSend_Failure() {
        NotificationEntity unsentNotification = new NotificationEntity();
        unsentNotification.setEmail(PAYEE_EMAIL_VALID);
        unsentNotification.setMessage("Teste de falha");
        unsentNotification.setSend(false);
        unsentNotification.setUpdatedAt(LocalDateTime.of(2025, 1, 1, 0, 0));
        unsentNotification.persistAndFlush();
        
        service.send(unsentNotification);

        assertFalse(unsentNotification.getSend(), "O status 'send' deve permanecer FALSE após a falha.");
    }
    
}
