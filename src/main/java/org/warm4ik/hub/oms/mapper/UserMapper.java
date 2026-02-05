package org.warm4ik.hub.oms.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.warm4ik.hub.oms.model.dto.UserDTO;
import org.warm4ik.hub.oms.model.entity.User;
import org.warm4ik.hub.oms.model.request.user.RegisterUserRequest;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "role", ignore = true)
  @Mapping(target = "orders", ignore = true)
  User createUser(RegisterUserRequest request);

  UserDTO userToUserDTO(User user);
}
