package com.pay.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.UuidGenerator;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.smallrye.common.constraint.NotNull;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "transactions")
public class TransactionEntity extends PanacheEntityBase {
    @Id
    @UuidGenerator
    private String id;
    @NotNull
    private BigDecimal value;
    @ManyToOne
    @JoinColumn(name = "account_source_id")
    private AccountEntity accountSource;
    @ManyToOne
    @JoinColumn(name = "account_destination_id")
    private AccountEntity accountDestination;
    @NotNull
    private LocalDateTime createdAt;
    @NotNull
    private Integer type;

    public TransactionEntity() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public AccountEntity getAccountSource() {
        return accountSource;
    }

    public void setAccountSource(AccountEntity accountSource) {
        this.accountSource = accountSource;
    }

    public AccountEntity getAccountDestination() {
        return accountDestination;
    }

    public void setAccountDestination(AccountEntity accountDestination) {
        this.accountDestination = accountDestination;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
