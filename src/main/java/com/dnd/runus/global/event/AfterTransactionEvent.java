package com.dnd.runus.global.event;

public interface AfterTransactionEvent extends TransactionEvent {
    default void onTransactionRollback() {}

    default void onComplete() {}
}
