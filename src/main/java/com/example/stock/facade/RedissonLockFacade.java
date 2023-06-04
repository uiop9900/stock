package com.example.stock.facade;

import com.example.stock.service.StockService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedissonLockFacade {

    private RedissonClient redissonClient; // import 해서 자동으로 class가 존재한다.

    private StockService stockService;

    public RedissonLockFacade(RedissonClient redissonClient, StockService stockService) {
        this.redissonClient = redissonClient;
        this.stockService = stockService;
    }

    public void decrease(Long key, Long quantity) {
        RLock lock = redissonClient.getLock(key.toString()); // key를 넣어서 lock 객체를 가지고 온다.

        try {
            boolean available = lock.tryLock(5, 1, TimeUnit.SECONDS); // 5번 1초동안 lock 획득을 시도한다.

            if (!available) { // lock 획득에 실패했다면,
                System.out.println("lock 획득 실패");
                return;
            }

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock(); // lock을 푼다.
        }
    }
}
