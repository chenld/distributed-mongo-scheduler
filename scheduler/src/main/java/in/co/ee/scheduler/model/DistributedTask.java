package in.co.ee.scheduler.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created with IntelliJ IDEA.
 * User: vishwas
 * Date: 31/01/13
 * Time: 16:48
 * To change this template use File | Settings | File Templates.
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedTask {

    String name();
}
