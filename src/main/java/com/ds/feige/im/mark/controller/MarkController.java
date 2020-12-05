package com.ds.feige.im.mark.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.ds.feige.im.constants.SocketPaths;
import com.ds.feige.im.gateway.socket.annotation.SocketController;
import com.ds.feige.im.gateway.socket.annotation.SocketRequestMapping;
import com.ds.feige.im.mark.dto.*;
import com.ds.feige.im.mark.service.MarkMessageService;

/**
 * @author DC
 */
@SocketController
public class MarkController {
    @Autowired
    MarkMessageService markMessageService;

    @SocketRequestMapping(SocketPaths.CS_MARK_FAVORITES)
    public long favorites(@RequestBody @Valid FavoritesRequest body) {
        long markId = markMessageService.favorites(body);
        return markId;
    }

    @SocketRequestMapping(SocketPaths.CS_MARK_CANCEL_FAVORITES)
    boolean cancelFavorites(@RequestBody @Valid CancelFavoritesRequest request) {
        boolean result = markMessageService.cancelFavorites(request);
        return result;
    }

    @SocketRequestMapping(SocketPaths.CS_MARK_REMARK)
    long remark(@RequestBody @Valid RemarkRequest request) {
        return markMessageService.remark(request);
    }

    @SocketRequestMapping(SocketPaths.CS_MARK_CANCEL_REMARK)
    boolean cancelRemark(@RequestBody @Valid CancelRemarkRequest request) {
        boolean result = markMessageService.cancelRemark(request);
        return result;
    }
    @SocketRequestMapping(SocketPaths.CS_MARK_GET)
    MarkMessageInfo getByMarkId(@RequestParam(value = "markId") long markId) {
        return markMessageService.getByMarkId(markId);
    }

    @SocketRequestMapping(SocketPaths.CS_MARK_QUERY)
    List<MarkMessageInfo> query(@RequestBody @Valid MarkMessageQueryRequest request) {
        List<MarkMessageInfo> messageInfos = markMessageService.query(request);
        return messageInfos;
    }

}
