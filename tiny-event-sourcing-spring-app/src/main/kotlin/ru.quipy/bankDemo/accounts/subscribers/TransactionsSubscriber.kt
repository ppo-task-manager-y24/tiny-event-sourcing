package ru.quipy.bankDemo.accounts.subscribers

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import ru.quipy.bankDemo.accounts.api.AccountAggregate
import ru.quipy.bankDemo.accounts.logic.Account
import ru.quipy.bankDemo.transfers.api.*
import ru.quipy.core.EventSourcingService
import ru.quipy.saga.SagaManager
import ru.quipy.streams.AggregateSubscriptionsManager
import java.util.*
import javax.annotation.PostConstruct

@Component
class TransactionsSubscriber(
    private val subscriptionsManager: AggregateSubscriptionsManager,
    private val accountEsService: EventSourcingService<UUID, AccountAggregate, Account>,
    private val sagaManager: SagaManager
) {
    private val logger: Logger = LoggerFactory.getLogger(TransactionsSubscriber::class.java)

    @PostConstruct
    fun init() {
        subscriptionsManager.createSubscriber(TransferTransactionAggregate::class, "accounts::transaction-processing-subscriber") {
            `when`(TransferTransactionCreatedEvent::class) { event ->
                val sagaContext = sagaManager
                    .withContextGiven(event.sagaContext)
                    .launchSaga("TRANSFER_TRANSACTION", "TRANSFER_TRANSACTION")
                    .performSagaStep("TRANSACTION_INIT", "transaction started")
                    .sagaContext

                logger.info("Got transaction to process: $event")

                val transactionOutcome1 = accountEsService.update(event.sourceAccountId, sagaContext) {
                    it.performTransferFrom(
                        event.sourceBankAccountId,
                        event.transferId,
                        event.transferAmount
                    )
                }

                val transactionOutcome2 = accountEsService.update(event.destinationAccountId, sagaContext) {
                    it.performTransferTo(
                        event.destinationBankAccountId,
                        event.transferId,
                        event.transferAmount
                    )
                }

                logger.info("Transaction: ${event.transferId}. Outcomes: $transactionOutcome1, $transactionOutcome2")
            }
            `when`(TransactionCompleteEvent::class) { event ->
                val sagaContext = sagaManager
                    .withContextGiven(event.sagaContext)
                    .performSagaStep("TRANSACTION_INIT", "transaction completed")
                    .sagaContext

                logger.info("Got transaction completed event: $event")

                val transactionOutcome1 = accountEsService.update(event.sourceAccountId, sagaContext) {
                    it.processPendingTransaction(event.sourceBankAccountId, event.transferId)
                }

                val transactionOutcome2 = accountEsService.update(event.destinationAccountId, sagaContext) {
                    it.processPendingTransaction(event.destinationBankAccountId, event.transferId)
                }

                logger.info("Transaction: ${event.transferId}. Outcomes: $transactionOutcome1, $transactionOutcome2")
            }
            `when`(TransactionFailedEvent::class) { event ->
                val sagaContext = sagaManager
                    .withContextGiven(event.sagaContext)
                    .performSagaStep("TRANSACTION_INIT", "transaction failed")
                    .sagaContext

                logger.info("Got transaction failed event: $event")

                val transactionOutcome1 = accountEsService.update(event.sourceAccountId, sagaContext) {
                    it.processPendingTransaction(event.sourceBankAccountId, event.transferId)
                }

                val transactionOutcome2 = accountEsService.update(event.destinationAccountId, sagaContext) {
                    it.processPendingTransaction(event.destinationBankAccountId, event.transferId)
                }

                logger.info("Transaction: ${event.transferId}. Outcomes: $transactionOutcome1, $transactionOutcome2")
            }
            `when`(TransactionWithdrawFailedEvent::class) { event ->
                val sagaContext = sagaManager
                    .withContextGiven(event.sagaContext)
                    .performSagaStep("TRANSACTION_INIT", "withdraw failed")
                    .sagaContext

                logger.info("Got transaction withdraw failed event: $event")

                val transactionOutcome1 = accountEsService.update(event.sourceAccountId, sagaContext) {
                    it.processPendingTransaction(event.sourceBankAccountId, event.transferId)
                }

                val transactionOutcome2 = accountEsService.update(event.destinationAccountId, sagaContext) {
                    it.rollbackPendingTransaction(event.destinationBankAccountId, event.transferId)
                }

                logger.info("Transaction: ${event.transferId}. Outcomes: $transactionOutcome1, $transactionOutcome2")
            }
            `when`(TransactionDepositFailedEvent::class) { event ->
                val sagaContext = sagaManager
                    .withContextGiven(event.sagaContext)
                    .performSagaStep("TRANSACTION_INIT", "deposit failed")
                    .sagaContext

                logger.info("Got transaction deposit failed event: $event")

                val transactionOutcome1 = accountEsService.update(event.sourceAccountId, sagaContext) {
                    it.rollbackPendingTransaction(event.sourceBankAccountId, event.transferId)
                }

                val transactionOutcome2 = accountEsService.update(event.destinationAccountId, sagaContext) {
                    it.processPendingTransaction(event.destinationBankAccountId, event.transferId)
                }

                logger.info("Transaction: ${event.transferId}. Outcomes: $transactionOutcome1, $transactionOutcome2")
            }
        }
    }
}