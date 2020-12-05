package com.ds.feige.im.gateway.service;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ds.base.nodepencies.exception.WarnMessageException;
import com.ds.feige.im.account.dto.LoginRequest;
import com.ds.feige.im.constants.FeigeWarn;
import com.ds.feige.im.gateway.entity.UserDevice;
import com.ds.feige.im.gateway.mapper.UserDeviceMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * 用户设备管理
 *
 * @author DC
 */
@Service
@Slf4j
public class UserDeviceServiceImpl extends ServiceImpl<UserDeviceMapper, UserDevice> implements UserDeviceService {
    @Override
    public void deviceLogin(LoginRequest loginRequest) {
        // 判断是更新还是新增
        UserDevice device = baseMapper.getByUserIdAndDeviceId(loginRequest.getUserId(), loginRequest.getDeviceId());
        if (device == null) {
            device = new UserDevice();
            device.setDeviceId(loginRequest.getDeviceId());
            device.setDeviceName(loginRequest.getDeviceName());
            device.setDeviceType(loginRequest.getDeviceType().name());
            device.setStatus(1);
            device.setUserId(loginRequest.getUserId());
            device.setDeviceToken(loginRequest.getDeviceToken());
        }
        device.setLastLoginTime(new Date());
        saveOrUpdate(device);
        log.info("User device login:userId={},deviceId={},deviceType={}", loginRequest.getUserId(),
            loginRequest.getDeviceId(), loginRequest.getDeviceType());
    }

    @Override
    public void deviceLogout(long userId, String deviceId) {
        UserDevice device = baseMapper.getByUserIdAndDeviceId(userId, deviceId);
        if (device == null) {
            throw new WarnMessageException(FeigeWarn.DEVICE_NOT_EXISTS, deviceId);
        }
        device.setStatus(-1);
        updateById(device);
        log.info("User device logout:userId={},deviceId={},deviceType={}", userId, deviceId, device.getDeviceType());
    }

    @Override
    public List<UserDevice> getDevices(long userId, int status) {
        List<UserDevice> devices = baseMapper.getDevicesByUserId(userId, status);
        return devices;
    }
}
