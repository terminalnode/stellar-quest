package terminalnode.xyz.quests

import org.stellar.sdk.*
import java.security.MessageDigest
import java.util.*

object StellarSet3 {
  fun quest3(sourceSecretKey: String) {
    // Add the special signing key
    val source = KeyPair.fromSecretSeed(sourceSecretKey)
    val sourceAccount = stellarServer.accounts().account(source.accountId)
    val md = MessageDigest.getInstance("SHA-256")
    val messageDigest = md.digest(Base64.getDecoder().decode("S2FuYXllTmV0"))
    val signerKey = Signer.sha256Hash(messageDigest)

    val addSigningKeyTx = Transaction.Builder(sourceAccount, Network.TESTNET)
      .addOperation(
        SetOptionsOperation.Builder()
          .setSigner(signerKey, 1)
          .build()
      )
      .addMemo(Memo.text("Add secret key"))
      .setBaseFee(100)
      .setTimeout(180)
      .build()
    addSigningKeyTx.sign(source)

    // Remove the special signing key
    val removeSigningKeyTx = Transaction.Builder(sourceAccount, Network.TESTNET)
      .addOperation(
        SetOptionsOperation.Builder()
          .setSigner(signerKey, 0)
          .build()
      ).addMemo(Memo.text("Remove secret key"))
      .setBaseFee(100)
      .setTimeout(180)
      .build()
    removeSigningKeyTx.sign(Base64.getDecoder().decode("S2FuYXllTmV0"))

    // Execute the transactions
    tryTransactions(addSigningKeyTx, removeSigningKeyTx)
  }

  fun quest2(sourceSecretKey: String) {
    println("Generating key pairs...")
    val source = KeyPair.fromSecretSeed(sourceSecretKey)
    val destination = KeyPair.fromAccountId("GCDNJUBQSX7AJWLJACMJ7I4BC3Z47BQUTMHEICZLE6MU4KQBRYG5JY6B")

    println("Verify accounts exist...")
    stellarServer.accounts().account(destination.accountId)
    val sourceAccount = stellarServer.accounts().account(source.accountId)

    println("Initializing transaction builder")
    val transactionBuilder = Transaction.Builder(sourceAccount, Network.TESTNET)
      .addMemo(Memo.text("Den ska upp i hundra!"))
      .setBaseFee(100)
      .setTimeout(180)

    println("Adding a hundred 0.01 transactions to ${destination.accountId}...")
    for (i in 1..100) {
      transactionBuilder.addOperation(
        PaymentOperation
          .Builder(destination.accountId, AssetTypeNative(), "0.01")
          .build()
      )
    }

    println("Building and signing transaction...")
    val transaction = transactionBuilder.build()
    transaction.sign(source)

    tryTransactions(transaction)
  }
}