package com.scaudachuang.catdetect.dao;

import com.scaudachuang.catlife.commons.model.TopHotDetection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author hiluyx
 * @since 2021/6/26 17:26
 **/
@Repository
public class RedisDao {
    private final Logger logger = LoggerFactory.getLogger(RedisDao.class);

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public Set<TopHotDetection> getAllTopHotZSet() {
        Set<Object> range = redisTemplate.opsForZSet().range(TopHotDetection.redisZSetKey, 0, -1);
        if (range == null) return null;
        return range.stream()
                .map(topHotDetection -> (TopHotDetection) topHotDetection)
                .collect(Collectors.toSet());
    }

    public List<TopHotDetection> getTopHotZSetN(int n) {
        List<TopHotDetection> allTopHotZSet = new ArrayList<>(getAllTopHotZSet());
        int size = allTopHotZSet.size();
        return allTopHotZSet.subList(size - n >= size ? n : 0, size);
    }

    public void addScore(TopHotDetection val, double score) {
        redisTemplate.opsForZSet().add(TopHotDetection.redisZSetKey, val, score);
    }

    public int rankTopHot(TopHotDetection val) {
        Long rank = redisTemplate.opsForZSet().rank(TopHotDetection.redisZSetKey, val);
        if (rank == null) return -1;
        return rank.intValue();
    }

    public String getDetectClass(String uuid) {
        if (uuid == null || uuid.length() <= 15)
            return null;
        return stringRedisTemplate.opsForValue().get(uuid);
    }
}
