package com.example.stock.service;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class StockService {
    private StockRepository stockRepository;

    public StockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    /**
     * 재고 감소 메소드
     * 하나의 thread만 db에 접근하게끔 만든다.
     */
    //@Transactional
    // Transactional은 결과값을 바로 db에 반영하는게 아니라 commit 된 후에 db에 반영된다.
    // 그 반영되기까지 시간동안 다른 Thread가 db에 접근하게 되면 동시성 이슈가 발생한다.
    public synchronized void decrease(Long id, Long quantity) {
        // get으로 stock을 가지고 온 다음
        // 재고 감소
        // 저장
        Stock stock = stockRepository.findById(id).orElseThrow();
        stock.decrease(quantity);
        stockRepository.saveAndFlush(stock);
    }
}
