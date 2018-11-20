package com.ctrip.framework.apollo.spring.util;

import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

/**
 * @author Jason Song(song_s@ctrip.com)
 * BeanDefinitionRegistry注册bean定义的一个工具类，方便BeanDefinition的注册
 */
public class BeanRegistrationUtil {
  public static boolean registerBeanDefinitionIfNotExists(BeanDefinitionRegistry registry, String beanName,
      Class<?> beanClass) {
    return registerBeanDefinitionIfNotExists(registry, beanName, beanClass, null);
  }

  public static boolean registerBeanDefinitionIfNotExists(BeanDefinitionRegistry registry, String beanName,
                                                          Class<?> beanClass, Map<String, Object> extraPropertyValues) {
    //比较这个beanName是否已经存在
    if (registry.containsBeanDefinition(beanName)) {
      return false;
    }

    String[] candidates = registry.getBeanDefinitionNames();

    //比较这个beanClass是否已经存在
    for (String candidate : candidates) {
      BeanDefinition beanDefinition = registry.getBeanDefinition(candidate);
      if (Objects.equals(beanDefinition.getBeanClassName(), beanClass.getName())) {
        return false;
      }
    }
    //生成beanDefinition(GenericBeanDefinition)
    BeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(beanClass).getBeanDefinition();
    //todo:这块MutablePropertyValues是否就是后面要注入的对象的属性值?需要跟一下代码
    if (extraPropertyValues != null) {
      for (Map.Entry<String, Object> entry : extraPropertyValues.entrySet()) {
        beanDefinition.getPropertyValues().add(entry.getKey(), entry.getValue());
      }
    }

    registry.registerBeanDefinition(beanName, beanDefinition);

    return true;
  }


}
