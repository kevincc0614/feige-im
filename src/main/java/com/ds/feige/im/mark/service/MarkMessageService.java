package com.ds.feige.im.mark.service;

import java.util.List;

import com.ds.feige.im.mark.dto.*;

/**
 * 收藏服务
 *
 * @author DC
 */
public interface MarkMessageService {
    /**
     * 标记消息
     */
    long mark(MarkRequest request);

    /**
     * 删除标记
     * 
     * @param request
     */
    boolean cancelMark(CancelMarkRequest request);

    /**
     * 更新备注
     * 
     * @param request
     */
    void updateRemark(UpdateRemarkRequest request);

    /**
     * 查询
     * 
     * @param request
     */
    List<MarkMessageInfo> query(MarkMessageQueryRequest request);

    /**
     * 获取标记详情
     * 
     * @param id
     */
    MarkMessageInfo getByMarkId(long id);
}
