package org.warm4ik.hub.oms.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.warm4ik.hub.oms.model.dto.OrderDTO;
import org.warm4ik.hub.oms.model.dto.OrderSearchDTO;
import org.warm4ik.hub.oms.model.entity.Order;
import org.warm4ik.hub.oms.model.entity.User;
import org.warm4ik.hub.oms.model.request.order.CreateOrderRequest;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OrderMapper {

    OrderDTO orderToOrderDTO(Order order);

    OrderSearchDTO orderToOrderSearchDTO(Order order);

    @Mapping(target = "status", ignore = true)
    @Mapping(target = "description", source = "request.description")
    @Mapping(target = "user", source = "user")
    Order createOrder(CreateOrderRequest request, User user);
}
