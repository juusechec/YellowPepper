package com.yellowpepper.challenge.repository.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

public class Customer {
  @Id private Integer id;

  @Column("first_name")
  @JsonProperty(value = "first_name")
  private String firstName;

  @Column("second_name")
  @JsonProperty(value = "second_name")
  private String secondName;

  private String surname;

  @Column("second_surname")
  @JsonProperty(value = "second_surname")
  private String secondSurname;

  public Customer() {
    // Do nothing
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getSecondName() {
    return secondName;
  }

  public void setSecondName(String secondName) {
    this.secondName = secondName;
  }

  public String getSurname() {
    return surname;
  }

  public void setSurname(String surname) {
    this.surname = surname;
  }

  public String getSecondSurname() {
    return secondSurname;
  }

  public void setSecondSurname(String secondSurname) {
    this.secondSurname = secondSurname;
  }
}
