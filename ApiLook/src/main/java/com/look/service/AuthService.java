
package com.look.service;

import com.look.exception.BadRequestException;

import com.look.dto.AuthLoginRequestDto;
import com.look.dto.AuthRegisterRequestDto;
import com.look.dto.AuthResponseDto;
import com.look.entity.User;

public interface AuthService {

    AuthResponseDto loginUser(AuthLoginRequestDto loginRequest);

    User registerUser(AuthRegisterRequestDto registerRequest) throws BadRequestException; 
}