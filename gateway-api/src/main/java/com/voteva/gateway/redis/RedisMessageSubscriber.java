package com.voteva.gateway.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.voteva.gateway.redis.model.Task;
import com.voteva.gateway.redis.model.Topic;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.io.IOException;

@Service
public class RedisMessageSubscriber {

    private final Jedis jedis;
    private final RetryService retryService;
    private final ObjectMapper objectMapper;

    @Autowired
    public RedisMessageSubscriber(
            Jedis jedis,
            RetryService retryService) {
        this.jedis = jedis;
        this.retryService = retryService;
        this.objectMapper = new ObjectMapper();
    }

    @Scheduled(fixedRate = 10000)
    public void executeTask() throws IOException {
        String taskStr = jedis.rpop(Topic.TESTS_DELETE.getName());

        if (StringUtils.isNotEmpty(taskStr)) {
            Task task = objectMapper.readValue(taskStr, Task.class);
            retryService.retry(task, Topic.TESTS_DELETE_30.getName());
        }
    }

    @Scheduled(fixedRate = 30000)
    public void executeTask30() throws IOException {
        String taskStr = jedis.rpop(Topic.TESTS_DELETE_30.getName());

        if (StringUtils.isNotEmpty(taskStr)) {
            Task task = objectMapper.readValue(taskStr, Task.class);
            retryService.retry(task, Topic.TESTS_DELETE_60.getName());
        }
    }

    @Scheduled(fixedRate = 60000)
    public void executeTask60() throws IOException {
        String taskStr = jedis.rpop(Topic.TESTS_DELETE_60.getName());

        if (StringUtils.isNotEmpty(taskStr)) {
            Task task = objectMapper.readValue(taskStr, Task.class);
            retryService.retry(task, Topic.TESTS_DELETE_60.getName());
        }
    }
}
