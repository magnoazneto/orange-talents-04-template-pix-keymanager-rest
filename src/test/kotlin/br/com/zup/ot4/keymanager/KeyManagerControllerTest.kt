package br.com.zup.ot4.keymanager

import br.com.zup.ot4.*
import br.com.zup.ot4.keymanager.registry.KeyPostRequest
import br.com.zup.ot4.keymanager.registry.KeyTypeRequest
import br.com.zup.ot4.keymanager.remove.KeyDeleteRequest
import br.com.zup.ot4.keymanager.search.PixKeyDetailsResponse
import br.com.zup.ot4.keymanager.search.PixKeyInfo
import br.com.zup.ot4.shared.grpc.GrpcClientFactory
import com.google.protobuf.Timestamp
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.any
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest
internal class KeyManagerControllerTest(

){

    private lateinit var clientId: String
    private lateinit var pixId: String

    @field:Inject
    lateinit var keyManagerStub: KeyManagerServiceGrpc.KeyManagerServiceBlockingStub

    @field:Inject
    @field:Client("/")
    lateinit var client: HttpClient

    @BeforeEach
    internal fun setUp() {
        clientId = UUID.randomUUID().toString()
        pixId = UUID.randomUUID().toString()
    }

    @Test
    fun `deve registrar uma nova chave pix com chave valida`() {

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
    fun `deve registrar uma nova chave pix random`() {

        val responseGrpc = PixKeyResponse.newBuilder()
            .setPixId(pixId)
            .build()

        `when`(keyManagerStub.register(any(PixKeyRequest::class.java))).thenReturn(responseGrpc)

        val newPixKey = KeyPostRequest(accountType = AccountType.CONTA_CORRENTE,
            key = null,
            keyType = KeyTypeRequest.RANDOM_KEY)

        val request = HttpRequest.POST("/api/v1/clientes/$clientId/pix", newPixKey)
        val response = client.toBlocking().exchange(request, KeyPostRequest::class.java)

        assertEquals(HttpStatus.CREATED, response.status)
    }

    @Test
    fun `deve remover uma chave pix existente`() {

        val responseGrpc = RemoveKeyResponse.newBuilder()
            .setSuccess(true)
            .build()

        `when`(keyManagerStub.remove(any(RemoveKeyRequest::class.java))).thenReturn(responseGrpc)

        val request = HttpRequest.DELETE<Any>("/api/v1/clientes/$clientId/pix", KeyDeleteRequest(pixId))
        val response = client.toBlocking().exchange(request, Any::class.java)

        assertEquals(HttpStatus.OK, response.status)
    }

    @Test
    fun `deve retornar chave pix por PixID e ClientId`() {
        `when`(keyManagerStub.search(any(SearchKeyRequest::class.java))).thenReturn(responseKeyGrpc())

        val request = HttpRequest.GET<Any>("/api/v1/clientes/${clientId}/pix?pixId=$pixId")
        val response = client.toBlocking().exchange(request, PixKeyDetailsResponse::class.java)

        assertEquals(HttpStatus.OK, response.status)
        assertEquals(pixId, response.body()!!.pixId)
        assertEquals("teste@gmail.com", response.body()!!.key)
    }

    @Test
    fun `deve retornar todas as chaves pix por clientId`() {

        val responseGrpc = SearchAllResponse.newBuilder()
            .addKeys(pixDetails())
            .addKeys(pixDetails())
            .addKeys(pixDetails())
            .build()

        `when`(keyManagerStub.searchAll(any(SearchAllRequest::class.java))).thenReturn(responseGrpc)

        val request = HttpRequest.GET<Any>("/api/v1/clientes/${clientId}/pix")
        val response = client.toBlocking().exchange(request, List::class.java)

        assertEquals(HttpStatus.OK, response.status)
        assertEquals(3, response.body().size)
    }

    private fun pixDetails(): SearchAllResponse.PixDetails {
        val createdAtInstant = LocalDateTime.now().toInstant(ZoneOffset.UTC)
        return SearchAllResponse.PixDetails.newBuilder()
            .setPixId(UUID.randomUUID().toString())
            .setExternalClientId(clientId)
            .setKeyType(KeyType.RANDOM_KEY)
            .setKey(UUID.randomUUID().toString())
            .setAccountType(AccountType.CONTA_CORRENTE)
            .setCreatedAt(
                Timestamp.newBuilder()
                    .setSeconds(createdAtInstant.epochSecond)
                    .setNanos(createdAtInstant.nano)
                    .build()
            )
            .build()
    }

    private fun responseKeyGrpc(): SearchKeyResponse? {
        return SearchKeyResponse.newBuilder()
            .setExternalClientId(clientId)
            .setPixId(pixId)
            .setKeyType(KeyType.EMAIL)
            .setKey("teste@gmail.com")
            .setOwnerName("magno")
            .setOwnerCpf("00000000000")
            .setAccountData(AccountData.newBuilder()
                .setOrganizationName("ITAU UNIBANCO")
                .setBranch("1234")
                .setAccountNumber("5432")
                .setAccountType(AccountType.CONTA_CORRENTE).build())
            .build()
    }

    @Factory
    @Replaces(factory = GrpcClientFactory::class)
    internal class MockitoStubFactory {
        @Singleton
        fun stubMock() = Mockito.mock(KeyManagerServiceGrpc.KeyManagerServiceBlockingStub::class.java)
    }
}