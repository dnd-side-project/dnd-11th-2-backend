package com.dnd.runus.application.config;

import com.dnd.runus.global.event.AfterTransactionEvent;
import com.dnd.runus.global.event.BeforeTransactionEvent;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import static org.springframework.transaction.event.TransactionPhase.*;

@Component
public class TransactionEventHandler {

    private static final int AFTER_COMMIT_ORDER = 1;
    private static final int AFTER_ROLLBACK_ORDER = AFTER_COMMIT_ORDER + 1;
    private static final int AFTER_COMPLETION_ORDER = AFTER_COMMIT_ORDER + 2;

    /**
     * 트랜잭션이 commit되기 전에 실행
     */
    @Async
    @TransactionalEventListener(phase = BEFORE_COMMIT)
    public void processBeforeCommit(BeforeTransactionEvent event) {
        event.invoke();
    }

    /**
     * 트랜잭션이 commit된 후에 실행
     */
    @Async
    @Order(AFTER_COMMIT_ORDER)
    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void processAfterCommit(AfterTransactionEvent event) {
        event.invoke();
    }

    /**
     * 트랜잭션이 rollback된 후에 실행
     */
    @Async
    @Order(AFTER_ROLLBACK_ORDER)
    @TransactionalEventListener(phase = AFTER_ROLLBACK)
    public void processAfterRollback(AfterTransactionEvent event) {
        event.onTransactionRollback();
    }

    /**
     * 트랜잭션이 commit 또는 rollback된 후에 실행
     */
    @Async
    @Order(AFTER_COMPLETION_ORDER)
    @TransactionalEventListener(phase = AFTER_COMPLETION)
    public void processAfterCompletion(AfterTransactionEvent event) {
        event.onComplete();
    }
}
