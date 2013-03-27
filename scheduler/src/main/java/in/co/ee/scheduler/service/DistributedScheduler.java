package in.co.ee.scheduler.service;

import com.mongodb.WriteResult;
import in.co.ee.scheduler.model.OptimisticLockingException;
import in.co.ee.scheduler.model.Task;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * Created with IntelliJ IDEA.
 * User: vishwas
 * Date: 01/02/13
 * Time: 10:39
 * To change this template use File | Settings | File Templates.
 */

@Service
public class DistributedScheduler {

    private MongoTemplate mongoTemplate;

    @Autowired
    public DistributedScheduler(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }


    public boolean acquireLock(String taskName) throws OptimisticLockingException {
        Task task = findByName(taskName);
        if(task != null && task.getStatus() == Task.Status.STARTED) {
           return false;
        }
        if(task == null) {
            task = new Task(taskName);
            try {
                mongoTemplate.save(task);
            } catch (DuplicateKeyException duplicateKey) {
                throw new OptimisticLockingException();
            }
        }
        startTask(task);
        return true;
    }

    private void startTask(Task task) throws OptimisticLockingException {
        Query query = query(where("name").is(task.getName())).addCriteria(where("lockVersion").is(task.getLockVersion())).addCriteria(where("status").ne(Task.Status.STARTED));
        Update update = Update.update("status", Task.Status.STARTED).set("lockVersion", new ObjectId());
        WriteResult result = mongoTemplate.updateMulti(query, update, Task.class);
        if(result.getN() != 1) {
           throw new OptimisticLockingException();
        }
    }

    private Task findByName(String taskName) {
        Query query = query(where("name").is(taskName));
        return mongoTemplate.findOne(query, Task.class);
    }

    public void releaseLock(String name) {
        Query query = query(where("name").is(name));
        query.addCriteria(where("status").is(Task.Status.STARTED));
        Update update = Update.update("status", Task.Status.READY);
        WriteResult result = mongoTemplate.updateMulti(query, update, Task.class);
        if(result.getN() != 1) {
            throw new IllegalStateException("Unexpected Task state in DB");
        }
    }
}
