package com.nervose.tktest.annotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CustomValidator.class)
public @interface CustomAnnotation  {
    String message() default "自定义错误";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}

