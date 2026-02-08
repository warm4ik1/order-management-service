package org.warm4ik.oms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class OrderManagementServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(OrderManagementServiceApplication.class, args);
  }
}
