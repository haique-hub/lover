package com.lovers.enums;

import lombok.Getter;

@Getter
public enum CoupleStatusEnum {
    PENDING(0),
    ACTIVE(1),
    UNBOUND(2);

    private final int code;

    CoupleStatusEnum(int code) {
        this.code = code;
    }
}
