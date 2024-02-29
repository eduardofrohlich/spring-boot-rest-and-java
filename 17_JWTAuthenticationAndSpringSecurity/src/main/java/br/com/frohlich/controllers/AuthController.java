package br.com.frohlich.controllers;

import br.com.frohlich.data.vo.v1.security.AccountCredentialsVO;
import br.com.frohlich.services.AuthServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.IllegalFormatCodePointException;

@SuppressWarnings("rawtypes")
@Tag(name = "Authentication Endpoint")
@RestController
@RequestMapping("/auth")
public class AuthController {

    AuthServices authServices;

    //metodo para o login
    @Operation(summary = "Authenticates a user and returns a token")
    @PostMapping(value = "/signin")
    public ResponseEntity signin(@RequestBody AccountCredentialsVO data) {
        if (checkIfParamsIsNotNull(data))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request!");

        var token = authServices.signin(data);
        if (token == null)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid username/password supplied!");
        return token;
    }

    private static boolean checkIfParamsIsNotNull(AccountCredentialsVO data) {
        return data == null || data.getUsername() == null ||
                data.getUsername().isBlank() ||
                data.getPassword() == null || data.getPassword().isBlank();
    }

}
