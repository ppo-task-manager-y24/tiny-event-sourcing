package ru.quipy.bankDemo.accounts.api

import ru.quipy.bankDemo.transfers.api.TRANSFER_DEPOSIT_COMPLETE
import ru.quipy.bankDemo.transfers.api.TRANSFER_DEPOSIT_FAILED
import ru.quipy.bankDemo.transfers.api.TRANSFER_WITHDRAW_COMPLETE
import ru.quipy.bankDemo.transfers.api.TRANSFER_WITHDRAW_FAILED
import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.math.BigDecimal
import java.util.*

const val ACCOUNT_CREATED = "ACCOUNT_CREATED_EVENT"
const val BANK_ACCOUNT_CREATED = "BANK_ACCOUNT_CREATED_EVENT"
const val BANK_ACCOUNT_DEPOSIT = "BANK_ACCOUNT_DEPOSIT_EVENT"
const val BANK_ACCOUNT_WITHDRAWAL = "BANK_ACCOUNT_WITHDRAWAL_EVENT"
const val INTERNAL_ACCOUNT_TRANSFER = "INTERNAL_ACCOUNT_TRANSFER_EVENT"

const val TRANSACTION_WITHDRAW_COMPLETE = "TRANSACTION_WITHDRAW_COMPLETE"
const val TRANSACTION_WITHDRAW_FAILED = "TRANSACTION_WITHDRAW_FAILED"
const val TRANSACTION_DEPOSIT_COMPLETE = "TRANSACTION_DEPOSIT_COMPLETE"
const val TRANSACTION_DEPOSIT_FAILED = "TRANSACTION_DEPOSIT_FAILED"
const val TRANSFER_TRANSACTION_PROCESSED = "TRANSFER_TRANSACTION_PROCESSED"
const val TRANSFER_TRANSACTION_ROLLBACKED = "TRANSFER_TRANSACTION_ROLLBACKED"


@DomainEvent(name = ACCOUNT_CREATED)
data class AccountCreatedEvent(
    val accountId: UUID,
    val userId: UUID,
) : Event<AccountAggregate>(
    name = ACCOUNT_CREATED,
)

@DomainEvent(name = BANK_ACCOUNT_CREATED)
data class BankAccountCreatedEvent(
    val accountId: UUID,
    val bankAccountId: UUID,
) : Event<AccountAggregate>(
    name = BANK_ACCOUNT_CREATED,
)

@DomainEvent(name = BANK_ACCOUNT_DEPOSIT)
data class BankAccountDepositEvent(
    val accountId: UUID,
    val bankAccountId: UUID,
    val amount: BigDecimal,
) : Event<AccountAggregate>(
    name = BANK_ACCOUNT_DEPOSIT,
)

@DomainEvent(name = BANK_ACCOUNT_WITHDRAWAL)
data class BankAccountWithdrawalEvent(
    val accountId: UUID,
    val bankAccountId: UUID,
    val amount: BigDecimal,
) : Event<AccountAggregate>(
    name = BANK_ACCOUNT_WITHDRAWAL,
)

@DomainEvent(name = INTERNAL_ACCOUNT_TRANSFER)
data class InternalAccountTransferEvent(
    val accountId: UUID,
    val bankAccountIdFrom: UUID,
    val bankAccountIdTo: UUID,
    val amount: BigDecimal,
) : Event<AccountAggregate>(
    name = INTERNAL_ACCOUNT_TRANSFER,
)

@DomainEvent(name = TRANSFER_WITHDRAW_COMPLETE)
data class TransferWithdrawCompletedEvent(
    val accountId: UUID,
    val bankAccountId: UUID,
    val transactionId: UUID,
    val transferAmount: BigDecimal,
) : Event<AccountAggregate>(
    name = TRANSFER_WITHDRAW_COMPLETE,
)

@DomainEvent(name = TRANSFER_WITHDRAW_FAILED)
data class TransferWithdrawFailedEvent(
    val accountId: UUID,
    val bankAccountId: UUID,
    val transactionId: UUID,
    val reason: String
) : Event<AccountAggregate>(
    name = TRANSFER_WITHDRAW_FAILED,
)

@DomainEvent(name = TRANSFER_DEPOSIT_COMPLETE)
data class TransferDepositCompletedEvent(
    val accountId: UUID,
    val bankAccountId: UUID,
    val transactionId: UUID,
    val transferAmount: BigDecimal
) : Event<AccountAggregate>(
    name = TRANSFER_DEPOSIT_COMPLETE,
)

@DomainEvent(name = TRANSFER_DEPOSIT_FAILED)
data class TransferDepositFailedEvent(
    val accountId: UUID,
    val bankAccountId: UUID,
    val transactionId: UUID,
    val reason: String
) : Event<AccountAggregate>(
    name = TRANSFER_DEPOSIT_FAILED,
)

@DomainEvent(name = TRANSFER_TRANSACTION_PROCESSED)
data class TransferTransactionProcessedEvent(
    val accountId: UUID,
    val bankAccountId: UUID,
    val transactionId: UUID,
) : Event<AccountAggregate>(
    name = TRANSFER_TRANSACTION_PROCESSED,
)

@DomainEvent(name = TRANSFER_TRANSACTION_ROLLBACKED)
data class TransferTransactionRollbackedEvent(
    val accountId: UUID,
    val bankAccountId: UUID,
    val transactionId: UUID,
) : Event<AccountAggregate>(
    name = TRANSFER_TRANSACTION_ROLLBACKED,
)