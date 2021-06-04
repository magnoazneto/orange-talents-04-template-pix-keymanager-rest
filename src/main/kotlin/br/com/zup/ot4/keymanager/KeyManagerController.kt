package br.com.zup.ot4.keymanager

import br.com.zup.ot4.KeyManagerServiceGrpc
import br.com.zup.ot4.PixKeyRequest
import br.com.zup.ot4.PixKeyResponse
import br.com.zup.ot4.keymanager.registry.KeyPostRequest
import br.com.zup.ot4.keymanager.registry.KeyPostResponse
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.http.exceptions.HttpStatusException
import io.micronaut.validation.Validated
import javax.inject.Inject
import javax.validation.Valid

@Controller("/keys")
@Validated
class KeyManagerController(
    @Inject val grpcClient: KeyManagerServiceGrpc.KeyManagerServiceBlockingStub
) {


    @Post
    fun register(@Valid @Body request: KeyPostRequest): HttpResponse<Any>{
        println(request)
        val grpcRequest = PixKeyRequest.newBuilder()
            .setAccountType(request.accountType)
            .setKeyType(request.keyType)
            .setPixKey(request.key)
            .setExternalClientId(request.clientId)
            .build()

        return try{
            val response = grpcClient.register(grpcRequest)
            HttpResponse.created(KeyPostResponse(response.pixId))
        } catch (e: StatusRuntimeException){
            when(e.status.code){
                Status.Code.INVALID_ARGUMENT -> HttpResponse.badRequest(e.status.description)
                Status.Code.NOT_FOUND -> HttpResponse.notFound(e.status.description)
                Status.Code.FAILED_PRECONDITION -> HttpResponse.badRequest(e.status.description)
                else -> throw HttpStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message)
            }
        }

    }
}