package terminalnode.xyz.quests

import org.stellar.sdk.*
import terminalnode.xyz.utils.stellarServer
import terminalnode.xyz.utils.tryTransactions
import java.net.URL
import java.security.MessageDigest
import java.util.*

object StellarSet2 {
  fun quest1(publicKey: String) {
    // Quest: Create an account with 5k stellar and a sha256 MEMO_HASH of "Stellar Quest Series 2"
    // First we create a random account funded with 10k from friendbot
    val initialAccount = KeyPair.random()
    val friendBotUrl = "https://friendbot.stellar.org/?addr=${initialAccount.accountId}";
    URL(friendBotUrl).openStream()
    println("New random account funded");

    // Create an account with the required hash
    val sourceAccount = stellarServer.accounts().account(initialAccount.accountId)
    val memoHash = MessageDigest.getInstance("SHA-256")
      .digest("Stellar Quest Series 2".toByteArray())

    val transaction = Transaction.Builder(sourceAccount, Network.TESTNET)
      .addOperation(
        CreateAccountOperation.Builder(publicKey, "5000").build()
      ).addMemo(MemoHash(memoHash))
      .setBaseFee(100)
      .setTimeout(180)
      .build()
    transaction.sign(initialAccount)

    tryTransactions(transaction)
  }

  fun quest2() {
    TODO("Quest is not done yet!")
  }

  fun quest3() {
    TODO("Quest is not done yet!")
  }

  fun quest4() {
    TODO("Quest is not done yet!")
  }

  fun quest5() {
    TODO("Quest is not done yet!")
  }

  fun quest6() {
    TODO("Quest is not done yet!")
  }

  fun quest7() {
    TODO("Quest is not done yet!")
  }

  fun quest8() {
    TODO("Quest is not done yet!")
  }
}