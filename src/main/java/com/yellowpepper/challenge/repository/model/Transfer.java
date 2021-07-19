package com.yellowpepper.challenge.repository.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transfer {
  @Id private Integer id;

  @Column("id_origin_account")
  private Integer idOriginAccount;

  @Column("id_destination_account")
  private Integer idDestinationAccount;

  private BigDecimal amount;

  private BigDecimal tax;

  @Column("id_currency")
  private Integer idCurrency;

  private LocalDateTime datetime;

  private String status;

  public Transfer() {
    // Do nothing
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Integer getIdOriginAccount() {
    return idOriginAccount;
  }

  public void setIdOriginAccount(Integer idOriginAccount) {
    this.idOriginAccount = idOriginAccount;
  }

  public Integer getIdDestinationAccount() {
    return idDestinationAccount;
  }

  public void setIdDestinationAccount(Integer idDestinationAccount) {
    this.idDestinationAccount = idDestinationAccount;
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

  public LocalDateTime getDatetime() {
    return datetime;
  }

  public void setDatetime(LocalDateTime datetime) {
    this.datetime = datetime;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public BigDecimal getTax() {
    return tax;
  }

  public void setTax(BigDecimal tax) {
    this.tax = tax;
  }
}
