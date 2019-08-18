package com.ctrip.framework.apollo.metaservice.controller;

import com.ctrip.framework.apollo.core.dto.ServiceDTO;
import com.ctrip.framework.apollo.metaservice.service.DiscoveryService;
import com.netflix.appinfo.InstanceInfo;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/services")
public class ServiceController {

  private final DiscoveryService discoveryService;

  private static Function<InstanceInfo, ServiceDTO> instanceInfoToServiceDTOFunc = instance -> {
    ServiceDTO service = new ServiceDTO();
    service.setAppName(instance.getAppName());
    service.setInstanceId(instance.getInstanceId());
    service.setHomepageUrl(instance.getHomePageUrl());
    return service;
  };

  public ServiceController(final DiscoveryService discoveryService) {
    this.discoveryService = discoveryService;
  }


  //youzhihao:这个接口好像没有用到
  @RequestMapping("/meta")
  public List<ServiceDTO> getMetaService() {
    List<InstanceInfo> instances = discoveryService.getMetaServiceInstances();
    List<ServiceDTO> result = instances.stream().map(instanceInfoToServiceDTOFunc).collect(Collectors.toList());
    return result;
  }

  //youzhihao:获取config所有服务的ip
  @RequestMapping("/config")
  public List<ServiceDTO> getConfigService(
      @RequestParam(value = "appId", defaultValue = "") String appId,
      @RequestParam(value = "ip", required = false) String clientIp) {
    List<InstanceInfo> instances = discoveryService.getConfigServiceInstances();
    List<ServiceDTO> result = instances.stream().map(instanceInfoToServiceDTOFunc).collect(Collectors.toList());
    return result;
  }

  //youzhihao:获取admin所有服务的ip
  @RequestMapping("/admin")
  public List<ServiceDTO> getAdminService() {
    List<InstanceInfo> instances = discoveryService.getAdminServiceInstances();
    List<ServiceDTO> result = instances.stream().map(instanceInfoToServiceDTOFunc).collect(Collectors.toList());
    return result;
  }
}
