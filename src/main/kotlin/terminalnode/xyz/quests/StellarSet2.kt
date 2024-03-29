package terminalnode.xyz.quests

import org.stellar.sdk.*
import terminalnode.xyz.utils.fundRandomAccount
import terminalnode.xyz.utils.stellarServer
import terminalnode.xyz.utils.tryTransactions
import java.security.MessageDigest

object StellarSet2 {
  private fun announceQuest(questNumber: Int, codeBlock: () -> Unit) {
    println("START: Set 2, Quest $questNumber")
    codeBlock()
    println("END: Set 2, Quest $questNumber")
  }

  fun quest1(publicKey: String) {
    // Quest: Create an account with 5k stellar and a sha256 MEMO_HASH of "Stellar Quest Series 2"
    announceQuest(1) {
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
  }

  fun quest2(secretKey: String) {
    // Quest: Create a trust line from your account to an asset and send that asset from
    // the issuer account to yours in the same transaction.
    announceQuest(2) {
      val issuingAccount = fundRandomAccount()
      val sourceAccount = KeyPair.fromSecretSeed(secretKey)
      val remoteSourceAccount = stellarServer.accounts().account(sourceAccount.accountId)
      val newAsset = Asset.createNonNativeAsset("ASS", issuingAccount.accountId)

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
  }

  fun quest3(secretKey: String) {
    // Quest: Create a fee bump transaction
    announceQuest(3) {
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
  }

  fun quest4(secretKey: String) {
    // Quest: Create a claimable balance of 100 XLM only claimable by you. It also should not be claimable before the
    // release of Set 2 Quest 5... but we're way past that now. Probably need to put some kind of time constraint though.
    announceQuest(4) {
      val sourceAccount = KeyPair.fromSecretSeed(secretKey)
      val remoteSourceAccount = stellarServer.accounts().account(sourceAccount.accountId)

      val transaction = Transaction.Builder(remoteSourceAccount, Network.TESTNET)
        .addOperation(
          CreateClaimableBalanceOperation.Builder(
            "100",
            AssetTypeNative(),
            listOf(Claimant(
              sourceAccount.accountId,
              Predicate.Not(Predicate.RelBefore(100))))
          ).build()
        ).addMemo(Memo.text("Claim this if you can!"))
        .setBaseFee(100)
        .setTimeout(180)
        .build()
        .also { it.sign(sourceAccount) }

      tryTransactions(transaction)
    }
  }

  fun quest5(secretKey: String) {
    // Quest: Claim the balance from quest 4
    announceQuest(5) {
      val sourceAccount = KeyPair.fromSecretSeed(secretKey)
      val remoteSourceAccount = stellarServer.accounts().account(sourceAccount.accountId)

      // This will throw an exception if there is no balance to be claimed.
      // And if the time hasn't passed it seems it will just throw a tx_failed error.
      val claimableBalance = stellarServer.claimableBalances()
        .forClaimant(sourceAccount.accountId)
        .execute()
        .records
        .first()

      val transaction = Transaction.Builder(remoteSourceAccount, Network.TESTNET)
        .addOperation(
          ClaimClaimableBalanceOperation
            .Builder(claimableBalance.id)
            .build()
        ).addMemo(Memo.text("My money!"))
        .setBaseFee(100)
        .setTimeout(180)
        .build()
        .also {it.sign(sourceAccount) }

      tryTransactions(transaction)
    }
  }

  fun quest6(secretKey: String) {
    // Quest: Sponsor the absolute minimum balance for a new account
    announceQuest(6) {
      val sourceAccount = KeyPair.fromSecretSeed(secretKey)
      val remoteSourceAccount = stellarServer.accounts().account(sourceAccount.accountId)
      val sponsoredAccount = KeyPair.random()

      val transaction = Transaction.Builder(remoteSourceAccount, Network.TESTNET)
        .addOperation(
          BeginSponsoringFutureReservesOperation
            .Builder(sponsoredAccount.accountId)
            .build()
        ).addOperation(
          CreateAccountOperation
            .Builder(sponsoredAccount.accountId, "0")
            .build()
        ).addOperation(EndSponsoringFutureReservesOperation(sponsoredAccount.accountId))
        .addMemo(Memo.text("Sponsored!"))
        .setBaseFee(100)
        .setTimeout(180)
        .build()
        .also {
          // Sponsoring transaction needs to be signed by both participants,
          // you can not sponsor someone who is not willing to be sponsored. :-)
          it.sign(sourceAccount)
          it.sign(sponsoredAccount)
        }

      tryTransactions(transaction)
    }
  }

  fun quest7(secretKey: String) {
    // Quest: Revoke sponsorship for the account sponsored in quest 6
    // Our approach will be to find and cancel all sponsorships
    announceQuest(7) {
      val sourceAccount = KeyPair.fromSecretSeed(secretKey)
      val remoteSourceAccount = stellarServer.accounts().account(sourceAccount.accountId)
      val transactionBuilder = Transaction.Builder(remoteSourceAccount, Network.TESTNET)
        .addMemo(Memo.text("Revoke all sponsorships!"))
        .setBaseFee(100)
        .setTimeout(180)

      val sponsoredAccounts = stellarServer.accounts().forSponsor(sourceAccount.accountId).execute().records
      if (sponsoredAccounts.isEmpty()) throw Exception("The provided account isn't sponsoring anything!")

      sponsoredAccounts.forEach {
        transactionBuilder.addOperation(
          // We need to provide the account with some XLM so they can handle the minimum balance themselves.
          // Otherwise the transaction will fail.
          PaymentOperation
            .Builder(it.accountId, AssetTypeNative(), "1")
            .build()
        ).addOperation(
          RevokeAccountSponsorshipOperation
            .Builder(it.accountId)
            .build()
        )
      }

      transactionBuilder.build()
        .also {
          it.sign(sourceAccount)
          tryTransactions(it)
        }
    }
  }

  fun quest8(secretKey: String, domain: String) {
    // Quest: Create a stellar.toml hosts file for your account
    announceQuest(8) {
      println("NOTE: This is just a partial solution, you also need to add your .well-known/stellar.toml file to the domain!")
      val sourceAccount = KeyPair.fromSecretSeed(secretKey)
      val remoteSourceAccount = stellarServer.accounts().account(sourceAccount.accountId)

      val transaction = Transaction.Builder(remoteSourceAccount, Network.TESTNET)
        .addOperation(
          SetOptionsOperation
            .Builder()
            .setHomeDomain(domain)
            .build()
        ).addMemo(Memo.text("This is my home"))
        .setBaseFee(100)
        .setTimeout(180)
        .build()
        .also { it.sign(sourceAccount) }

      tryTransactions(transaction)
    }
  }
}