package org.warm4ik.hub.oms.service;

import org.warm4ik.hub.oms.model.dto.TokenDTO;
import org.warm4ik.hub.oms.model.dto.UserDTO;
import org.warm4ik.hub.oms.model.request.user.RegisterUserRequest;
import org.warm4ik.hub.oms.model.response.ApiResponse;

public interface AuthService {

  ApiResponse<UserDTO> register(RegisterUserRequest request);

  ApiResponse<TokenDTO> login(String username, String password);

  ApiResponse<UserDTO> profile();
}
