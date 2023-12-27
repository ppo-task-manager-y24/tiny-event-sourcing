package ru.quipy.bankDemo.transfers.api

import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.math.BigDecimal
import java.util.*

const val TRANSFER_TRANSACTION_CREATED = "TRANSFER_TRANSACTION_CREATED"
const val TRANSFER_COMPLETE = "TRANSFER_COMPLETE"
const val TRANSFER_FAILED = "TRANSFER_FAILED"
const val TRANSFER_DEPOSIT_COMPLETE = "TRANSFER_DEPOSIT_COMPLETE"
const val TRANSFER_DEPOSIT_FAILED = "TRANSFER_DEPOSIT_FAILED"
const val TRANSFER_WITHDRAW_COMPLETE = "TRANSFER_WITHDRAW_COMPLETE"
const val TRANSFER_WITHDRAW_FAILED = "TRANSFER_WITHDRAW_FAILED"
const val TRANSFER_HALF_FAILED = "TRANSFER_HALF_FAILED"

@DomainEvent(name = TRANSFER_TRANSACTION_CREATED)
data class TransferTransactionCreatedEvent(
    val transferId: UUID,
    val sourceAccountId: UUID,
    val sourceBankAccountId: UUID,
    val destinationAccountId: UUID,
    val destinationBankAccountId: UUID,
    val transferAmount: BigDecimal,
) : Event<TransferTransactionAggregate>(
    name = TRANSFER_TRANSACTION_CREATED,
)

@DomainEvent(name = TRANSFER_COMPLETE)
data class TransactionCompleteEvent(
    val transferId: UUID,
    val sourceAccountId: UUID,
    val sourceBankAccountId: UUID,
    val destinationAccountId: UUID,
    val destinationBankAccountId: UUID,
) : Event<TransferTransactionAggregate>(
    name = TRANSFER_COMPLETE,
)

@DomainEvent(name = TRANSFER_FAILED)
data class TransactionFailedEvent(
    val transferId: UUID,
    val sourceAccountId: UUID,
    val sourceBankAccountId: UUID,
    val destinationAccountId: UUID,
    val destinationBankAccountId: UUID,
) : Event<TransferTransactionAggregate>(
    name = TRANSFER_FAILED,
)

@DomainEvent(name = TRANSFER_DEPOSIT_COMPLETE)
data class TransactionDepositCompleteEvent(
    val transferId: UUID,
    val destinationAccountId: UUID,
    val destinationBankAccountId: UUID,
) : Event<TransferTransactionAggregate>(
    name = TRANSFER_DEPOSIT_COMPLETE,
)

@DomainEvent(name = TRANSFER_DEPOSIT_FAILED)
data class TransactionDepositFailedEvent(
    val transferId: UUID,
    val sourceAccountId: UUID,
    val sourceBankAccountId: UUID,
    val destinationAccountId: UUID,
    val destinationBankAccountId: UUID,
) : Event<TransferTransactionAggregate>(
    name = TRANSFER_DEPOSIT_FAILED,
)

@DomainEvent(name = TRANSFER_WITHDRAW_COMPLETE)
data class TransactionWithdrawCompleteEvent(
    val transferId: UUID,
    val sourceAccountId: UUID,
    val sourceBankAccountId: UUID,
) : Event<TransferTransactionAggregate>(
    name = TRANSFER_WITHDRAW_COMPLETE,
)

@DomainEvent(name = TRANSFER_WITHDRAW_FAILED)
data class TransactionWithdrawFailedEvent(
    val transferId: UUID,
    val sourceAccountId: UUID,
    val sourceBankAccountId: UUID,
    val destinationAccountId: UUID,
    val destinationBankAccountId: UUID,
) : Event<TransferTransactionAggregate>(
    name = TRANSFER_WITHDRAW_FAILED,
)

@DomainEvent(name = TRANSFER_HALF_FAILED)
data class TransactionHalfFailedEvent(
    val transferId: UUID,
    val accountId: UUID,
    val bankAccountId: UUID
): Event<TransferTransactionAggregate>(
    name = TRANSFER_HALF_FAILED
)
