package com.pay.services;

import java.time.LocalDateTime;

import org.jboss.logging.Logger;

import com.pay.models.NotificationEntity;
import com.pay.models.TransactionEntity;
import com.pay.resources.NotificationClient;
import com.pay.resources.requests.NotificationRequest;

import io.vertx.core.eventbus.EventBus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class NotificationService {
    private static final Logger LOG = Logger.getLogger(NotificationService.class);

    @Inject
    EventBus bus;

    @Inject
    NotificationClient notificationClient;

    public NotificationEntity create(String idTransaction){
        TransactionEntity entity = TransactionEntity.findById(idTransaction);
        if (entity == null) {
            
        }
        NotificationEntity notificationEntity = new NotificationEntity();
        notificationEntity.setCreatedAt(LocalDateTime.now());
        notificationEntity.setUpdatedAt(LocalDateTime.now());
        notificationEntity.setSend(false);
        notificationEntity.setEmail(entity.getAccountDestination().getUser().getEmail());
        notificationEntity.setEmail(entity.getAccountDestination().getUser().getEmail());
        notificationEntity.setTitle("Transferência recebida!");
        notificationEntity.setMessage(String.format("""
            ==============================================
            Transferencia de : %f
            Enviado por: %s
            Recebida com sucesso!
            ==============================================
            Recebido em: %s
            ==============================================
            """, entity.getValue(), entity.getAccountSource().getUser().getName(), entity.getCreatedAt()));
        notificationEntity.persistAndFlush();
        LOG.info("Criando notificação:" + notificationEntity.getId());
        return notificationEntity;
    }

    public void send(NotificationEntity entity){
        try {
            LOG.info("Enviando notificação:" + entity.getId());
            NotificationRequest notificationRequest = new NotificationRequest(entity.getEmail(), entity.getMessage());
            notificationClient.notify(notificationRequest);
            entity.setSend(true);
            entity.persistAndFlush();            
        } catch (RuntimeException e) {
            entity.setUpdatedAt(LocalDateTime.now());
            entity.persistAndFlush();            
        }
    }

}
