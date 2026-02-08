package org.warm4ik.hub.oms.service;

import org.warm4ik.hub.oms.model.dto.TokenDTO;
import org.warm4ik.hub.oms.model.dto.UserDTO;
import org.warm4ik.hub.oms.model.request.user.RegisterUserRequest;
import org.warm4ik.hub.oms.model.response.OmsResponse;

public interface AuthService {

  OmsResponse<UserDTO> register(RegisterUserRequest request);

  OmsResponse<TokenDTO> login(String username, String password);

  OmsResponse<UserDTO> profile();
}
