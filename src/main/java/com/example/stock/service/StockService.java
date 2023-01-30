package com.example.stock.service;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional(propagation = Propagation.REQUIRES_NEW) // NamedLockFacade와는 다르게 Transaction되어야 해서 설정 넣음
    public void decrease(Long id, Long quantity) { // synchronized는 하나의 프로세스에서만 동시성을 보장한다. 서버가 여러대면 이슈 발생.-> 거의 사용하지 않음.
        // get으로 stock을 가지고 온 다음
        // 재고 감소
        // 저장
        Stock stock = stockRepository.findById(id).orElseThrow();
        stock.decrease(quantity);
        stockRepository.saveAndFlush(stock);
    }
}
