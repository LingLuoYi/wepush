package com.wepush.wepush.entity;

import lombok.Getter;
import lombok.Setter;

public class User {

    @Getter @Setter
    private String username;

    @Getter @Setter
    private String password = "";
}
