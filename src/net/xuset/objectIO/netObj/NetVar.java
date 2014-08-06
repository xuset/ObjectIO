package net.xuset.objectIO.netObj;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by xuset on 7/23/14.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target( {ElementType.FIELD, ElementType.TYPE})
public @interface NetVar {
    String id() default "";
}
