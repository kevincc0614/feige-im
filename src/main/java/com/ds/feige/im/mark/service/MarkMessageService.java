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
     * 收藏消息
     */
    long favorites(FavoritesRequest request);

    /**
     * 取消收藏
     */
    boolean cancelFavorites(CancelFavoritesRequest cancelFavoritesRequest);
    /**
     * 更新备注
     * 
     * @param request
     */
    long remark(RemarkRequest request);

    boolean cancelRemark(CancelRemarkRequest request);

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
