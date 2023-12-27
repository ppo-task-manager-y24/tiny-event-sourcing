package ru.quipy.bankDemo.transfers.subscribers

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import ru.quipy.bankDemo.accounts.api.*
import ru.quipy.bankDemo.transfers.api.TransferTransactionAggregate
import ru.quipy.bankDemo.transfers.logic.TransferTransaction
import ru.quipy.core.EventSourcingService
import ru.quipy.saga.SagaManager
import ru.quipy.streams.AggregateSubscriptionsManager
import java.util.*
import javax.annotation.PostConstruct

@Component
class BankAccountsSubscriber(
    private val subscriptionsManager: AggregateSubscriptionsManager,
    private val transactionEsService: EventSourcingService<UUID, TransferTransactionAggregate, TransferTransaction>,
    private val sagaManager: SagaManager
) {
    private val logger: Logger = LoggerFactory.getLogger(BankAccountsSubscriber::class.java)

    @PostConstruct
    fun init() {
        subscriptionsManager.createSubscriber(AccountAggregate::class, "transactions::bank-accounts-subscriber") {
            `when`(TransferWithdrawCompletedEvent::class) { event ->
                val sagaContext = sagaManager
                    .withContextGiven(event.sagaContext)
                    .performSagaStep("TRANSFER_TRANSACTION", "withdraw completed")
                    .sagaContext

                transactionEsService.update(
                    aggregateId = event.transactionId,
                    sagaContext = sagaContext
                ) { it.transactionWithdrawComplete(event.bankAccountId) }
            }
            `when`(TransferWithdrawFailedEvent::class) { event ->
                val sagaContext = sagaManager
                    .withContextGiven(event.sagaContext)
                    .performSagaStep("TRANSFER_TRANSACTION", "withdraw failed")
                    .sagaContext

                transactionEsService.update(
                    aggregateId = event.transactionId,
                    sagaContext = sagaContext
                ) { it.transactionWithdrawFailed(event.bankAccountId) }
            }
            `when`(TransferDepositCompletedEvent::class) { event ->
                val sagaContext = sagaManager
                    .withContextGiven(event.sagaContext)
                    .performSagaStep("TRANSFER_TRANSACTION", "deposit completed")
                    .sagaContext

                transactionEsService.update(
                    aggregateId = event.transactionId,
                    sagaContext = sagaContext
                ) { it.transactionDepositComplete(event.bankAccountId) }
            }
            `when`(TransferDepositFailedEvent::class) { event ->
                val sagaContext = sagaManager
                    .withContextGiven(event.sagaContext)
                    .performSagaStep("TRANSFER_TRANSACTION", "deposit failed")
                    .sagaContext

                transactionEsService.update(
                    aggregateId = event.transactionId,
                    sagaContext = sagaContext
                ) { it.transactionDepositFailed(event.bankAccountId) }
            }
        }
    }
}