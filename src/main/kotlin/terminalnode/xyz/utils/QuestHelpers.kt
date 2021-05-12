package terminalnode.xyz.utils

import org.stellar.sdk.Server
import org.stellar.sdk.Transaction

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
      if (!response.isSuccess) throw Exception("Transaction $index/$totalTxs failed! ($memo)")
    } catch (e: Exception) {
      println("Transaction $index ($memo) failed with exception: ${e.message}.\nStacktrace:")
      e.printStackTrace()
    }
  }
}