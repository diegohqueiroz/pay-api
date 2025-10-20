package com.pay.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.pay.exceptions.TransactionException;
import com.pay.models.AccountEntity;
import com.pay.models.TransactionEntity;
import com.pay.models.UserEntity;
import com.pay.models.enums.TransactionType;
import com.pay.models.enums.UserType;
import com.pay.resources.requests.MovimentRequest;
import com.pay.resources.requests.TransferRequest;

import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

@QuarkusTest
public class TransactionServiceTest {
    @Inject
    private TransactionService service;

    private TransferRequest validRequest;
    private TransferRequest invalidRequest;
    private AccountEntity payerAccount;
    private AccountEntity payeeAccount;
    private UserEntity payerUser;
    private UserEntity payeeUser;
    private MovimentRequest validMovimentRequest;
    private MovimentRequest invalidMovimentRequest;

    @BeforeEach
    void setUp() {
        payerUser = new UserEntity();
        payerUser.setId(10L);
        payerUser.setType(UserType.GENERAL.getCode());
        
        payeeUser = new UserEntity();
        payeeUser.setId(20L);
        payeeUser.setType(UserType.GENERAL.getCode());
        
        payerAccount = new AccountEntity();
        payerAccount.setBalance(new BigDecimal("500.00"));
        payerAccount.setUser(payerUser);

        payeeAccount = new AccountEntity();
        payeeAccount.setBalance(new BigDecimal("100.00"));
        payeeAccount.setUser(payeeUser);
        
        validRequest = new TransferRequest();
        validRequest.setPayer(1L);
        validRequest.setPayee(2L);
        validRequest.setValue(new BigDecimal("150.00"));

        invalidRequest = new TransferRequest();
        invalidRequest.setPayer(99L);
        invalidRequest.setPayee(10L);
        invalidRequest.setValue(new BigDecimal("10.00"));

        validMovimentRequest = new MovimentRequest(new BigDecimal("100.00"), 1);

        invalidMovimentRequest = new MovimentRequest(new BigDecimal("10.00"), 99);
    }

    @Test
    @DisplayName("Deve subtrair o valor corretamente do saldo (Débito)")
    void testDebitAccount() {
        BigDecimal balance = new BigDecimal("500.00");
        BigDecimal debitValue = new BigDecimal("150.00");
        BigDecimal expected = new BigDecimal("350.00");
        
        BigDecimal actual = service.debitAccount(debitValue, balance);
        
        assertEquals(expected, actual, "O saldo final deve ser o resultado da subtração.");
    }

    @Test
    @DisplayName("Deve somar o valor corretamente ao saldo (Crédito)")
    void testCreditAccount() {
        BigDecimal balance = new BigDecimal("100.00");
        BigDecimal creditValue = new BigDecimal("150.00");
        BigDecimal expected = new BigDecimal("250.00");
        
        BigDecimal actual = service.creditAccount(creditValue, balance);
        
        assertEquals(expected, actual, "O saldo final deve ser o resultado da soma.");
    }
    
    @Test
    @DisplayName("Deve gerar a entidade TransactionEntity corretamente")
    void testGenerateTransaction() {
        LocalDateTime beforeCall = LocalDateTime.now();
        
        TransactionEntity entity = service.generateTransaction(payerAccount, payeeAccount, validRequest.getValue(), TransactionType.TRANSFER);
        
        assertNotNull(entity, "A entidade não deve ser nula.");
        assertEquals(payerAccount, entity.getAccountSource(), "Conta de origem deve ser a correta.");
        assertEquals(payeeAccount, entity.getAccountDestination(), "Conta de destino deve ser a correta.");
        assertEquals(validRequest.getValue(), entity.getValue(), "O valor deve ser o da requisição.");
        assertEquals(TransactionType.TRANSFER.getCode(), entity.getType(), "O tipo deve ser TRANSFER.");
        
        assertNotNull(entity.getCreatedAt(), "A data de criação não deve ser nula.");
        assertTrue(entity.getCreatedAt().isAfter(beforeCall) || entity.getCreatedAt().isEqual(beforeCall), 
                   "A data de criação deve ser aproximadamente o momento da chamada.");
    }

    @Test
    @DisplayName("Validação deve passar com dados válidos")
    void testValidate_Success() {
        try {
            service.validate(validRequest, Optional.of(payerAccount), Optional.of(payeeAccount));
            assertTrue(true);
        } catch (TransactionException e) {
            fail("Não deveria ter lançado exceção com dados válidos: " + e.getMessage());
        }
    }
    
    @Test
    @DisplayName("Deve falhar se a conta de origem for igual à conta de destino")
    void testValidate_SameAccount() {
        TransferRequest request = new TransferRequest();
        request.setPayer(10L);
        request.setPayee(10L);
        request.setValue(new BigDecimal("10.00"));
        
        TransactionException exception = assertThrows(TransactionException.class, () -> {
            service.validate(request, Optional.of(payerAccount), Optional.of(payerAccount));
        });
        
        assertEquals("Não é possivel transferir para mesma conta", exception.getMessage());
    }

    @Test
    @DisplayName("Deve falhar se a conta de origem não for encontrada")
    void testValidate_SourceAccountNotFound() {
        TransactionException exception = assertThrows(TransactionException.class, () -> {
            service.validate(validRequest, Optional.empty(), Optional.of(payeeAccount));
        });
        
        assertEquals("Conta de origem não encontrada", exception.getMessage());
    }

    @Test
    @DisplayName("Deve falhar se a conta de destino for nula")
    void testValidate_DestinationAccountNull() {
        TransactionException exception = assertThrows(TransactionException.class, () -> {
            service.validate(validRequest, Optional.of(payerAccount), null);
        });
        
        assertEquals("Conta de destino não encontrada", exception.getMessage());
    }
    
    @Test
    @DisplayName("Deve falhar se o saldo for insuficiente")
    void testValidate_InsufficientBalance() {
        validRequest.setValue(new BigDecimal("600.00")); // Saldo é 500.00
        
        TransactionException exception = assertThrows(TransactionException.class, () -> {
            service.validate(validRequest, Optional.of(payerAccount), Optional.of(payeeAccount));
        });
        
        assertEquals("Saldo insuficiente", exception.getMessage());
    }

    @Test
    @DisplayName("Deve falhar se o usuário pagador for do tipo COMPANY")
    void testValidate_PayerIsCompany() {
        payerUser.setType(UserType.COMPANY.getCode());
        
        TransactionException exception = assertThrows(TransactionException.class, () -> {
            service.validate(validRequest, Optional.of(payerAccount), Optional.of(payeeAccount));
        });
        
        assertEquals("Conta não pode realizar transferencia", exception.getMessage());
    }
    
    @Test
    @DisplayName("Autorização deve falhar quando AutorizationClient lança WebApplicationException")
    void testAutorization_Failure() {
        TransactionException exception = assertThrows(TransactionException.class, () -> {
            service.autorization(invalidRequest.getPayer());
        });

        assertEquals("Transferencia não autorizada", exception.getMessage());
    }
    
    @Test
    @DisplayName("Autorização deve passar quando AutorizationClient não lança exceção")
    void testAutorization_Success() {
        try {
            service.autorization(validRequest.getPayer());
            assertTrue(true, "Não deveria ter lançado exceção de autorização.");
        } catch (TransactionException e) {
            fail("Deveria ter passado na autorização: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("DEBIT: Deve processar o débito e atualizar o saldo com sucesso")
    @TestTransaction
    void testDebit_Success() {
        String transactionId = service.debit(validMovimentRequest);

        assertNotNull(transactionId, "O ID da transação não deve ser nulo");
        
        AccountEntity payer = AccountEntity.find("user.id", 1).firstResult();
        assertEquals(new BigDecimal("900.00"), payer.getBalance(), "O saldo final da conta deve estar correto.");
        
        assertEquals(7, TransactionEntity.count(), "Deve haver uma transação persistida.");
    }
    
    @Test
    @DisplayName("DEBIT: Deve falhar se a conta não for encontrada")
    void testDebit_AccountNotFound() {
        
        TransactionException exception = assertThrows(TransactionException.class, () -> {
            service.debit(invalidMovimentRequest);
        });
        
        assertEquals("Conta de origem não encontrada", exception.getMessage());
    }

    @Test
    @DisplayName("DEBIT: Deve falhar se o saldo for insuficiente")
    void testDebit_InsufficientBalance() {
        validMovimentRequest.setValue(new BigDecimal("6000.00"));
        
        TransactionException exception = assertThrows(TransactionException.class, () -> {
            service.debit(validMovimentRequest);
        });
        
        assertEquals("Saldo insuficiente", exception.getMessage());
    }
    
    @Test
    @DisplayName("CREDIT: Deve processar o crédito e atualizar o saldo com sucesso")
    @TestTransaction
    void testCredit_Success() {
        String transactionId = service.credit(validMovimentRequest);

        assertNotNull(transactionId, "O ID da transação não deve ser nulo");
        
        AccountEntity payer = AccountEntity.find("user.id", 1).firstResult();
        
        assertEquals(new BigDecimal("1100.00"), payer.getBalance(), "O saldo final está correto.");

        assertEquals(7, TransactionEntity.count(), "Deve haver uma transação persistida.");//Já existem 5 na carga do banco
    }

    @Test
    @DisplayName("TRANSFER: Deve iniciar a transferência, persistir e publicar no EventBus")
    @TestTransaction
    void testTransfer_Flow() {
        String transactionId = service.transfer(validRequest);
        
        assertNotNull(transactionId, "O ID da transação não deve ser nulo");
    }
    
    @Test
    @DisplayName("TRANSFER PERSISTENCE: Deve concluir a transferência e atualizar saldos")
    @TestTransaction
    void testTransferPersistence_Success() {
        AccountEntity payer = AccountEntity.find("user.id", validRequest.getPayer()).firstResult();
        AccountEntity payee = AccountEntity.find("user.id", validRequest.getPayee()).firstResult();
        
        assertEquals(new BigDecimal("1000.00"), payer.getBalance(), "O saldo inicial carregado no banco.");
        assertEquals(new BigDecimal("2000.00"), payee.getBalance(), "O saldo inicial carregado no banco.");

        String transactionId = service.transferPersistence(validRequest);

        assertNotNull(transactionId, "O ID da transação não deve ser nulo");
        
        payer = AccountEntity.find("user.id", validRequest.getPayer()).firstResult();
        payee = AccountEntity.find("user.id", validRequest.getPayee()).firstResult();
        
        assertEquals(new BigDecimal("850.00"), payer.getBalance(), "O saldo final do Payer deve estar correto.");
        
        assertEquals(new BigDecimal("2150.00"), payee.getBalance(), "O saldo final do Payee deve estar correto");

        assertEquals(7, TransactionEntity.count(), "Deve haver uma transação persistida.");//Já tem 5 na carga do banco de dados
    }

    @Test
    @DisplayName("validateMoviment: Deve falhar se a conta não for encontrada")
    void testValidateMoviment_AccountNotFound() {
        AccountEntity nonExistingAccount = null;
        
        TransactionException exception = assertThrows(TransactionException.class, () -> {
            service.validateMoviment(validMovimentRequest, Optional.ofNullable(nonExistingAccount), TransactionType.DEBIT);
        });
        
        assertEquals("Conta de origem não encontrada", exception.getMessage());
    }

    @Test
    @DisplayName("validateMoviment: Deve falhar se o saldo for insuficiente")
    void testValidateMoviment_InsufficientBalance() {
        validMovimentRequest.setValue(new BigDecimal("6000.00"));
        AccountEntity payer = AccountEntity.find("user.id", validMovimentRequest.getAccount()).firstResult();
        
        TransactionException exception = assertThrows(TransactionException.class, () -> {
            service.validateMoviment(validMovimentRequest, Optional.of(payer), TransactionType.DEBIT);
        });
        
        assertEquals("Saldo insuficiente", exception.getMessage());
    }

    @Test
    @DisplayName("validateMoviment: Validação deve passar com dados válidos")
    void testValidateMoviment_Success() {
        try {
            AccountEntity payer = AccountEntity.find("user.id", 1).firstResult();
            service.validateMoviment(validMovimentRequest, Optional.of(payer), TransactionType.DEBIT);
            assertTrue(true);
        } catch (TransactionException e) {
            fail("Não deveria ter lançado exceção com dados válidos: " + e.getMessage());
        }
    }
}
