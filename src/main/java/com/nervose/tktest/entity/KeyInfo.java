package com.nervose.tktest.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class KeyInfo implements Serializable{
    Integer id;
    String pk;
    String sk;
}
