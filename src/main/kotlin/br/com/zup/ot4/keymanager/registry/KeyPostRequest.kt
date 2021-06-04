package br.com.zup.ot4.keymanager.registry

import br.com.zup.ot4.AccountType
import br.com.zup.ot4.KeyType
import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Introspected
data class KeyPostRequest(
    @field:NotBlank val clientId: String,
    @field:NotNull val keyType: KeyType,
    @field:NotBlank @field:Size(max = 77) val key: String,
    @field:NotNull val accountType: AccountType
) {

}
