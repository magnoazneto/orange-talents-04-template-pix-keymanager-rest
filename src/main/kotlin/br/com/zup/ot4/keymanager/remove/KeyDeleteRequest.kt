package br.com.zup.ot4.keymanager.remove

import br.com.zup.ot4.RemoveKeyRequest
import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank

@Introspected
data class KeyDeleteRequest(
    @field:NotBlank val pixId: String
) {
    fun toGrpcRequest(externalClientId: String): RemoveKeyRequest? {
        return RemoveKeyRequest.newBuilder()
            .setExternalClientId(externalClientId)
            .setPixId(pixId)
            .build()
    }
}
