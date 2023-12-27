package ru.quipy.bankDemo.transfers.logic

import ru.quipy.bankDemo.accounts.api.TransferWithdrawFailedEvent
import ru.quipy.bankDemo.transfers.api.*
import ru.quipy.bankDemo.transfers.logic.TransferTransaction.TransactionState.*
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import ru.quipy.domain.Event
import java.math.BigDecimal
import java.util.*

class TransferTransaction : AggregateState<UUID, TransferTransactionAggregate> {
    private lateinit var transferId: UUID
    internal var transactionState = CREATED

    private lateinit var sourceAccountId: UUID
    private lateinit var sourceBankAccountId: UUID
    private lateinit var destinationAccountId: UUID
    private lateinit var destinationBankAccountId: UUID

    private lateinit var transferAmount: BigDecimal

    override fun getId() = transferId

    fun initiateTransferTransaction(
        id: UUID = UUID.randomUUID(),
        sourceAccountId: UUID,
        sourceBankAccountId: UUID,
        destinationAccountId: UUID,
        destinationBankAccountId: UUID,
        transferAmount: BigDecimal
    ): TransferTransactionCreatedEvent {
        // todo sukhoa validation
        return TransferTransactionCreatedEvent(
            id,
            sourceAccountId,
            sourceBankAccountId,
            destinationAccountId,
            destinationBankAccountId,
            transferAmount
        )
    }

    fun transactionWithdrawComplete(
        id: UUID
    ): Event<TransferTransactionAggregate> {
        if (transactionState == FAILED) {
            return TransactionDepositFailedEvent(
                id,
                this.sourceAccountId,
                this.sourceBankAccountId,
                this.destinationAccountId,
                this.destinationBankAccountId
            )
        }
        if (transactionState == CREATED) {
            return TransactionWithdrawCompleteEvent(
                id,
                this.sourceAccountId,
                this.sourceBankAccountId
            )
        }

        return TransactionCompleteEvent(
            id,
            this.sourceAccountId,
            this.sourceBankAccountId,
            this.destinationAccountId,
            this.destinationBankAccountId
        )
    }

    fun transactionWithdrawFailed(
        id: UUID
    ): Event<TransferTransactionAggregate> {
        if (transactionState == FAILED) {
            return TransactionFailedEvent(
                id,
                this.sourceAccountId,
                this.sourceBankAccountId,
                this.destinationAccountId,
                this.destinationBankAccountId
            )
        }
        if (transactionState == CREATED) {
            return TransactionHalfFailedEvent(
                id,
                this.sourceAccountId,
                this.sourceBankAccountId
            )
        }

        return TransactionWithdrawFailedEvent(
            id,
            this.sourceAccountId,
            this.sourceBankAccountId,
            this.destinationAccountId,
            this.destinationBankAccountId
        )
    }

    fun transactionDepositComplete(
        id: UUID
    ): Event<TransferTransactionAggregate> {
        if (transactionState == FAILED) {
            return TransactionWithdrawFailedEvent(
                id,
                this.sourceAccountId,
                this.sourceBankAccountId,
                this.destinationAccountId,
                this.destinationBankAccountId
            )
        }
        if (transactionState == CREATED) {
            return TransactionDepositCompleteEvent(
                id,
                this.destinationAccountId,
                this.destinationBankAccountId
            )
        }

        return TransactionCompleteEvent(
            id,
            this.sourceAccountId,
            this.sourceBankAccountId,
            this.destinationAccountId,
            this.destinationBankAccountId
        )
    }

    fun transactionDepositFailed(
        id: UUID
    ): Event<TransferTransactionAggregate> {
        if (transactionState == FAILED) {
            return TransactionFailedEvent(
                id,
                this.sourceAccountId,
                this.sourceBankAccountId,
                this.destinationAccountId,
                this.destinationBankAccountId
            )
        }
        if (transactionState == CREATED) {
            return TransactionHalfFailedEvent(
                id,
                this.destinationAccountId,
                this.destinationBankAccountId
            )
        }

        return TransactionDepositFailedEvent(
            id,
            this.sourceAccountId,
            this.sourceBankAccountId,
            this.destinationAccountId,
            this.destinationBankAccountId
        )
    }

    @StateTransitionFunc
    fun initiateTransferTransaction(event: TransferTransactionCreatedEvent) {
        this.transferId = event.transferId
        this.sourceAccountId = event.sourceAccountId
        this.sourceBankAccountId = event.sourceBankAccountId
        this.destinationAccountId = event.destinationAccountId
        this.destinationBankAccountId = event.destinationBankAccountId
    }

    @StateTransitionFunc
    fun completed(event: TransactionCompleteEvent) {
        transactionState = COMPLETED
    }

    @StateTransitionFunc
    fun failed(event: TransactionFailedEvent) {
        transactionState = FAILED
    }

    @StateTransitionFunc
    fun halfFailed(event: TransactionHalfFailedEvent) {
        transactionState = FAILED
    }

    @StateTransitionFunc
    fun withdrawCompleted(event: TransactionWithdrawCompleteEvent) {
        transactionState = HALF_CONFIRMED
    }

    @StateTransitionFunc
    fun withdrawFailed(event: TransactionWithdrawFailedEvent) {
        transactionState = FAILED
    }

    @StateTransitionFunc
    fun depositCompleted(event: TransactionDepositCompleteEvent) {
        transactionState = COMPLETED
    }

    @StateTransitionFunc
    fun depositFailed(event: TransactionDepositFailedEvent) {
        transactionState = FAILED
    }

    enum class TransactionState {
        CREATED,
        HALF_CONFIRMED,
        COMPLETED,
        FAILED
    }
}