package org.warm4ik.oms.mapper;

import org.springframework.data.domain.Page;
import org.warm4ik.oms.model.response.PaginationResponse;

public class PaginationMapper {

  public static <T> PaginationResponse<T> toPaginationResponse(Page<T> page) {
    return new PaginationResponse<>(
        page.getContent(),
        new PaginationResponse.Pagination(
            page.getTotalElements(),
            page.getSize(),
            page.getNumber() + 1,
            page.getTotalPages()));
  }
}
