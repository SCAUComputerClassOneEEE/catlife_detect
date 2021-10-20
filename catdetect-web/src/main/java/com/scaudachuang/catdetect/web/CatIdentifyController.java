package com.scaudachuang.catdetect.web;

import com.scaudachuang.catdetect.dao.RedisDao;
import com.scaudachuang.catdetect.utils.ImageUtilJnb;
import com.scaudachuang.catlife.commons.model.RtMessage;
import com.scaudachuang.catlife.commons.model.TopHotDetection;
import com.scaudachuang.catlife.commons.model.UserSession;
import com.scaudachuang.catlife.commons.utils.HttpHelper;
import com.scaudachuang.catlife.commons.utils.LimitProcessing;
import com.scaudachuang.catlife.commons.utils.TaskPool;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.*;

/**
 * @author hiluyx
 * @since 2021/8/26 21:50
 **/
@RestController
@RequestMapping("/public")
public class CatIdentifyController {
    @Resource
    private RedisDao redisDao;
    @Resource
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/topIdentity")
    @LimitProcessing(name = "topHot", ratePerSec = 1)
    public RtMessage<Map<String, Object>> getTopDetect(@RequestParam(value = "top", required = false) int top) {
        if (top <= 0)
            top = 10;
        List<TopHotDetection> topHotZSetN = redisDao.getTopHotZSetN(top);
        HashMap<String, Object> map = new HashMap<>();
        map.put("tops", topHotZSetN);
        map.put("nums", topHotZSetN.size());
        return RtMessage.OK(map);
    }

    @PostMapping("/catDetect")
    public RtMessage<String/*uuid*/> detectUpload(
            @RequestParam("img") MultipartFile file,
            HttpServletRequest request) throws Exception {
        UserSession userSessionValue = HttpHelper.getUserSessionValue(request);
        String s = UUID.randomUUID().toString();
        userSessionValue.setNowTaskUUID(s);
        userSessionValue.setTaskNum(userSessionValue.getTaskNum() + 1);
        TaskPool.execute(()->{
            // file -> MQ
            try {
                byte[] bytes = ImageUtilJnb.compressMMultipartFile(file);
                File save = new File("D:\\321322198511205873F.jpg");
                FileOutputStream fileOutputStream = new FileOutputStream(save);
                fileOutputStream.write(bytes);
                fileOutputStream.close();
//                rabbitTemplate.convertAndSend(RabbitmqConfig.exchange, RabbitmqConfig.routingKey, bytes, new CorrelationData(s));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return RtMessage.OK(s);
    }

    @GetMapping("/detectClass")
    public RtMessage<String> getDetectClass(@RequestParam(value = "uuid") String uuid) throws Exception {
        String detectClass = redisDao.getDetectClass(uuid);
        if (detectClass == null) {
            return RtMessage.ERROR(400, "no record", "");
        }
        return RtMessage.OK(detectClass);
    }
}
