package com.ds.feige.im.favorite.service;

import com.ds.feige.im.favorite.dto.*;

import java.util.List;

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
     * 取消标记
     */
    boolean cancelMark(CancelMarkRequest request);

    /**
     * 更新备注
     */
    void updateRemark(UpdateRemarkRequest request);

    /**
     * 查询
     */
    List<MarkMessageInfo> query(MarkMessageQueryRequest request);

    MarkMessageInfo getByMarkId(long id);
}
