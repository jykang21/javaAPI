package kr.co.softbridge.sobroplatform.commons.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface SecondaryMapper {
    String value() default "";
}
