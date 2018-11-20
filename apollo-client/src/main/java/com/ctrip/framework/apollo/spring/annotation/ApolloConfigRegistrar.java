package com.ctrip.framework.apollo.spring.annotation;

import com.ctrip.framework.apollo.spring.property.SpringValueDefinitionProcessor;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import com.ctrip.framework.apollo.spring.config.PropertySourcesProcessor;
import com.ctrip.framework.apollo.spring.util.BeanRegistrationUtil;
import com.google.common.collect.Lists;

/**
 * @author Jason Song(song_s@ctrip.com)
 * java注解方式配置apollo的入口
 * EnableApolloConfig注解的核心回调
 * ImportBeanDefinitionRegistrar这个接口的核心作用就是在Configuration注解类进行处理之前进行一些额外的bean的定义
 *
 */
public class ApolloConfigRegistrar implements ImportBeanDefinitionRegistrar {
  @Override
  public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
    //获取@EnableApolloConfig对象
    AnnotationAttributes attributes = AnnotationAttributes.fromMap(importingClassMetadata
        .getAnnotationAttributes(EnableApolloConfig.class.getName()));
    //获取namespace值
    String[] namespaces = attributes.getStringArray("value");
    //获取注解的顺序
    int order = attributes.getNumber("order");
    //注册到自己的PropertySourcesProcessor,实质就是一个BeanFactoryPostProcessor
    //BeanFactoryPostProcessor本质是所有bean定义后的一个钩子
    PropertySourcesProcessor.addNamespaces(Lists.newArrayList(namespaces), order);

    Map<String, Object> propertySourcesPlaceholderPropertyValues = new HashMap<>();
    // to make sure the default PropertySourcesPlaceholderConfigurer's priority is higher than PropertyPlaceholderConfigurer
    propertySourcesPlaceholderPropertyValues.put("order", 0);
    //----------定义这些bean，注册到BeanDefinitionRegistry----------------
    //保证属性占位符处理的注册,spring3.1以后默认使用PropertySourcesPlaceholderConfigurer,PropertyPlaceholderConfigurer为更早的spring版本使用
    //本质是一个BeanFactoryPostProcessor
    BeanRegistrationUtil.registerBeanDefinitionIfNotExists(registry, PropertySourcesPlaceholderConfigurer.class.getName(),
        PropertySourcesPlaceholderConfigurer.class, propertySourcesPlaceholderPropertyValues);
    /***
     * 核心类
     * 1. 自定义的BeanFactoryPostProcessor
     * 2. 目的是在所有bean都定义好之后，根据注册的namespace，从服务器拉去配置，创建成PropertySource，然后放入ConfigurableEnvironment中。
     * 3. 每一个ApplicationContext会包含一个Environment，Environmentn内部会有多个PropertySource,PropertySource在前面的优先级更高
     */
    BeanRegistrationUtil.registerBeanDefinitionIfNotExists(registry, PropertySourcesProcessor.class.getName(),
        PropertySourcesProcessor.class);
    /***
     * 1. 自定义的BeanPostProcessor
     * 2. 目的:
     *        将@ApolloConfig标注的字段注入值
     *        将@ApolloConfigChangeListener标注的方法注册成监听器
     */
    BeanRegistrationUtil.registerBeanDefinitionIfNotExists(registry, ApolloAnnotationProcessor.class.getName(),
        ApolloAnnotationProcessor.class);

    BeanRegistrationUtil.registerBeanDefinitionIfNotExists(registry, SpringValueProcessor.class.getName(), SpringValueProcessor.class);
    BeanRegistrationUtil.registerBeanDefinitionIfNotExists(registry, SpringValueDefinitionProcessor.class.getName(), SpringValueDefinitionProcessor.class);

    BeanRegistrationUtil.registerBeanDefinitionIfNotExists(registry, ApolloJsonValueProcessor.class.getName(),
            ApolloJsonValueProcessor.class);
  }
}
