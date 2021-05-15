package terminalnode.xyz.utils

import org.stellar.sdk.KeyPair
import org.stellar.sdk.Server
import org.stellar.sdk.Transaction
import java.net.URL

const val stellarServerUrl = "https://horizon-testnet.stellar.org"
val stellarServer = Server(stellarServerUrl)

fun tryTransactions(vararg transactions: Transaction) {
  val totalTxs = transactions.size
  println("Sending $totalTxs ${if (totalTxs == 1) "transaction" else "transactions"}...")

  transactions.forEachIndexed { zeroIndex, transaction ->
    val memo = transaction.memo.toString()
    val index = zeroIndex + 1

    try {
      val response = stellarServer.submitTransaction(transaction)
      val resultText = if (response.isSuccess) "Success" else "Failure"

      println("Transaction $index/$totalTxs ($memo) completed! $resultText")
      if (!response.isSuccess) {
        println(response.extras.resultCodes.operationsResultCodes)
        throw Exception(response.extras.resultCodes.transactionResultCode)
      } else {
        println("https://horizon.stellar.org/transactions/${response.hash}")
      }
    } catch (e: Exception) {
      println("Transaction $index ($memo) failed with exception: ${e.message}.\nStacktrace:")
      e.printStackTrace()
    }
  }
}

fun fundRandomAccount(): KeyPair = KeyPair.random().also {
  println("Generating and funding an account")
  val friendBotUrl = "https://friendbot.stellar.org/?addr=${it.accountId}"
  URL(friendBotUrl).openStream()
  println("New random account funded")
}