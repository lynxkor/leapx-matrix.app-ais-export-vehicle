package com.leapmotor.export.vehicle;

import com.alicp.jetcache.anno.config.EnableMethodCache;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.env.ConfigurableEnvironment;


@SpringBootApplication(scanBasePackages = {"com.leapmotor.export.vehicle"})
@EnableConfigurationProperties
@MapperScan(basePackages = {"com.leapmotor.export.vehicle.**.mapper"})
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.leapmotor.export.vehicle"})
@EnableMethodCache(basePackages = {"com.leapmotor.export.vehicle.infrastructure"})
@EnableAspectJAutoProxy(exposeProxy = true)
public class ExportVehicleApplication {

    public static void main(final String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(ExportVehicleApplication.class, args);
        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        System.out.println("\n"+
                " ......................阿弥陀佛......................\n"+
                "                       _oo0oo_                      \n"+
                "                      o8888888o                     \n"+
                "                     88 \".\" 88                     \n"+
                "                      (| -_- |)                     \n"+
                "                      0\\  =  /0                     \n"+
                "                   ___/‘---’\\___                   \n"+
                "                  .' \\|       |/ '.                 \n"+
                "                 / \\\\|||  :  |||// \\                \n"+
                "                / _||||| -卍-|||||_ \\               \n"+
                "               |   | \\\\\\  -  /// |   |              \n"+
                "               | \\_|  ''\\---/''  |_/ |              \n"+
                "               \\  .-\\__  '-'  ___/-. /              \n"+
                "             ___'. .'  /--.--\\  '. .'___            \n"+
                "         .\"\" ‘<  ‘.___\\_<|>_/___.’>’ \"\".          \n"+
                "       | | :  ‘- \\‘.;‘\\ _ /’;.’/ - ’ : | |        \n"+
                "         \\  \\ ‘_.   \\_ __\\ /__ _/   .-’ /  /        \n"+
                "    =====‘-.____‘.___ \\_____/___.-’___.-’=====     \n"+
                "                       ‘=---=’                      \n"+
                "                                                    \n"+
                "....................佛祖保佑 ,永不宕机...................");
    }
}
