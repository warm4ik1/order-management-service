package org.warm4ik.oms.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.warm4ik.oms.model.dto.UserDTO;
import org.warm4ik.oms.model.entity.User;
import org.warm4ik.oms.model.request.auth.RegisterUserRequest;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

  @Mapping(target = "role", ignore = true)
  @Mapping(target = "orders", ignore = true)
  User createUser(RegisterUserRequest request);

  UserDTO userToUserDTO(User user);
}
