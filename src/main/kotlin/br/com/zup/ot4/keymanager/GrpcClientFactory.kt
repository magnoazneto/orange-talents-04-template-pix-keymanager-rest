package br.com.zup.ot4.keymanager

import br.com.zup.ot4.KeyManagerServiceGrpc
import io.grpc.ManagedChannel
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import javax.inject.Singleton

@Factory
class GrpcClientFactory {

    @Singleton
    fun keyManagerClientStub(
        @GrpcChannel("keyManager") channel: ManagedChannel
    ) : KeyManagerServiceGrpc.KeyManagerServiceBlockingStub? {
        return KeyManagerServiceGrpc.newBlockingStub(channel)
    }
}