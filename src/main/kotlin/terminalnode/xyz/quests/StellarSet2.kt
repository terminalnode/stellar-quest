package terminalnode.xyz.quests

import org.stellar.sdk.*
import org.stellar.sdk.xdr.AssetCode
import terminalnode.xyz.utils.fundRandomAccount
import terminalnode.xyz.utils.stellarServer
import terminalnode.xyz.utils.tryTransactions
import java.security.MessageDigest

object StellarSet2 {
  fun quest1(publicKey: String) {
    // Quest: Create an account with 5k stellar and a sha256 MEMO_HASH of "Stellar Quest Series 2"
    val initialAccount = fundRandomAccount()
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

  fun quest2(secretKey: String) {
    val issuingAccount = fundRandomAccount()
    val sourceAccount = KeyPair.fromSecretSeed(secretKey)
    val remoteSourceAccount = stellarServer.accounts().account(sourceAccount.accountId)
    val newAsset = Asset.createNonNativeAsset("ASS", issuingAccount.accountId);

    val transaction = Transaction.Builder(remoteSourceAccount, Network.TESTNET)
      .addOperation(
        ChangeTrustOperation.Builder(newAsset, "100000000").build()
      ).addOperation(
        PaymentOperation.Builder(sourceAccount.accountId, newAsset, "1000")
          .setSourceAccount(issuingAccount.accountId)
          .build()
      )
      .setBaseFee(100)
      .setTimeout(180)
      .build()
      .also {
        it.sign(issuingAccount)
        it.sign(sourceAccount)
      }

    // Sign with both keys to create trust line from source and issue asset from issuing

    tryTransactions(transaction)
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