package br.com.zup.ot4.keymanager.registry

import br.com.caelum.stella.validation.CPFValidator
import br.com.zup.ot4.AccountType
import br.com.zup.ot4.KeyType
import br.com.zup.ot4.PixKeyRequest
import br.com.zup.ot4.shared.annotations.ValidPixKey
import io.micronaut.core.annotation.Introspected
import io.micronaut.validation.validator.constraints.EmailValidator
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Introspected
@ValidPixKey
data class KeyPostRequest(
    val keyType: KeyTypeRequest,
    @field:Size(max = 77) val key: String?,
    val accountType: AccountType
) {
    fun toGrpcRequest(clientId: UUID): PixKeyRequest {
        return PixKeyRequest.newBuilder()
            .setAccountType(accountType)
            .setKeyType(keyType.grpcType)
            .setPixKey(key ?: "")
            .setExternalClientId(clientId.toString())
            .build()
    }
}

enum class KeyTypeRequest(val grpcType: KeyType){
    CPF(KeyType.CPF) {
        override fun validate(key: String?): Boolean {
            if(key.isNullOrBlank()) return false

            return CPFValidator(false)
                .invalidMessagesFor(key)
                .isEmpty()
        }
    },

    EMAIL(KeyType.EMAIL) {
        override fun validate(key: String?): Boolean {
            if(key.isNullOrBlank()) return false

            return key.matches("^[a-z0-9.]+@[a-z0-9]+\\.[a-z]+\\.?([a-z]+)?\$".toRegex())
        }
    },

    PHONE_NUMBER(KeyType.PHONE_NUMBER) {
        override fun validate(key: String?): Boolean {
            if(key.isNullOrBlank()) return false

            return key.matches("^\\+[1-9][0-9]\\d{1,14}\$".toRegex())
        }
    },

    RANDOM_KEY(KeyType.RANDOM_KEY) {
        override fun validate(key: String?) = key.isNullOrBlank()
    };

    abstract fun validate(key: String?): Boolean
}
