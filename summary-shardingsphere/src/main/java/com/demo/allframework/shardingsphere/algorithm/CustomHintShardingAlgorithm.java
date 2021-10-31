package com.demo.allframework.shardingsphere.algorithm;

import org.apache.shardingsphere.api.sharding.hint.HintShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.hint.HintShardingValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author CDY
 * @date 2021/10/31
 * @description 自定义Hint分片算法
 */
public class CustomHintShardingAlgorithm implements HintShardingAlgorithm<String> {

    public CustomHintShardingAlgorithm() {
        super();
    }

    /**
     * @param availableTargetNames  可用的数据源或表名
     * @param shardingValue  分片值
     * @return  数据源或表名
     */
    @Override
    public Collection<String> doSharding(Collection<String> availableTargetNames, HintShardingValue<String> shardingValue) {

        System.out.println("逻辑表："+shardingValue.getLogicTableName());
        System.out.println("分片键："+shardingValue.getColumnName());
        System.out.println("分片值："+shardingValue.getValues());

        List<String> res = new ArrayList<>();
        for (String target : availableTargetNames) {
            for (String value : shardingValue.getValues()) {
                if(target.contains(value)){
                    res.add(target);
                }
            }

        }

        return res;
    }

}
