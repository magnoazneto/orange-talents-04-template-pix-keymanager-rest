package br.com.zup.ot4.keymanager

import br.com.zup.ot4.*
import br.com.zup.ot4.keymanager.registry.KeyPostRequest
import br.com.zup.ot4.keymanager.registry.KeyTypeRequest
import br.com.zup.ot4.keymanager.remove.KeyDeleteRequest
import br.com.zup.ot4.shared.grpc.GrpcClientFactory
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.any
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest
internal class KeyManagerControllerTest(

){

    @field:Inject
    lateinit var keyManagerStub: KeyManagerServiceGrpc.KeyManagerServiceBlockingStub

    @field:Inject
    @field:Client("/")
    lateinit var client: HttpClient

    @Test
    fun `deve registrar uma nova chave pix`() {
        val clientId = UUID.randomUUID().toString()
        val pixId = UUID.randomUUID().toString()

        val responseGrpc = PixKeyResponse.newBuilder()
            .setPixId(pixId)
            .build()

        `when`(keyManagerStub.register(any(PixKeyRequest::class.java))).thenReturn(responseGrpc)

        val newPixKey = KeyPostRequest(accountType = AccountType.CONTA_CORRENTE,
                                        key = "teste@teste.com.br",
                                        keyType = KeyTypeRequest.EMAIL)

        val request = HttpRequest.POST("/api/v1/clientes/$clientId/pix", newPixKey)
        val response = client.toBlocking().exchange(request, KeyPostRequest::class.java)

        assertEquals(HttpStatus.CREATED, response.status)
    }

    @Test
    internal fun `deve remover uma chave pix existente`() {
        val clientId = UUID.randomUUID().toString()
        val pixId = UUID.randomUUID().toString()

        val responseGrpc = RemoveKeyResponse.newBuilder()
            .setSuccess(true)
            .build()

        `when`(keyManagerStub.remove(any(RemoveKeyRequest::class.java))).thenReturn(responseGrpc)

        val request = HttpRequest.DELETE<Any>("/api/v1/clientes/$clientId/pix", KeyDeleteRequest(pixId))
        val response = client.toBlocking().exchange(request, Any::class.java)

        assertEquals(HttpStatus.OK, response.status)
    }

    @Factory
    @Replaces(factory = GrpcClientFactory::class)
    internal class MockitoStubFactory {
        @Singleton
        fun stubMock() = Mockito.mock(KeyManagerServiceGrpc.KeyManagerServiceBlockingStub::class.java)
    }
}