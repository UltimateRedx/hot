package com.hotelpal.service.service;

import com.alibaba.fastjson.JSON;
import com.hotelpal.service.basic.mysql.dao.UserRelaDao;
import com.hotelpal.service.common.so.UserRelaSO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
@Transactional
public class TestService {
    @Resource
    private UserRelaDao userRelaDao;
    @Resource
    private TestService testService;

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void test() {
        System.out.println(1);
        JSON.toJSONString(userRelaDao.getPageList(new UserRelaSO()));
        System.out.println(2);
//        ((TestService) AopContext.currentProxy()).test1();
        testService.test1();
    }
    @Transactional
    public void test1() {
        System.out.println(11111111);
        JSON.toJSONString(userRelaDao.getPageList(new UserRelaSO()));
        System.out.println(222222);
    }
}
