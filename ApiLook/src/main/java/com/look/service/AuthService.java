// src/main/java/com/look/service/AuthService.java
package com.look.service;

import org.apache.coyote.BadRequestException; // Asegúrate que esta es la excepción correcta, ¿quizás com.look.exception.BadRequestException?

import com.look.dto.AuthLoginRequestDto;
import com.look.dto.AuthRegisterRequestDto;
import com.look.dto.AuthResponseDto;
import com.look.entity.User;

public interface AuthService {

    AuthResponseDto loginUser(AuthLoginRequestDto loginRequest);

    User registerUser(AuthRegisterRequestDto registerRequest) throws BadRequestException; // Ajusta la excepción si es necesario
}