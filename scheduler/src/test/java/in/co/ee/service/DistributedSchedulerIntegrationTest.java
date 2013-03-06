package in.co.ee.service;

import in.co.ee.scheduler.service.ScheduledTask;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.Assert.assertThat;

/**
 * Created with IntelliJ IDEA.
 * User: vishwas
 * Date: 06/03/13
 * Time: 14:11
 * To change this template use File | Settings | File Templates.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:test-application-context.xml"})
public class DistributedSchedulerIntegrationTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    public void itThrowsOptimisticLockExceptionWhenAcquiringDuplicateLocks() throws ExecutionException, InterruptedException, IOException {
        ScheduledTask scheduledTask = new ScheduledTask();
        ExecutorService service = Executors.newFixedThreadPool(100);
        List<Future<Boolean>> results = new ArrayList<Future<Boolean>>();
        for(int i=0; i< 5 ; i++) {
            results.add(service.submit(new SchedulerCallable(scheduledTask)));
        }

        service.shutdown();
        while(!service.isTerminated());
        for(Future<Boolean> result : results) {
            System.out.println(result.get());
        }
    }

    private static class SchedulerCallable implements Callable<Boolean> {
        private ScheduledTask scheduledTask;

        private SchedulerCallable(ScheduledTask scheduledTask) {
            this.scheduledTask = scheduledTask;
        }

        @Override
        public Boolean call() throws Exception {
            return scheduledTask.execute();
        }
    }

}
