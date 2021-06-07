package br.com.zup.ot4.keymanager

import br.com.zup.ot4.KeyManagerServiceGrpc
import br.com.zup.ot4.SearchKeyRequest
import br.com.zup.ot4.keymanager.registry.KeyPostRequest
import br.com.zup.ot4.keymanager.remove.KeyDeleteRequest
import br.com.zup.ot4.keymanager.search.PixKeyDetailsResponse
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.*
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import java.util.*
import javax.inject.Inject
import javax.validation.Valid

@Controller("/api/v1/clientes")
@Validated
class KeyManagerController(
    @Inject val grpcClient: KeyManagerServiceGrpc.KeyManagerServiceBlockingStub
) {

    private val LOGGER = LoggerFactory.getLogger(this::class.java)

    @Get("/{clientId}/pix")
    fun search(@QueryValue pixId: String, @PathVariable clientId: String) : HttpResponse<Any> {
        val grpcResponse = grpcClient.search(
            SearchKeyRequest.newBuilder()
                .setPixData(
                    SearchKeyRequest.PixData.newBuilder()
                        .setExternalClientId(clientId)
                        .setPixId(pixId)
                        .build()
                )
                .build()
        )

        return HttpResponse.ok(PixKeyDetailsResponse.of(grpcResponse))
    }


    @Post("/{clientId}/pix")
    fun register(clientId: UUID, @Valid @Body request: KeyPostRequest): HttpResponse<Any>{
        val grpcRequest = request.toGrpcRequest(clientId)

        val grpcResponse = grpcClient.register(grpcRequest)
        LOGGER.info("[$clientId] -> nova chave pix criada com $request")
        return HttpResponse.created(location(clientId, grpcResponse.pixId))
    }

    @Delete("/{clientId}/pix")
    fun remove(clientId: UUID, @Body request: KeyDeleteRequest): HttpResponse<Any> {
        val grpcRequest = request.toGrpcRequest(clientId.toString())

        grpcClient.remove(grpcRequest)
        LOGGER.info("removendo chave pix: ${request.pixId}")
        return HttpResponse.ok()
    }

    private fun location(clientId: UUID, pixId: String) = HttpResponse
        .uri("/api/v1/clientes/$clientId/pix/$pixId")
}