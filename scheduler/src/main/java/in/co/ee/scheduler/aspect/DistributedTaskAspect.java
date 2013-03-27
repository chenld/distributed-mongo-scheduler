package in.co.ee.scheduler.aspect;

import in.co.ee.scheduler.model.DistributedTask;
import in.co.ee.scheduler.model.OptimisticLockingException;
import in.co.ee.scheduler.service.DistributedScheduler;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: vishwas
 * Date: 31/01/13
 * Time: 16:59
 * To change this template use File | Settings | File Templates.
 */
@Component
@Aspect
public class DistributedTaskAspect {

    private DistributedScheduler distributedScheduler;

    @Autowired
    public DistributedTaskAspect(DistributedScheduler distributedScheduler) {
        this.distributedScheduler = distributedScheduler;
    }
    @Pointcut("@annotation(distributedTask)")
    public void interceptTask(DistributedTask distributedTask) {
    }

    @Around("interceptTask(distributedTask)")
    public Object beforeTaskExecution(ProceedingJoinPoint joinPoint, DistributedTask distributedTask) throws Throwable {
        Object output = null;
        try {
            boolean canExecute = distributedScheduler.acquireLock(distributedTask.name());
            if(canExecute)  {
                try {
                    output = joinPoint.proceed();
                } finally {
                    distributedScheduler.releaseLock(distributedTask.name());
                }
            }
        } catch (OptimisticLockingException exception) {

        }
        return output;
    }

}
