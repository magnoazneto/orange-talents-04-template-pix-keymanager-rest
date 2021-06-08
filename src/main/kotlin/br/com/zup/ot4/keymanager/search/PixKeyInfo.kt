package br.com.zup.ot4.keymanager.search

import br.com.zup.ot4.AccountType
import br.com.zup.ot4.KeyType
import br.com.zup.ot4.SearchAllResponse
import java.time.LocalDateTime
import java.time.ZoneOffset

class PixKeyInfo(
    val pixId: String,
    val clientId: String,
    val keyType: KeyType,
    val key: String,
    val accountType: AccountType,
    val createdAt: String
) {
    constructor(pixInfo: SearchAllResponse.PixDetails): this(
        pixInfo.pixId,
        pixInfo.externalClientId,
        pixInfo.keyType,
        pixInfo.key,
        pixInfo.accountType,
        LocalDateTime.ofEpochSecond(
            pixInfo.createdAt.seconds,
            pixInfo.createdAt.nanos,
            ZoneOffset.UTC
        ).toString()
    )
}
