package org.warm4ik.oms.service;

import org.warm4ik.oms.model.dto.TokenDTO;
import org.warm4ik.oms.model.dto.UserDTO;
import org.warm4ik.oms.model.request.auth.RegisterUserRequest;
import org.warm4ik.oms.model.response.OmsResponse;

public interface AuthService {

  OmsResponse<UserDTO> register(RegisterUserRequest request);

  OmsResponse<TokenDTO> login(String username, String password);

  OmsResponse<UserDTO> profile();

  OmsResponse<TokenDTO> refreshToken(String refreshToken);
}
