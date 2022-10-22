package io.github.yudady.spring;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import javax.annotation.Nullable;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

public class JsonRedisSerializer implements RedisSerializer<Object> {

    private final ObjectMapper mapper;

    public JsonRedisSerializer() {
        this.mapper = new ObjectMapper();
        this.mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        this.mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }


    @Override
    public Object deserialize(@Nullable byte[] bytes) {
        if (bytes == null) return null;

        try {
            return mapper.readValue(bytes, Object.class);
        } catch (Exception e) {
            throw new SerializationException(e.getMessage(), e);
        }
    }


    @Override
    public byte[] serialize(@Nullable Object object) {
        try {
            return mapper.writeValueAsBytes(object);
        } catch (JsonProcessingException e) {
            throw new SerializationException(e.getMessage(), e);
        }
    }

}