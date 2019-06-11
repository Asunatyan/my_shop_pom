package com.tamakiakoo.utils;

import java.lang.annotation.*;

@Documented
@Target(value = ElementType.METHOD)
@Retention(value = RetentionPolicy.RUNTIME)//运行时生效
public @interface TestAnno {

    //String value();

    String[] toarray();

    int add() default 13;

    String test() default "morenzhi";

}
