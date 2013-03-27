package in.co.ee.service;

import in.co.ee.scheduler.model.Task;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

import static org.fest.assertions.Assertions.assertThat;


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

    @Autowired
    private ScheduledTask scheduledTask;


    @Before
    public void setup() {
        mongoTemplate.getDb().dropDatabase();
    }

    @Test
    public void itAcquiresLockAndExecutesAtLeastOneTaskSuccessfullyAndThenReleasesLock() {
        Task task = findTask();
        assertThat(task).isNull();
        Collection<Boolean> results = executeTasksInParallel(scheduledTask, false);
        assertThat(results.contains(Boolean.TRUE)).isTrue();
        task = findTask();
        assertThat(task).isNotNull();
        assertThat(task.getStatus()).isEqualTo(Task.Status.READY);
        assertThat(task.getLockVersion()).isNotNull();
    }

    @Test
    public void itAcquiresLockAndThenReleasesLockEvenIfTaskThrowsAnException() {
        Task task = findTask();
        assertThat(task).isNull();
        executeTasksInParallel(scheduledTask, true);

        task = findTask();
        assertThat(task).isNotNull();
        assertThat(task.getStatus()).isEqualTo(Task.Status.READY);
        assertThat(task.getLockVersion()).isNotNull();
    }


    private Task findTask() {
        return mongoTemplate.findOne(Query.query(Criteria.where("name").is("Task_Name")), Task.class);
    }

    private Collection<Boolean> executeTasksInParallel(ScheduledTask scheduledTask, boolean throwException) {
        ExecutorService service = Executors.newFixedThreadPool(5);
        List<Future<Boolean>> futures = new ArrayList<Future<Boolean>>();
        for(int i=0; i< 5 ; i++) {
            futures.add(service.submit(new SchedulerCallable(scheduledTask, throwException)));
        }
        service.shutdown();
        while(!service.isTerminated());
        List<Boolean> results = new ArrayList<Boolean>();
        for (Future<Boolean> future : futures) {
            try {
                results.add(future.get());
            } catch (InterruptedException e) {
            } catch (ExecutionException e) {
            }
        }
        return results;
    }

    private static class SchedulerCallable implements Callable<Boolean> {
        private ScheduledTask scheduledTask;
        private boolean throwException;

        private SchedulerCallable(ScheduledTask scheduledTask, boolean throwException) {
            this.scheduledTask = scheduledTask;
            this.throwException = throwException;
        }

        @Override
        public Boolean call() throws Exception {
            return scheduledTask.execute(throwException);
        }
    }

}
