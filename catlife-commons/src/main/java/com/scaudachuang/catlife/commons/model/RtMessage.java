package com.scaudachuang.catlife.commons.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author hiluyx
 * @since 2021/9/1 11:06
 **/

@Data
@AllArgsConstructor
public class RtMessage<M> {
    private int status;
    private String errMsg;
    private M data;
    public RtMessage() {}

    public static <M> RtMessage<M> OK(M data) {
        RtMessage<M> msg = new RtMessage<>();
        msg.status = 0;
        msg.errMsg = "OK";
        msg.data = data;
        return msg;
    }

    public static <M> RtMessage<M> ERROR(int status, String errMsg, M data) {
        RtMessage<M> msg = new RtMessage<>();
        msg.status = status;
        msg.errMsg = errMsg;
        msg.data = data;
        return msg;
    }

    public static <M> RtMessage<M> INSERT_BOOL(boolean insert, M m) {
        RtMessage<M> msg = new RtMessage<>();
        msg.status = insert ? 0 : 15600;
        msg.data = m;
        return msg;
    }
}