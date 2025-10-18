package com.pay.comsumers;

import org.jboss.logging.Logger;

import com.pay.models.NotificationEntity;
import com.pay.services.NotificationService;

import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.common.annotation.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class TransferConsumer {

    private static final Logger LOG = Logger.getLogger(TransferConsumer.class);
    public static final String TRANSFER_PROCESSOR_ADDRESS = "transfer.process";

    @Inject
    NotificationService notificationService;

    @ConsumeEvent(TRANSFER_PROCESSOR_ADDRESS)
    @Blocking
    @Transactional
    public String processTransfer(String idTransaction) {
        LOG.info("Event Bus: Recebida requisição para processar transferência: " 
                 + idTransaction);
        
        try {
            NotificationEntity notificationEntity = notificationService.create(idTransaction); 

            notificationService.send(notificationEntity);
            LOG.info("Event Bus: Transferência processada com sucesso. ID: " + idTransaction);
            
            return "OK";
            
        } catch (Exception e) {
            LOG.error("Event Bus: Falha ao processar transferência.", e);
            return "Falha Interna: " + e.getMessage();
        }
    }
}