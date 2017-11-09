package com.ctrip.framework.apollo.spi;

import java.util.Map;

import com.ctrip.framework.apollo.build.ApolloInjector;
import com.google.common.collect.Maps;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public class DefaultConfigFactoryManager implements ConfigFactoryManager {
  private ConfigRegistry m_registry;

  private Map<String, ConfigFactory> m_factories = Maps.newConcurrentMap();

  public DefaultConfigFactoryManager() {
    m_registry = ApolloInjector.getInstance(ConfigRegistry.class);
  }

  @Override
  public ConfigFactory getFactory(String namespace) {
    //youzhihao:从注册机中获取configFactory，优先级最高，当用户自定义configFacotry并且注册到才获取到
    // step 1: check hacked factory
    ConfigFactory factory = m_registry.getFactory(namespace);

    if (factory != null) {
      return factory;
    }

    // step 2: check cache
    factory = m_factories.get(namespace);

    if (factory != null) {
      return factory;
    }

    // step 3: check declared config factory
    factory = ApolloInjector.getInstance(ConfigFactory.class, namespace);

    if (factory != null) {
      return factory;
    }

    // step 4: check default config factory
    factory = ApolloInjector.getInstance(ConfigFactory.class);

    m_factories.put(namespace, factory);

    // factory should not be null
    return factory;
  }
}
