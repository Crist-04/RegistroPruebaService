package com.example.registropruebaservice.JPA;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;

public class Result {

    public boolean correct;
    public String errorMessage;
    public Object object;
    public List<Object> objects;
    public Object data;

    @JsonIgnore
    public Exception ex;
    @JsonIgnore
    public int status;

}
