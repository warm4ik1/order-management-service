package org.warm4ik.hub.oms.model.response;

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
public class ApiResponse<P extends Serializable> implements Serializable {
    private String message;
    private P payload;
    private boolean success;

    public static <P extends Serializable> ApiResponse<P> createSuccessful(P payload) {
        return new ApiResponse<>(StringUtils.EMPTY, payload, true);
    }

    public static <P extends Serializable> ApiResponse<P> createSuccessful(String message, P payload) {
        return new ApiResponse<>(message, payload, true);
    }

    public static <P extends Serializable> ApiResponse<P> createFailed(String message) {
        return new ApiResponse<>(message, null, false);
    }
}
