package com.sonatype.blametest;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;

public class JacksonObjectMapper implements ObjectMapper
{
  com.fasterxml.jackson.databind.ObjectMapper mapper
      = new com.fasterxml.jackson.databind.ObjectMapper();

  public String writeValue(Object value) {
    try {
      return mapper.writeValueAsString(value);
    } catch (JsonProcessingException jsonExcp) {
      throw new RuntimeException(jsonExcp);
    }
  }

  public <T> T readValue(String value, Class<T> valueType) {
    try {
      return mapper.readValue(value, valueType);
    } catch (IOException ioExcp) {
      throw new RuntimeException(ioExcp);
    }
  }
}
