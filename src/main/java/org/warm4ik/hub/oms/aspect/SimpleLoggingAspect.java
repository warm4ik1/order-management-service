package org.warm4ik.hub.oms.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Around;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class SimpleLoggingAspect {

  @Around("@within(org.springframework.web.bind.annotation.RestController)")
  public Object logControllerMethods(ProceedingJoinPoint joinPoint) throws Throwable {
    // Если TRACE логирование выключено - пропускаем
    if (!log.isTraceEnabled()) {
      return joinPoint.proceed();
    }

    // Получаем информацию о методе
    String methodName = joinPoint.getSignature().getName();
    String className = joinPoint.getTarget().getClass().getSimpleName();

    // Логируем вход в метод
    log.trace("→ {}.{}", className, methodName);

    long start = System.currentTimeMillis();

    try {
      // Выполняем оригинальный метод
      Object result = joinPoint.proceed();

      // Замеряем время выполнения
      long duration = System.currentTimeMillis() - start;

      // Логируем успешное завершение
      log.trace("← {}.{} ({} ms)", className, methodName, duration);

      return result;

    } catch (Exception e) {
      // Если произошла ошибка
      long duration = System.currentTimeMillis() - start;

      // Логируем ошибку
      log.error("✗ {}.{} failed ({} ms): {}", className, methodName, duration, e.getMessage());

      // Пробрасываем исключение дальше
      throw e;
    }
  }
}
