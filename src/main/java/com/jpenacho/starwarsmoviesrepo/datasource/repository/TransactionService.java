package com.jpenacho.starwarsmoviesrepo.datasource.repository;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import reactor.core.publisher.Mono;

import java.util.function.Supplier;

public abstract class TransactionService {
    private final TransactionTemplate tx;
    private final TransactionTemplate readOnlyTx;

    protected TransactionService(PlatformTransactionManager transactionManager) {
        this.tx = new TransactionTemplate(transactionManager);
        this.readOnlyTx = new TransactionTemplate(transactionManager);
        this.readOnlyTx.setReadOnly(true);
    }

    public final <T> Mono<T> startTx(Supplier<T> supplier) {
        return Mono.fromSupplier(() -> this.startTxSync(supplier));
    }

    public final <T> T startTxSync(Supplier<T> supplier) {
        return this.tx.execute((status) -> supplier.get());
    }

    public final Mono<Void> startTx(Runnable runnable) {
        return Mono.fromRunnable(() -> this.startTxSync(runnable));
    }

    public final void startTxSync(Runnable runnable) {
        this.tx.executeWithoutResult((status) -> runnable.run());
    }

    public final <T> Mono<T> readOnlyTx(Supplier<T> supplier) {
        return Mono.fromSupplier(() -> this.readOnlyTxSync(supplier));
    }

    public final <T> T readOnlyTxSync(Supplier<T> supplier) {
        return this.readOnlyTx.execute((status) -> supplier.get());
    }

    public final Mono<Void> readOnlyTx(Runnable runnable) {
        return Mono.fromRunnable(() -> this.readOnlyTxSync(runnable));
    }

    public final void readOnlyTxSync(Runnable runnable) {
        this.readOnlyTx.executeWithoutResult((status) -> runnable.run());
    }
}
