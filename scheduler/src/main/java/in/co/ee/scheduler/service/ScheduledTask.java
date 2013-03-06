package in.co.ee.scheduler.service;

import in.co.ee.scheduler.model.DistributedTask;

/**
 * Created with IntelliJ IDEA.
 * User: vishwas
 * Date: 06/03/13
 * Time: 16:12
 * To change this template use File | Settings | File Templates.
 */
public class ScheduledTask {

    @DistributedTask(name = "DoSomething")
    public Boolean execute() {
        System.out.print("Executed");
        return true;
    }
}
