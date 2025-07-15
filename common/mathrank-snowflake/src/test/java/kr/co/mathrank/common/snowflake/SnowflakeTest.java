package kr.co.mathrank.common.snowflake;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class SnowflakeTest {
    @Test
    void 동시성_환경에서도_고유값_생성한다() {
        final Set<Long> set = Collections.synchronizedSet(new HashSet<>());

        final int latchCount = 100;
        final Snowflake snowflake = new Snowflake(1L);

        final Executor executor = Executors.newFixedThreadPool(10);
        final CountDownLatch countDownLatch = new CountDownLatch(latchCount);
        for (int i = 0; i < latchCount; i++) {
            executor.execute(() -> {
                set.add(snowflake.nextId());
                countDownLatch.countDown();
            });
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Assertions.assertThat(set.size()).isEqualTo(latchCount);
    }

    @Test
    void 분산환경에서도_고유값_생성한다() {
        final Set<Long> set = Collections.synchronizedSet(new HashSet<>());

        final int tryCount = 100;
        final int totalKeyCount = tryCount * 4;

        final Snowflake snowflake1 = new Snowflake(1L);
        final Snowflake snowflake2 = new Snowflake(2L);
        final Snowflake snowflake3 = new Snowflake(3L);
        final Snowflake snowflake4 = new Snowflake(4L);

        final Executor executor = Executors.newFixedThreadPool(100);

        final CountDownLatch countDownLatch = new CountDownLatch(tryCount);
        for (int i = 0; i < tryCount; i++) {
            executor.execute(() -> {
                set.add(snowflake1.nextId());
                set.add(snowflake2.nextId());
                set.add(snowflake3.nextId());
                set.add(snowflake4.nextId());

                countDownLatch.countDown();
            });
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Assertions.assertThat(set.size()).isEqualTo(totalKeyCount);
    }
}
