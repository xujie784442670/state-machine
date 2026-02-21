package cn.jasonone.sm.core;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import cn.jasonone.sm.domain.Transition;
import cn.jasonone.sm.support.State;
import cn.jasonone.sm.support.StateMachineManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class RedisStateMachineManager implements StateMachineManager {

    private final RedisTemplate<String, String> redisTemplate;

    private static final ObjectMapper MAPPER = new ObjectMapper();


    private State readState(String key) {
        String json = redisTemplate.opsForValue().get(key + ":value");
        String className = redisTemplate.opsForValue().get(key + ":type");
        if (StrUtil.hasBlank(json, className)) {
            return null;
        }
        Class<?> type = ClassUtil.getClass(className);
        try {
            return (State) MAPPER.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeState(String key, State state, Duration timeout) {
        try {
            String value = null;
            if (state != null) {
                value = MAPPER.writeValueAsString(state);
                redisTemplate.opsForValue().set(key + ":type", state.getClass().getName(), timeout);
            }
            redisTemplate.opsForValue().set(key + ":value", value, timeout);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeState(String key, State state) {
        writeState(key,  state, Duration.ofMillis(0));
    }


    @Override
    public void setInitialState(String machineCode, State initialState) {
        // 获取初始状态
        String key = machineCode + ":initialState";
        State oldState = readState(key);
        Assert.isFalse(oldState == null || oldState.equals(initialState), "状态机[{}]已存在初始状态", machineCode);
        writeState(key, initialState);
    }

    @Override
    public State getInitialState(String machineCode) {
        return readState(machineCode + ":initialState");
    }

    @Override
    public State getCurrentState(String machineCode, String businessId) {
        String key = machineCode + ":currentState:" + businessId;
        State state = readState(key);
        if (state == null) {
            writeState(key, null, Duration.ofMinutes(1));
        }
        return state;
    }

    @Override
    public void saveTransition(String machineCode, Transition transition) {
        String key = machineCode + ":transitions";
        try {
            String json = MAPPER.writeValueAsString(transition);
            redisTemplate.opsForList().leftPush(key, json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Set<Transition> getTransitions(String machineCode) {
        String key = machineCode + ":transitions";
        List<String> range = redisTemplate.opsForList().range(key, 0, -1);
        if (range == null) {
            return null;
        }
        return range.stream().map(json -> {
            try {
                return MAPPER.readValue(json, Transition.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toSet());
    }

    @Override
    public boolean updateState(String machineCode, String businessId, State state) {
        String key = machineCode + ":currentState:" + businessId;
        writeState(key, state);
        return true;
    }
}
