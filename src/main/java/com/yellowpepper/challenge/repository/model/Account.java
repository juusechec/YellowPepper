package com.yellowpepper.challenge.repository.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import java.math.BigDecimal;

public class Account {
  @Id private Integer id;

  @Column("id_holder")
  @JsonProperty(value = "id_holder")
  private Integer idHolder;

  private BigDecimal amount;

  @Column("id_currency")
  @JsonProperty(value = "id_currency")
  private Integer idCurrency;

  public Account() {
    // Do nothing
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Integer getIdHolder() {
    return idHolder;
  }

  public void setIdHolder(Integer idHolder) {
    this.idHolder = idHolder;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public Integer getIdCurrency() {
    return idCurrency;
  }

  public void setIdCurrency(Integer idCurrency) {
    this.idCurrency = idCurrency;
  }

  @Override
  public String toString() {
    return "Account{"
        + "id="
        + id
        + ", idHolder="
        + idHolder
        + ", amount="
        + amount
        + ", idCurrency="
        + idCurrency
        + '}';
  }
}
