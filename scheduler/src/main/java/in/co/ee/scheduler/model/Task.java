package in.co.ee.scheduler.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created with IntelliJ IDEA.
 * User: vishwas
 * Date: 01/02/13
 * Time: 11:03
 * To change this template use File | Settings | File Templates.
 */

@Document
public class Task {

    @Id
    private ObjectId id;

    private ObjectId lockVersion;


    @Indexed(unique = true)
    private String name;

    private Status status;

    public Task(String name) {
        this.name = name;
        this.lockVersion = new ObjectId();
        this.status = Status.READY;
    }

    public String getName() {
        return name;
    }

    public ObjectId getId() {
        return id;
    }

    public ObjectId getLockVersion() {
        return lockVersion;
    }

    public void setLockVersion(ObjectId lockVersion) {
        this.lockVersion = lockVersion;
    }

    public Status getStatus() {
        return status;
    }


    public enum Status {
        READY, STARTED
    }
}
