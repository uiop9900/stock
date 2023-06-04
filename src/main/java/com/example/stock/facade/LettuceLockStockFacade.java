package com.example.stock.facade;

import com.example.stock.repository.RedisLockRepository;
import com.example.stock.service.StockService;
import org.aspectj.lang.annotation.RequiredTypes;
import org.springframework.stereotype.Component;

@Component
public class LettuceLockStockFacade  {

    private RedisLockRepository redisLockRepository;
    private StockService stockService;

    public LettuceLockStockFacade(RedisLockRepository redisLockRepository, StockService stockService) {
        this.redisLockRepository = redisLockRepository;
        this.stockService = stockService;
    }


    public void decrease(Long key, Long quantity) throws InterruptedException {
        while(!redisLockRepository.lock(key)) {
            Thread.sleep(100); // 해당 쓰레드가 lock을 잡지 못하면 잠깐 재운다.
        }

        try {
            stockService.decrease(key, quantity);
        } finally {
            redisLockRepository.unLock(key);
        }
    }


}
