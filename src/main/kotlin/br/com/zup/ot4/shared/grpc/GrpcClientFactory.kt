package br.com.zup.ot4.shared.grpc

import br.com.zup.ot4.KeyManagerServiceGrpc
import io.grpc.ManagedChannel
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import javax.inject.Singleton

@Factory
class GrpcClientFactory(@GrpcChannel("keyManager") val channel: ManagedChannel) {

    @Singleton
    fun keyManagerClientStub() = KeyManagerServiceGrpc.newBlockingStub(channel)
}