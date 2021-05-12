package terminalnode.xyz.quests

import org.stellar.sdk.*
import org.stellar.sdk.xdr.AssetCode
import org.stellar.sdk.xdr.AssetType
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
        ChangeTrustOperation
          .Builder(newAsset, "100000000")
          .build()
      ).addOperation(
        PaymentOperation
          .Builder(sourceAccount.accountId, newAsset, "1000")
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

    tryTransactions(transaction)
  }

  fun quest3(secretKey: String) {
    val sugarDaddyAccount = fundRandomAccount()
    val sourceAccount = KeyPair.fromSecretSeed(secretKey)
    val remoteSourceAccount = stellarServer.accounts().account(sourceAccount.accountId)

    val transaction = Transaction.Builder(remoteSourceAccount, Network.TESTNET)
      .addOperation(
        PaymentOperation
          .Builder(sugarDaddyAccount.accountId, AssetTypeNative(), "1")
          .build()
      )
      .setBaseFee(100)
      .setTimeout(180)
      .build()
      .also {
        it.sign(sourceAccount)
      }

    val feeBumpTransaction = FeeBumpTransaction.Builder(transaction)
      .setBaseFee(180)
      .setFeeAccount(sugarDaddyAccount.accountId)
      .build()
      .also {
        it.sign(sugarDaddyAccount)
      }

    // The Stellar Server object accepts Transactions or FeeBumpTransactions, not AbstractTransaction,
    // so we can't make the handy-dandy tryTransactions method work for both unfortunately.
    try {
      val response = stellarServer.submitTransaction(feeBumpTransaction)
      if (!response.isSuccess) {
        println(response.extras.resultCodes.transactionResultCode)
        throw Exception()
      }
    } catch (e: Exception) {
      println("TRANSACTION FAILED! OH NO!")
    }
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