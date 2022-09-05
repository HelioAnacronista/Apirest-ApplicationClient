package io.github.helio.rest;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;


public class ApiErrors {

    @Getter
    private List<String> errors;

    public ApiErrors(String msgErro) {
        this.errors = Arrays.asList(msgErro);
    }
}
