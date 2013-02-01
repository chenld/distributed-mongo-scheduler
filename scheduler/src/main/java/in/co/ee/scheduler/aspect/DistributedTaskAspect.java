package in.co.ee.scheduler.aspect;

import in.co.ee.scheduler.model.DistributedTask;
import in.co.ee.scheduler.model.OptimisticLockingException;
import in.co.ee.scheduler.service.DistributedScheduler;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: vishwas
 * Date: 31/01/13
 * Time: 16:59
 * To change this template use File | Settings | File Templates.
 */
@Aspect
@Component
public class DistributedTaskAspect {

    private DistributedScheduler distributedScheduler;

    @Autowired
    public DistributedTaskAspect(DistributedScheduler distributedScheduler) {
        this.distributedScheduler = distributedScheduler;
    }

    @Before("execution(* *.*(..)) && @annotation(distributedTask)")
    public void beforeTaskExecution(ProceedingJoinPoint joinPoint, DistributedTask distributedTask) throws Throwable {
        try {
            boolean canExecute = distributedScheduler.acquireLock(distributedTask.name());
            if(canExecute)  {
                joinPoint.proceed() ;
            }
        } catch (OptimisticLockingException exception) {

        }
    }

    @After("execution(* *.*(..)) && @annotation(distributedTask)")
    public void afterTaskExecution(ProceedingJoinPoint joinPoint, DistributedTask distributedTask) throws Throwable {
        distributedScheduler.releaseLock(distributedTask.name());
    }


}
