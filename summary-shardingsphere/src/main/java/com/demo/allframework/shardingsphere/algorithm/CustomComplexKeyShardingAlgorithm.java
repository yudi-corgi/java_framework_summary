package com.demo.allframework.shardingsphere.algorithm;

import org.apache.shardingsphere.api.sharding.complex.ComplexKeysShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.complex.ComplexKeysShardingValue;

import java.util.Collection;

/**
 * @author CDY
 * @date 2021/10/31
 * @description 自定义复合分片算法
 */
public class CustomComplexKeyShardingAlgorithm implements ComplexKeysShardingAlgorithm<String> {

    public CustomComplexKeyShardingAlgorithm() {
        super();
    }

    /**
     * @param availableTargetNames  可用的数据源或表名
     * @param shardingValue  分片值
     * @return  数据源或表名
     */
    @Override
    public Collection<String> doSharding(Collection<String> availableTargetNames, ComplexKeysShardingValue<String> shardingValue) {

        System.out.println("逻辑表：" + shardingValue.getLogicTableName());
        shardingValue.getColumnNameAndShardingValuesMap().forEach((k,v)->{
            System.out.println("精确分片键：" + k);
            System.out.println("精确分片值：" + v);
        });
        shardingValue.getColumnNameAndRangeValuesMap().forEach((k,v)->{
            System.out.println("精确分片键：" + k);
            System.out.println("精确分片值：" + v);
        });

        return availableTargetNames;
    }

}
