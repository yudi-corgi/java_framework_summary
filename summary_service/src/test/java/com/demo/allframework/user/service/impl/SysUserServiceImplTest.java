package com.demo.allframework.user.service.impl;

import com.demo.allframework.user.domain.Trader;
import com.demo.allframework.user.domain.Transaction;
import com.demo.allframework.entity.SysUser;
import com.demo.allframework.service.SysUserService;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author YUDI
 * @date 2020/5/2 18:14
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SysUserServiceImplTest {

    @Autowired
    private SysUserService userService;
    @Autowired
    private CacheManager cacheManager;

    @Test
    public void testInsert() {

        SysUser user = new SysUser();
        user.setName("YUDI");
        user.setGender("男");
        user.setLoginName("admin");
        user.setPassword("admin123");
        int effective = userService.insert(user);
        System.out.println( effective);
    }

    @Test
    public void testCache(){
        // AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext();
        // ac.register(CacheConfig.class);
        // ac.refresh();
        // ConcurrentMapCacheManager cacheManager = ac.getBean(ConcurrentMapCacheManager.class);

        // System.out.println(userService.cache());
        // System.out.println(userService.cache());
        ConcurrentMapCache cache1 = (ConcurrentMapCache) cacheManager.getCache("cache1");
        assert cache1 != null;
        cache1.getNativeCache().keySet().forEach(System.out::println);
    }

    @Test
    public void testInvoke() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Class<?> clazz = Class.forName("com.demo.allframework.service.SysUserService");
        System.out.println(clazz);
        System.out.println(clazz.toString());
        System.out.println(userService.getClass());
        List<String> list = new ArrayList<>();
        list.add("123");list.add("abc");list.add("asd");list.add("qwe");
        List<String> list2 = list.stream().skip(2).limit(1).collect(Collectors.toList());
        System.out.println(list2);
        String[] arr  = {"hello","world"};
        List<String> list3 = new ArrayList<>();
        list3.add("hello");
        list3.add("world");
        List<String> list4 = list3.stream().map(world -> world.split("")).flatMap(Arrays::stream).distinct().collect(Collectors.toList());
        System.out.println(list4);
        List<String> numbers1 = Arrays.asList("1", "2", "3");
        List<String> numbers2 = Arrays.asList("4", "5");
        List<String> n = numbers1.stream().flatMap(i->numbers2.stream().map(j->i+","+j)).collect(Collectors.toList());
        n.forEach(System.out::println);
        List<Integer> number = Arrays.asList(1,2,3);
        number.stream().reduce(Integer::max).ifPresent(System.out::println);
    }

    @Test
    public void testJavaEightFeature(){
        System.out.println("=================================数据封装==============================");
        Trader raoul = new Trader("Raoul", "Cambridge");
        Trader mario = new Trader("Mario","Milan");
        Trader alan = new Trader("Alan","Cambridge");
        Trader brian = new Trader("Brian","Cambridge");
        List<Trader> traders = new ArrayList<>();
        traders.add(raoul);traders.add(mario);traders.add(alan);traders.add(brian);
        List<Transaction> transactions = Arrays.asList(
                new Transaction(brian, 2011, 300), new Transaction(raoul, 2012, 1000), new Transaction(raoul, 2011, 400),
                new Transaction(mario, 2012, 710), new Transaction(mario, 2012, 700), new Transaction(alan, 2012, 950)
        );
        System.out.println("=================================第一道题==============================");
        List<Transaction> oneList = transactions.stream().filter(t->t.getYear()==2011).sorted(Comparator.comparing(Transaction::getValue)).collect(Collectors.toList());
        System.out.println(oneList);
        System.out.println("=================================第二道题==============================");
        System.out.println(traders.stream().map(Trader::getCity).distinct().collect(Collectors.toList()));
        System.out.println(transactions.stream().map(t -> t.getTrader().getCity()).distinct().collect(Collectors.toList()));
        System.out.println(transactions.stream().map(t -> t.getTrader().getCity()).collect(Collectors.toSet()));
        System.out.println("=================================第三道题==============================");
        System.out.println(traders.stream()
                .filter(t->"Cambridge".equalsIgnoreCase(t.getCity()))
                .sorted(Comparator.comparing(Trader::getName)).collect(Collectors.toList()));
        System.out.println(
                transactions.stream().filter(t->"Cambridge".equalsIgnoreCase(t.getTrader().getCity())).distinct()
                        .sorted(Comparator.comparing(t -> t.getTrader().getName())).collect(Collectors.toList())
        );
        System.out.println("=================================第四道题==============================");
        System.out.println(transactions.stream().map(t->t.getTrader().getName()).distinct().sorted().collect(Collectors.toList()));
        System.out.println(transactions.stream().map(t->t.getTrader().getName()).distinct().sorted().reduce("",(n1,n2)->n1+n2));
        System.out.println(transactions.stream().map(t->t.getTrader().getName()).distinct().sorted().collect(Collectors.joining()));
        System.out.println("=================================第五道题==============================");
        System.out.println(transactions.stream().anyMatch(t->"Milan".equalsIgnoreCase(t.getTrader().getCity())));
        System.out.println("=================================第六道题==============================");
        transactions.stream()
                .filter(t->"Cambridge".equalsIgnoreCase(t.getTrader().getCity()))
                .map(Transaction::getValue)
                .reduce(Integer::sum).ifPresent(System.out::println);
        System.out.println("=================================第七道题==============================");
        transactions.stream().map(Transaction::getValue).reduce(Integer::max).ifPresent(System.out::println);
        System.out.println("=================================第八道题==============================");
        transactions.stream().reduce((t1, t2) -> t1.getValue()<t2.getValue()?t1:t2).ifPresent(System.out::println);
        transactions.stream().min(Comparator.comparingInt(Transaction::getValue)).ifPresent(System.out::println);

    }

    @Test
    public void testCompare(){
        List<Integer> numbers1 = Lists.newArrayList(4, 5, 3, 7, 1, 9, 0);
        numbers1.sort(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                System.out.println(o1+":"+o2);
                return o1.compareTo(o2);
            }
        });
        System.out.println(numbers1);
    }

}