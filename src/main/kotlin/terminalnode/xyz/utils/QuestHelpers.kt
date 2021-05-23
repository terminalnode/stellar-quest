package terminalnode.xyz.utils

import org.stellar.sdk.*
import java.net.URL

const val stellarTestNetServerUrl = "https://horizon-testnet.stellar.org"
const val stellarMainNetUrl = "https://horizon.stellar.org"
val stellarServer = Server(stellarTestNetServerUrl)
val stellarMainNetServer = Server(stellarMainNetUrl)

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
        println("Rate limit remaining: ${response.rateLimitRemaining}")
        println("Rate limit limit: ${response.rateLimitLimit}")
        println("Rate limit reset: ${response.rateLimitReset}")
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

fun clearDataFromAccount(secretKey: String) {
  val keyPair = KeyPair.fromSecretSeed(secretKey)
  val account = stellarServer.accounts().account(keyPair.accountId)

  if (account.data.keys.isNotEmpty()) {
    println("Found old data, removing that first!")
    Transaction.Builder(account, Network.TESTNET)
      .setTimeout(180).setBaseFee(100).addMemo(Memo.text("Adding NFT!"))
      .apply {
        account.data.keys.forEach {
          addOperation(ManageDataOperation.Builder(it, null).build())
        }
      }.build()
      .also {
        it.sign(keyPair)
        tryTransactions(it)
      }
  } else {
    println("No existing data in account, not clearing anything")
  }
}