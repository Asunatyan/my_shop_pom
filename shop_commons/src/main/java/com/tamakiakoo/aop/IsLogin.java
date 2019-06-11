package com.tamakiakoo.aop;

import java.lang.annotation.*;

@Documented
@Target(value = ElementType.METHOD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface IsLogin {
    boolean mustLogin() default false;
}
