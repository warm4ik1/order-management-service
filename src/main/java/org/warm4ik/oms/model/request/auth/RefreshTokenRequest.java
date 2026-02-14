package org.warm4ik.oms.model.request.auth;

import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

public record RefreshTokenRequest(@NotBlank(message = "RefreshToken is required") String refreshToken) implements Serializable {}
