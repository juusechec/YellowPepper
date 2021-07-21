package com.yellowpepper.challenge.service.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Rates {
  @JsonProperty(value = "CAD")
  private Double cad;

  public Double getCad() {
    return cad;
  }

  public void setCad(Double cad) {
    this.cad = cad;
  }
}
