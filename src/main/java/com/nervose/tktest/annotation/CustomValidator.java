package com.nervose.tktest.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CustomValidator implements ConstraintValidator<CustomAnnotation,Object> {
    @Override
    public void initialize(CustomAnnotation constraintAnnotation) {
        System.out.println("自定义校验规则初始化");
    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        return "1".equals(o);
    }
}
