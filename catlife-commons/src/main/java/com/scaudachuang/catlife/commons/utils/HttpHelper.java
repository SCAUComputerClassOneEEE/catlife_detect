package com.scaudachuang.catlife.commons.utils;

import com.scaudachuang.catlife.commons.model.RtMessage;
import com.scaudachuang.catlife.commons.model.UserSession;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;

/**
 * @author hiluyx
 * @since 2021/9/1 11:14
 **/
public class HttpHelper {
    public static final String USER_SESSION_ATTR_STRING = "usr_session";

    public static UserSession getUserSessionValue(HttpServletRequest request) throws Exception{
        return HttpHelper.getSessionVal(UserSession.class, request, USER_SESSION_ATTR_STRING);
    }

    public static <M> M getSessionVal(Class<M> mClass,
                                      HttpServletRequest request,
                                      String sessionKey)
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        HttpSession session = request.getSession();
        Object attribute = session.getAttribute(sessionKey);
        if (attribute == null) {
            M m = mClass.getConstructor().newInstance();
            session.setAttribute(sessionKey, m);
            return m;
        } else {
            return (M) attribute;
        }
    }

    public static void setUserSessionId(HttpServletRequest request, long defineOnlineStatus) throws Exception {
        UserSession userSessionValue = getUserSessionValue(request);
        userSessionValue.setDefineOnlineStatus(defineOnlineStatus);
    }

    public static <T> void errMsgResponse(HttpServletResponse response, int status, String msg, T data) {
        try {
            response.setStatus(status);
            response.setHeader("Content-Type", "application/json;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.println(JSONUtil.writeValue(RtMessage.ERROR(status, msg, data)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
