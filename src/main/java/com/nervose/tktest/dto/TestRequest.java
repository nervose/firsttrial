package com.nervose.tktest.dto;

import com.nervose.tktest.annotation.CustomAnnotation;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.*;
import java.util.Date;

@Data
@ToString
public class TestRequest {

    @Past
    private Date date;
    @NotNull
    private Integer id;
    @Pattern(regexp="[0-9a-zA-Z]*")
    private String description;
    @Size(max = 10)
    private String name;
    @CustomAnnotation
    private String sex;
}
