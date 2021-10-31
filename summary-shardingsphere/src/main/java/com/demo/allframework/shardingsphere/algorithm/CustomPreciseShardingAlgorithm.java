package com.demo.allframework.shardingsphere.algorithm;

import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;

import java.util.Collection;

/**
 * @author CDY
 * @date 2021/10/31
 * @description 自定义精确分片算法
 */
public class CustomPreciseShardingAlgorithm implements PreciseShardingAlgorithm<Integer> {

    public CustomPreciseShardingAlgorithm() {
        super();
    }

    /**
     * 精确分片
     * @param availableTargetNames  可用的数据源或表名
     * @param shardingValue  分片值
     * @return  数据源或表名
     */
    @Override
    public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<Integer> shardingValue) {

        System.out.println("逻辑表：" + shardingValue.getLogicTableName());
        System.out.println("分片键：" + shardingValue.getColumnName());
        System.out.println("分片值：" + shardingValue.getValue());

        int num = shardingValue.getValue() % 2;
        for (String target : availableTargetNames) {
            if (target.contains(num + "")) {
                return target;
            }
        }

        return null;
    }
}
