package com.leapmotor.export.vehicle.base;

import cn.hutool.extra.spring.SpringUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leapmotor.export.vehicle.ExportVehicleApplication;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

@SpringBootTest(classes = ExportVehicleApplication.class)
@ActiveProfiles("dev")
public class BaseServiceTest<T> {
    //不能自动注入方式，否则具体实现没有实现泛型会无法启动成功
    protected T service;

    @Resource
    ObjectMapper objectMapper;

    @BeforeAll
    public static void before() {
        //网context里面设置用户
    }

    @SneakyThrows
    protected void printJson(Object result) {
        System.err.println(objectMapper.writeValueAsString(result));
    }

    @PostConstruct
    public void postConstruct() {
        // 获取子类的Class对象
        Class<?> subclass = getClass();
        // 获取父类的泛型参数类型
        Type genericSuperclass = subclass.getGenericSuperclass();
        if (genericSuperclass instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) genericSuperclass;
            // 获取第一个类型参数的Class对象
            Class<T> typeArgument = (Class<T>) pt.getActualTypeArguments()[0];
            service = SpringUtil.getBean(typeArgument);
        }
    }
}
