package terminalnode.xyz.quests

import org.stellar.sdk.*
import terminalnode.xyz.utils.*
import java.net.URL
import java.security.MessageDigest
import java.util.*

object StellarSet3 {
	private fun announceQuest(questNumber: Int, codeBlock: () -> Unit) {
		println("START: Set 3, Quest $questNumber")
		codeBlock()
		println("END: Set 3, Quest $questNumber")
	}

	fun quest1(sourceSecretKey: String) {
		announceQuest(1) {
			// Quest: Create a new account and bump it's sequence to 110101115104111
			// See: https://horizon.stellar.org/transactions/073cde1fab7d28d3e322e5f61ac385c37859c3e82b17b6c92a9f0444420336cb/operations
			//
			// Even though the transaction implies that we should create an account and bump its sequence, that didn't work
			// for me and relevant code has therefore been commented out below.
			val source = KeyPair.fromSecretSeed(sourceSecretKey)
			val sourceAccount = stellarServer.accounts().account(source.accountId)
			// val newAccount = KeyPair.random()

			val transaction = Transaction.Builder(sourceAccount, Network.TESTNET)
				.addMemo(Memo.text("Bompetee-bomp!"))
				.setBaseFee(100)
				.setTimeout(180)
				// Not sure this is needed, probably not. First tried bumping the newly created account,
				// but the verification didn't pass until I changed the bump sequence operation to my own account.
				// .addOperation(
				//   CreateAccountOperation
				//     .Builder(newAccount.accountId, "10")
				//     .setSourceAccount(sourceAccount.accountId)
				//     .build()
				// )
				.addOperation(
					BumpSequenceOperation
						.Builder(110101115104111)
						.build()
				).build()
				.also {
					it.sign(source)
					//it.sign(newAccount)
				}

			tryTransactions(transaction)
		}
	}

	fun quest2(sourceSecretKey: String) {
		announceQuest(2) {
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

	fun quest3(sourceSecretKey: String) {
		announceQuest(3) {
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
	}

	fun quest4(sourceSecretKey: String) {
		announceQuest(4) {
			// Quest: Create a pre-authorized transaction
			val source = KeyPair.fromSecretSeed(sourceSecretKey)
			val sourceAccount = stellarServer.accounts().account(source.accountId)

			// There's probably some better way to do this, but this is the way I came up with to make sure
			// that the preAuth transaction has a later sequence number than the authorizing transaction.
			val sourceAccountBumped = stellarServer.accounts().account(source.accountId)
			sourceAccountBumped.incrementSequenceNumber()

			val preAuthorizedTransaction = Transaction.Builder(sourceAccountBumped, Network.TESTNET)
				.addOperation(
					SetOptionsOperation.Builder()
						.setHomeDomain("test.xyz")
						.build()
				).addMemo(Memo.text("Pre-Quest 4"))
				.setBaseFee(100)
				.setTimeout(180)
				.build()

			val authorizingTransaction = Transaction.Builder(sourceAccount, Network.TESTNET)
				.addOperation(
					SetOptionsOperation.Builder()
						.setSigner(Signer.preAuthTx(preAuthorizedTransaction), 1)
						.build()
				).addMemo(Memo.text("Quest 4"))
				.setBaseFee(100)
				.setTimeout(180)
				.build()
				.also { it.sign(source) }

			tryTransactions(authorizingTransaction, preAuthorizedTransaction)
		}
	}

	fun quest5(sourceSecretKey: String) {
		announceQuest(5) {
			// Quest: Issue a clawback transaction.
			// 1. Set the clawback flag (which requires auth revocable) on your account.
			// 2. Create a trust line from some poor sod's account to yours.
			val poorSod = fundRandomAccount()
			val myAccount = KeyPair.fromSecretSeed(sourceSecretKey)
			val sourceAccount = stellarServer.accounts().account(myAccount.accountId)
			val newAsset = Asset.createNonNativeAsset("MariusLenk", myAccount.accountId)

			val enableClawback = Transaction.Builder(sourceAccount, Network.TESTNET)
				.addOperation(
					SetOptionsOperation
						.Builder()
						.setSetFlags(0x2) // auth revocable
						.build())
				.addOperation(
					SetOptionsOperation
						.Builder()
						.setSetFlags(0x8) // clawback flag
						.build())
				.setTimeout(180).setBaseFee(100).addMemo(Memo.text("MariusLenk bucks"))
				.build()
				.also { it.sign(myAccount) }

			val acceptAsset = Transaction.Builder(sourceAccount, Network.TESTNET)
				.addOperation(
					ChangeTrustOperation
						.Builder(newAsset, "10000000")
						.setSourceAccount(poorSod.accountId)
						.build())
				.addOperation(
					PaymentOperation
						.Builder(poorSod.accountId, newAsset, "10")
						.build())
				.setTimeout(180).setBaseFee(100).addMemo(Memo.text("MariusLenk bucks"))
				.build()
				.also {
					it.sign(myAccount)
					it.sign(poorSod)
				}

			val clawbackAsset = Transaction.Builder(sourceAccount, Network.TESTNET)
				.addOperation(ClawbackOperation.Builder(poorSod.accountId, newAsset, "10").build())
				.setTimeout(180).setBaseFee(100).addMemo(Memo.text("MariusLenk bucks"))
				.build()
				.also {
					it.sign(myAccount)
				}

			tryTransactions(enableClawback, acceptAsset, clawbackAsset)
		}
	}

	fun quest6(sourceSecretKey: String) {
		announceQuest(6) {
			// Quest: Create a Stellar Quest-style NFT.
			// Add the base 64 encoded version of the provided image as a series of key-value pairs.
			// The keys and values both contain data, but the keys are also prefixed with two digits that identify
			// the order in which they should be applied.
			val source = KeyPair.fromSecretSeed(sourceSecretKey)
			val sourceAccount = stellarServer.accounts().account(source.accountId)
			clearDataFromAccount(sourceSecretKey)

			// Create list of key value pairs.
			// Data can be up to 64 bytes, minus two bytes for the indexing on the key
			val addDataTransactionBuilder = Transaction.Builder(sourceAccount, Network.TESTNET)
				.setTimeout(180).setBaseFee(100).addMemo(Memo.text("Adding NFT!"))

			// There's a bug where cloudflare compresses the image we get from api.stellar.quest, rendering the b64 string
			// unusable. Solution is to use this original version of the image posted on discord instead.
			//val ops = URL("https://api.stellar.quest/badge/GCEE5H3RI2MFP4UQ4NHFKLGTIHILWA775AM7KTLU5HUBSLOBJN7M4RSL?network=public&v=1")
			val ops = URL("https://cdn.discordapp.com/attachments/765215066420805663/845133032443740160/GCEE5H3RI2MFP4UQ4NHFKLGTIHILWA775AM7KTLU5HUBSLOBJN7M4RSL.png")
				.openStream()
				.readAllBytes()
				.let { Base64.getEncoder().encode(it).toList() }
				.chunked(62 + 64)
				.mapIndexed { index, keyAndValue ->
					val key = keyAndValue.take(62).toByteArray().toString(Charsets.UTF_8)
					val stringKey = "${index.toString().padStart(2, '0')}${key}"
					val value = keyAndValue.drop(62).toByteArray()

					return@mapIndexed ManageDataOperation
						.Builder(stringKey, value)
						.build()
				}
			ops.forEach { addDataTransactionBuilder.addOperation(it) }

			// Build and sign tx
			val addDataTransaction = addDataTransactionBuilder.build().also { it.sign(source) }
			tryTransactions(addDataTransaction)
		}
	}

	fun quest6Dirty(sourceSecretKey: String) {
		announceQuest(6) {
			println("CHEATER EDITION, that doesn't actually work atm!")
			// In this version of the quest, we copy the data from the source account instead of encoding it ourselves.
			val source = KeyPair.fromSecretSeed(sourceSecretKey)
			val sourceAccount = stellarServer.accounts().account(source.accountId)
			clearDataFromAccount(sourceSecretKey)

			val operations = stellarMainNetServer
				.accounts()
				.account("GCEE5H3RI2MFP4UQ4NHFKLGTIHILWA775AM7KTLU5HUBSLOBJN7M4RSL")
				.data
				.map {
					if (it.key.substring(0, 2) == "00") {
						println(it.key)
						println(it.value)
					}
					ManageDataOperation
						.Builder(it.key, it.value.toByteArray())
						.build()
				}
			println("Operations: ${operations.size}")

			val transaction = Transaction.Builder(sourceAccount, Network.TESTNET)
				.setTimeout(180).setBaseFee(100).addMemo(Memo.text("Adding NFT, cheat!"))
				.apply { operations.forEach { addOperation(it) } }
				.build()
				.also { it.sign(source) }
			println("Operations: ${transaction.operations.size}")

			tryTransactions(transaction)
		}
	}

	fun quest7() {
		announceQuest(7) {
			TODO("Quest is not done yet!")
		}
	}

	fun quest8() {
		announceQuest(8) {
			TODO("Quest is not done yet!")
		}
	}
}