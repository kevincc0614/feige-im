package com.ds.feige.im.mark.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.ds.base.nodepencies.api.Response;
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

    @SocketRequestMapping(SocketPaths.CS_MARK_ADD)
    public long mark(@RequestBody @Valid MarkRequest body) {
        long markId = markMessageService.mark(body);
        return markId;
    }

    @SocketRequestMapping(SocketPaths.CS_MARK_GET)
    MarkMessageInfo getByMarkId(@RequestParam(value = "markId") long markId) {
        return markMessageService.getByMarkId(markId);
    }

    @SocketRequestMapping(SocketPaths.CS_MARK_DELETE)
    boolean cancelMark(CancelMarkRequest request) {
        boolean success = markMessageService.cancelMark(request);
        return success;
    }

    @SocketRequestMapping(SocketPaths.CS_MARK_UPDATE)
    Response updateRemark(@RequestBody @Valid UpdateRemarkRequest request) {
        markMessageService.updateRemark(request);
        return Response.EMPTY_SUCCESS;
    }

    @SocketRequestMapping(SocketPaths.CS_MARK_QUERY)
    Response<List<MarkMessageInfo>> query(@RequestBody @Valid MarkMessageQueryRequest request) {
        List<MarkMessageInfo> messageInfos = markMessageService.query(request);
        return new Response<>(messageInfos);
    }

}
