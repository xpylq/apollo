package com.ctrip.framework.apollo.spring.config;

import com.ctrip.framework.apollo.spring.spi.ConfigPropertySourcesProcessorHelper;
import com.ctrip.framework.foundation.internals.ServiceBootstrap;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;

/**
 * Apollo Property Sources processor for Spring XML Based Application
 * spring bean 初始化时候集中hlok的顺序
 * 1.BeanDefinitionRegistryPostProcessor.postProcessBeanDefinitionRegistry
 * 2.BeanFactoryPostProcessor.postProcessBeanFactory
 * 3.BeanPostProcessor
 * @author Jason Song(song_s@ctrip.com)
 */
public class ConfigPropertySourcesProcessor extends PropertySourcesProcessor
    implements BeanDefinitionRegistryPostProcessor {

  private ConfigPropertySourcesProcessorHelper helper = ServiceBootstrap.loadPrimary(ConfigPropertySourcesProcessorHelper.class);

  @Override
  public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
    helper.postProcessBeanDefinitionRegistry(registry);
  }
}
