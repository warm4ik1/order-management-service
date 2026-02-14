package org.warm4ik.oms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.warm4ik.oms.model.dto.TokenDTO;
import org.warm4ik.oms.model.dto.UserDTO;
import org.warm4ik.oms.model.request.auth.LoginRequest;
import org.warm4ik.oms.model.request.auth.RegisterUserRequest;
import org.warm4ik.oms.model.response.OmsResponse;

public interface AuthApi {

  @Operation(
      summary = "User registration",
      description = "Registers a new user in the system and returns created user details")
  @ApiResponses({
    @ApiResponse(responseCode = "201", description = "User successfully registered"),
    @ApiResponse(responseCode = "400", description = "Validation error or malformed request"),
    @ApiResponse(responseCode = "409", description = "Username already exists")
  })
  ResponseEntity<OmsResponse<UserDTO>> register(@RequestBody @Valid RegisterUserRequest request);

  @Operation(
      summary = "User login",
      description = "Authenticates user credentials and returns JWT access token")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Login successful"),
    @ApiResponse(responseCode = "401", description = "Invalid username or password"),
    @ApiResponse(responseCode = "400", description = "Malformed request or invalid input")
  })
  ResponseEntity<OmsResponse<TokenDTO>> login(@RequestBody @Valid LoginRequest request);

  @Operation(
      summary = "Get current user profile",
      description = "Returns profile of authenticated user")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Profile retrieved"),
    @ApiResponse(responseCode = "401", description = "Unauthorized"),
    @ApiResponse(responseCode = "403", description = "Access denied"),
    @ApiResponse(responseCode = "404", description = "User not found")
  })
  ResponseEntity<OmsResponse<UserDTO>> profile();
}
