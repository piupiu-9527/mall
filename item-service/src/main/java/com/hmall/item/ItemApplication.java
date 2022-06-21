package com.hmall.item;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@MapperScan("com.hmall.item.mapper")
@SpringBootApplication
@EnableTransactionManagement
public class ItemApplication {
    public static void main(String[] args) {
        SpringApplication.run(ItemApplication.class, args);
    }

    @Bean
    //注意MessageConverter导包，使用amqp的
    //import org.springframework.amqp.support.converter.MessageConverter;
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }
}
