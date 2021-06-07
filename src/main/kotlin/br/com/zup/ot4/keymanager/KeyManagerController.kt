package br.com.zup.ot4.keymanager

import br.com.zup.ot4.KeyManagerServiceGrpc
import br.com.zup.ot4.keymanager.registry.KeyPostRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import java.util.*
import javax.inject.Inject
import javax.validation.Valid

@Controller("/api/v1/clientes/{clientId}")
@Validated
class KeyManagerController(
    @Inject val grpcClient: KeyManagerServiceGrpc.KeyManagerServiceBlockingStub
) {

    private val LOGGER = LoggerFactory.getLogger(this::class.java)


    @Post("/pix")
    fun register(clientId: UUID, @Valid @Body request: KeyPostRequest): HttpResponse<Any>{
        val grpcRequest = request.toGrpcRequest(clientId)

        LOGGER.info("[$clientId] criando nova chave pix com $request")
        val grpcResponse = grpcClient.register(grpcRequest)
        return HttpResponse.created(location(clientId, grpcResponse.pixId))
    }

    private fun location(clientId: UUID, pixId: String) = HttpResponse
        .uri("/api/v1/clientes/$clientId/pix/$pixId")
}