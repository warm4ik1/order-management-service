package org.warm4ik.oms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.warm4ik.oms.model.enums.UserRole;

import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO implements Serializable {

  private UUID id;

  private String username;

  private UserRole role;
}
