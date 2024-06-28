package com.rikikunproject.duinocoins.model

import com.google.gson.annotations.SerializedName

data class SuccessResponse(
    val success: Boolean,
    val server: String,
    val result: Result,
    val message: String
)

data class Result(
    @SerializedName("achievements")
    val achievements: List<Int>,
    @SerializedName("balance")
    val balance: Balance,
    @SerializedName("items")
    val items: List<Any>, // Gantilah dengan tipe data yang sesuai jika ada informasi items
    @SerializedName("miners")
    val miners: List<Miner>,
    @SerializedName("prices")
    val prices: Prices,
    @SerializedName("transactions")
    val transactions: List<Transaction>
)

data class Balance(
    @SerializedName("balance")
    val balance: Double,
    @SerializedName("created")
    val created: String,
    @SerializedName("last_login")
    val lastLogin: Long,
    @SerializedName("stake_amount")
    val stakeAmount: Double,
    @SerializedName("stake_date")
    val stakeDate: Long,
    @SerializedName("trust_score")
    val trustScore: Int,
    @SerializedName("username")
    val username: String,
    @SerializedName("verified")
    val verified: String,
    @SerializedName("verified_by")
    val verifiedBy: String,
    @SerializedName("verified_date")
    val verifiedDate: Long,
    @SerializedName("warnings")
    val warnings: Int
)

data class Miner(
    @SerializedName("accepted")
    val accepted: Int,
    @SerializedName("algorithm")
    val algorithm: String,
    @SerializedName("diff")
    val diff: Int,
    @SerializedName("hashrate")
    val hashrate: Int,
    @SerializedName("identifier")
    val identifier: String,
    @SerializedName("it")
    val it: Any?,
    @SerializedName("ki")
    val ki: Int,
    @SerializedName("pg")
    val pg: Int,
    @SerializedName("pool")
    val pool: String,
    @SerializedName("rejected")
    val rejected: Int,
    @SerializedName("sharerate")
    val sharerate: Int,
    @SerializedName("sharetime")
    val sharetime: Double,
    @SerializedName("software")
    val software: String,
    @SerializedName("threadid")
    val threadid: String,
    @SerializedName("username")
    val minerUsername: String,
    @SerializedName("wd")
    val wd: String
)

data class Prices(
    @SerializedName("bch")
    val bch: String,
    @SerializedName("fluffy")
    val fluffy: String,
    @SerializedName("furim")
    val furim: String,
    @SerializedName("max")
    val max: String,
    @SerializedName("nano")
    val nano: String,
    @SerializedName("nodes")
    val nodes: String,
    @SerializedName("pancake")
    val pancake: String,
    @SerializedName("sunswap")
    val sunswap: String,
    @SerializedName("sushi")
    val sushi: String,
    @SerializedName("trx")
    val trx: String,
    @SerializedName("ubeswap")
    val ubeswap: String,
    @SerializedName("xmg")
    val xmg: String
)

data class Transaction(
    @SerializedName("amount")
    val amount: String,
    @SerializedName("datetime")
    val datetime: String,
    @SerializedName("hash")
    val hash: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("memo")
    val memo: String,
    @SerializedName("recipient")
    val recipient: String,
    @SerializedName("sender")
    val sender: String
)

data class TransactionResponses(
    val success: Boolean,
    val server: String,
    val result: Transaction,
    val message: String
)
