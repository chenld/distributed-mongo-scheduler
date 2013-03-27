package in.co.ee.service;

import in.co.ee.scheduler.model.DistributedTask;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTask {

    @DistributedTask(name = "Task_Name")
    public Boolean execute(boolean throwException) {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(throwException) {
            throw new RuntimeException();
        }
        return true;
    }
}