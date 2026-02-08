package org.warm4ik.oms.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OmsResponse<P extends Serializable> implements Serializable {
  private String message;
  private P payload;
  private boolean success;

  public static <P extends Serializable> OmsResponse<P> createSuccessful(P payload) {
    return new OmsResponse<>(StringUtils.EMPTY, payload, true);
  }

  public static <P extends Serializable> OmsResponse<P> createSuccessful(
      String message, P payload) {
    return new OmsResponse<>(message, payload, true);
  }

  public static <P extends Serializable> OmsResponse<P> createFailed(String message) {
    return new OmsResponse<>(message, null, false);
  }
}
