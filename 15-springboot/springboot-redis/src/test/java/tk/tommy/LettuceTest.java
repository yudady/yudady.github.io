package tk.tommy;


import io.lettuce.core.RedisClient;
import io.lettuce.core.SetArgs;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.cluster.RedisClusterClient;
import java.time.Duration;
import java.util.concurrent.Flow;
import org.junit.jupiter.api.Test;
import java.util.concurrent.Future.*;
import java.util.concurrent.*;
import reactor.core.publisher.Flux;


public class LettuceTest {


    /**
     * 填上 Redis 地址，连接、执行、关闭。Perfect！
     */
    @Test
    public void test01() {
        // Syntax: redis://[password@]host[:port][/databaseNumber]
        // Syntax: redis://[username:password@]host[:port][/databaseNumber]
        RedisClient redisClient = RedisClient.create("redis://password@localhost:6379/0");
        StatefulRedisConnection<String, String> connection = redisClient.connect();
        RedisCommands<String, String> syncCommands = connection.sync();


        syncCommands.set("mykey", "Hello", SetArgs.Builder.ex(Duration.ofSeconds(10))); // redis> SETEX mykey 10 "Hello"

        // SETEX mykey 10 "Hello"

        connection.close();
        redisClient.shutdown();

    }

    @Test
    public void cluster() {
        // Syntax: redis://[password@]host[:port]
        // Syntax: redis://[username:password@]host[:port]
        RedisClusterClient redisClient = RedisClusterClient.create("redis://password@localhost:7379");
    }

    @Test
    public void sentinel() {
        // Syntax: redis-sentinel://[password@]host[:port][,host2[:port2]][/databaseNumber]#sentinelMasterId
        RedisClient redisClient = RedisClient.create("redis-sentinel://localhost:26379,localhost:26380/0#mymaster");
    }

    @Test
    public void test02() {

    }


}
