package com.atguigu.gmall.common.cache;

import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.*;

/**
 * @author rbx
 * @title
 * @Create 2023-02-28 12:59
 * @Description
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface GmallCache {
    String prefix() default "chche:";
    String suffix() default ":info";
}
