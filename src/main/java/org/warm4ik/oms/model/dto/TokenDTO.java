package org.warm4ik.oms.model.dto;

import java.io.Serializable;

public record TokenDTO(String accessToken, String refreshToken) implements Serializable {}
