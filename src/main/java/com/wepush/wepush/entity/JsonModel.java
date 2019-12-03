package com.wepush.wepush.entity;

import lombok.Getter;
import lombok.Setter;

public class JsonModel<T> {

    @Setter @Getter
    private Integer code;

    @Setter @Getter
    private String msg;

    @Setter @Getter
    private T data;
}
