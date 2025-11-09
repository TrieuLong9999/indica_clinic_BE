package com.clinic.indica.service;

import com.clinic.indica.dto.AuthResponse;
import com.clinic.indica.dto.LoginRequest;
import com.clinic.indica.dto.RefreshTokenRequest;

public interface AuthService {
    AuthResponse login(LoginRequest loginRequest);
    AuthResponse refreshToken(RefreshTokenRequest refreshTokenRequest);
}
