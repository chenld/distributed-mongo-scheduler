package in.co.ee.scheduler.service;

import com.mongodb.WriteConcern;
import org.springframework.data.mongodb.core.MongoAction;
import org.springframework.data.mongodb.core.WriteConcernResolver;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: vishwas
 * Date: 06/03/13
 * Time: 15:26
 * To change this template use File | Settings | File Templates.
 */
@Component
public class DistributedSchedulerWriteConcernResolver implements WriteConcernResolver {


    @Override
    public WriteConcern resolve(MongoAction action) {
        if(action.getCollectionName().contains("task")) {
            return WriteConcern.SAFE;
        }
        return action.getDefaultWriteConcern();
    }
}
