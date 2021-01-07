package com.ds.feige.im.gateway.service;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ds.base.nodepencies.exception.WarnMessageException;
import com.ds.feige.im.account.dto.LoginRequest;
import com.ds.feige.im.constants.FeigeWarn;
import com.ds.feige.im.gateway.dto.UserDeviceQuery;
import com.ds.feige.im.gateway.entity.UserDevice;
import com.ds.feige.im.gateway.mapper.UserDeviceMapper;
import com.ds.feige.im.push.dto.RegisterTokenRequest;
import com.google.common.base.Strings;

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
            device.setUserId(loginRequest.getUserId());
            device.setDeviceId(loginRequest.getDeviceId());
        }
        device.setDeviceType(loginRequest.getDeviceType().name());
        device.setDeviceName(loginRequest.getDeviceName());
        device.setLastLoginTime(new Date());
        device.setStatus(UserDevice.LOGIN_STATUS);
        saveOrUpdate(device);
        log.info("User device login:userId={},deviceId={},deviceType={}", loginRequest.getUserId(),
            loginRequest.getDeviceId(), loginRequest.getDeviceType());
    }

    @Override
    public void deviceLogout(long userId, String deviceId) {
        int i = baseMapper.updateDeviceStatus(userId, deviceId, UserDevice.LOGIN_STATUS, UserDevice.LOGOUT_STATUS);
        log.info("User device logout:userId={},deviceId={},updated={}", userId, deviceId, i == 1);
    }

    public List<UserDevice> queryDevices(UserDeviceQuery query) {
        QueryWrapper wrapper = new QueryWrapper();
        if (query.getUserIds() != null && !query.getUserIds().isEmpty()) {
            wrapper.in("user_id", query.getUserIds());
        }
        if (query.getStatusSet() != null && !query.getStatusSet().isEmpty()) {
            wrapper.in("status", query.getStatusSet());
        }
        if (Strings.isNullOrEmpty(query.getDeviceId())) {
            wrapper.eq("device_id", query.getDeviceId());
        }
        if (Strings.isNullOrEmpty(query.getDeviceType())) {
            wrapper.eq("device_type", query.getDeviceType());
        }
        List<UserDevice> devices = baseMapper.selectList(wrapper);
        return devices;
    }

    @Override
    public List<UserDevice> getPushableDevices(long userId) {
        List<UserDevice> userDevices = baseMapper.getUserPushableDevices(userId, UserDevice.LOGIN_STATUS);
        return userDevices;
    }

    @Override
    public void registerToken(RegisterTokenRequest request) {
        long userId = request.getUserId();
        String deviceId = request.getDeviceId();
        UserDevice userDevice = baseMapper.getByUserIdAndDeviceId(userId, deviceId);
        if (userDevice == null) {
            throw new WarnMessageException(FeigeWarn.DEVICE_NOT_EXISTS);
        }
        userDevice.setDeviceToken(request.getDeviceToken());
        save(userDevice);
        log.info("Register device token success:userId={},deviceId={},token={}", userDevice, deviceId,
            request.getDeviceToken());
    }
}
