package br.com.zup.ot4.keymanager.search

import br.com.zup.ot4.KeyType
import br.com.zup.ot4.SearchKeyResponse
import java.time.LocalDateTime
import java.time.ZoneOffset

data class PixKeyDetailsResponse(
    val pixId: String,
    val clientId: String,
    val keyType: KeyType,
    val key: String,
    val ownerName: String,
    val accountData: AccountDataResponse,
    val createdAt: String
) {

    companion object {
        fun of(grpcResponse: SearchKeyResponse): PixKeyDetailsResponse {
            return PixKeyDetailsResponse(
                pixId = grpcResponse.pixId,
                clientId = grpcResponse.externalClientId,
                keyType = grpcResponse.keyType,
                key = grpcResponse.key,
                ownerName = grpcResponse.ownerName,
                accountData = AccountDataResponse(grpcResponse.accountData),
                createdAt = LocalDateTime.ofEpochSecond(
                    grpcResponse.createdAt.seconds,
                    grpcResponse.createdAt.nanos,
                    ZoneOffset.UTC
                ).toString()
            )
        }
    }
}