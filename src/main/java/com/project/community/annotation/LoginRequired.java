package com.project.community.annotation;

import java.lang.annotation.*;

/**
 * @author makwingchi
 * @Description
 * @create 2020-04-16 23:41
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LoginRequired {
}
