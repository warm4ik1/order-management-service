package org.warm4ik.hub.oms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.warm4ik.hub.oms.model.dto.UserDTO;
import org.warm4ik.hub.oms.model.response.OmsResponse;
import org.warm4ik.hub.oms.model.response.PaginationResponse;

import java.util.UUID;

public interface UserApi {

  @Operation(summary = "Get all users", description = "Returns a paginated list of all users")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "The list of users was successfully retrieved",
            content = @Content(schema = @Schema(implementation = PaginationResponse.class))),
        @ApiResponse(responseCode = "403", description = "Access is forbidden"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
      })
  ResponseEntity<OmsResponse<PaginationResponse<UserDTO>>> findAllUsers(
      @Parameter(description = "Page number (starting from 0)")
          @RequestParam(name = "page", defaultValue = "0")
          int page,
      @Parameter(description = "Number of items per page")
          @RequestParam(name = "limit", defaultValue = "10")
          int limit);

  @Operation(
      summary = "Delete a user by ID",
      description = "Deletes a user from the system by their unique UUID")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204", description = "User was successfully deleted"),
        @ApiResponse(responseCode = "403", description = "Access is forbidden"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
      })
  ResponseEntity<Void> deleteUserById(
      @Parameter(description = "UUID of the user to be deleted") @PathVariable(name = "id")
          UUID id);
}
