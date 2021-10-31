package com.demo.allframework.shardingsphere.algorithm;

import com.google.common.collect.Range;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingValue;

import java.util.Collection;
import java.util.Collections;

/**
 * @author CDY
 * @date 2021/10/31
 * @description 自定义范围分片算法
 */
public class CustomRangeShardingAlgorithm implements RangeShardingAlgorithm<Integer> {

    public CustomRangeShardingAlgorithm() {
        super();
    }

    @Override
    public Collection<String> doSharding(Collection<String> availableTargetNames, RangeShardingValue<Integer> shardingValue) {

        System.out.println("逻辑表：" + shardingValue.getLogicTableName());
        System.out.println("分片键：" + shardingValue.getColumnName());
        System.out.println("分片值：" + shardingValue.getValueRange());
        Range<Integer> valueRange = shardingValue.getValueRange();
        if (valueRange.contains(1)) {
            return availableTargetNames;
        }
        return Collections.emptyList();
    }
}
