package br.com.zup.ot4.keymanager.search

import br.com.zup.ot4.AccountData
import br.com.zup.ot4.AccountType

data class AccountDataResponse(
    val organitazionName: String,
    val branch: String,
    val accountNumber: String,
    val accountType: AccountType
) {
    constructor(protobufData: AccountData) : this(
        organitazionName = protobufData.organizationName,
        branch = protobufData.branch,
        accountNumber = protobufData.accountNumber,
        accountType = protobufData.accountType
    )

}
