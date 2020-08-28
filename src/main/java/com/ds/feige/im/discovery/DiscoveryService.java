package com.ds.feige.im.discovery;

import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.ds.feige.im.common.util.NetworkUtils;
import com.ds.feige.im.common.util.RuntimeUtils;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
@Component
public class DiscoveryService {
    @NacosInjected
    NamingService namingService;
    @Value("${spring.application.name}")
    private String applicationName;
    @Value("${server.port}")
    private int port;
    private Map<String,Instance> instanceMap;
    private Instance instance;
    static final Logger LOGGER= LoggerFactory.getLogger(DiscoveryService.class);
    @PostConstruct
    void registerService() throws Exception{
        //服务注册
        final Instance self=new Instance();
        String ip= NetworkUtils.getIpAddress();
        self.setIp(ip);
        self.setPort(port);
        self.setInstanceId(ip+":"+port+"@"+ RuntimeUtils.getPid());
        self.setEphemeral(false);
        namingService.registerInstance(applicationName, self);
        LOGGER.info("Register service to nacos success:instanceId={}",self.getInstanceId());
        namingService.subscribe(applicationName, event -> {
            if(event instanceof NamingEvent){
                NamingEvent namingEvent=(NamingEvent)event;
                List<Instance> instanceList=namingEvent.getInstances();
                LOGGER.info("Fire naming event:{}",instanceList.toString());
                if(instanceList!=null&&!instanceList.isEmpty()){
                    instanceMap= Maps.newHashMap();
                    for(Instance i:instanceList){
                        if(i.isHealthy()){
                            instanceMap.put(i.getInstanceId(),i);
                        }
                    }
                }
            }
        });
        this.instance=self;
    }

    public String getInstanceId(){
        return this.instance.getInstanceId();
    }
    public Instance getInstance(){
        return this.instance;
    }

}
