package com.example.stock.service;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class StockServiceTest {

    @Autowired
    private StockService stockService;

    @Autowired
    private StockRepository stockRepository;

    @BeforeEach // 테스트를 하려면 기존의 재고가 존재해야하기때문에 bofore로 돌려서 재고를 만든다.
    public void before() {
        Stock stock = new Stock(1L, 100L);
        stockRepository.saveAndFlush(stock);
    }

    @AfterEach // 테스트가 정상작동하면 넣었던 데이터 삭제 -> @Transactional 쓰는 것과 동일할 듯.
    public void after() {
        stockRepository.deleteAll();
    }

    @Test
    public void stock_decrease() {
        stockService.decrease(1L, 1L);
        
        // 100 - 1 = 99

        Stock stock = stockRepository.findById(1L).orElseThrow();
        Assertions.assertEquals(99, stock.getQuantity());
    }

    @Test
    public void 동시에_100개의_요청() throws InterruptedException {
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount); // Thread가 다 작업을 끝날때까지 기다린다.

        for(int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    stockService.decrease(1L, 1L);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        Stock stock = stockRepository.findById(1L).orElseThrow();
        // 100 개 저장 후 반복문을 통해 100 - (1 * 100) = 0 이 될 것으로 예상한다.
        Assertions.assertEquals(0L, stock.getQuantity());
        // -> 여러개의 쓰레드가 한꺼번에 db를 조회하고 -1를 하면서 순차적으로 계산이 되지 않는다. -> 원하는 값으로 나오지 않는다.
    }
}