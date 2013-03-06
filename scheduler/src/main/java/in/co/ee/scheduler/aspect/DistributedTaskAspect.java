package in.co.ee.scheduler.aspect;

import in.co.ee.scheduler.model.DistributedTask;
import in.co.ee.scheduler.model.OptimisticLockingException;
import in.co.ee.scheduler.service.DistributedScheduler;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
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

    @Pointcut("@annotation(distributedTask)")
    public void interceptTask(DistributedTask distributedTask) {
        System.out.print("POINTCUT");
    }

    @Around("interceptTask(distributedTask)")
    public Object beforeTaskExecution(ProceedingJoinPoint joinPoint, DistributedTask distributedTask) throws Throwable {
        Object output = null;
        try {
            boolean canExecute = distributedScheduler.acquireLock("NAME");
            if(canExecute)  {
                output = joinPoint.proceed();
                distributedScheduler.releaseLock("NAME");
            }
        } catch (OptimisticLockingException exception) {

        }
        return output;
    }


}
