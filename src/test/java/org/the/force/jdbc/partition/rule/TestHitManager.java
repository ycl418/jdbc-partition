package org.the.force.jdbc.partition.rule;

import org.testng.annotations.Test;
import org.the.force.jdbc.partition.rule.hits.HitManager;
import org.the.force.jdbc.partition.rule.hits.PartitionColumn;
import org.the.force.thirdparty.druid.support.logging.Log;
import org.the.force.thirdparty.druid.support.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by xuji on 2017/5/27.
 */
public class TestHitManager {

    private static Log logger = LogFactory.getLog(TestHitManager.class);

    @Test
    public void test1() {
        HitManager.set(new HashMap());
        Map<PartitionColumn, Object> map = HitManager.get();
        map.put(new PartitionColumn("t_order", "order_id"), 1);
        map.put(new PartitionColumn("t_order", "id"), 2);
        map.put(new PartitionColumn("t_order_item", "order_id"), 2);
        Set<PartitionColumnValue> set = HitManager.match("t_order");
        logger.info(set.toString());
        //HitManager.set(new HashedMap());
    }
}
