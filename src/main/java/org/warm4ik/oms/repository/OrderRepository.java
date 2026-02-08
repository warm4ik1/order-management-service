package org.warm4ik.oms.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.warm4ik.oms.model.entity.Order;

import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

  Page<Order> findAllByUserId(UUID userId, Pageable pageable);
}
