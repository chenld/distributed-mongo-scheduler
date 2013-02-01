package in.co.ee.scheduler.model;

import org.springframework.scheduling.annotation.Scheduled;

import java.lang.annotation.*;

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
