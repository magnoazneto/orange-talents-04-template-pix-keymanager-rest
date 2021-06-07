package br.com.zup.ot4.shared

import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.hateoas.JsonError
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class GlobalExceptionHandlerTest {

    val genericRequest = HttpRequest.GET<Any>("/")

    @Test
    internal fun `deve retornar 404 quando statusException for not found`() {
        val message = "nao encontrado"
        val notFoundException = StatusRuntimeException(Status.NOT_FOUND
            .withDescription(message))

        val response = GlobalExceptionHandler().handle(genericRequest, notFoundException)

        assertEquals(HttpStatus.NOT_FOUND, response.status)
        assertNotNull(response.body())
        assertEquals(message, (response.body() as JsonError).message)
    }

    @Test
    internal fun `deve retornar 422 quando statusException for already exists`() {
        val message = "chave duplicada"
        val alreadyExistsException = StatusRuntimeException(Status.ALREADY_EXISTS
            .withDescription(message))

        val response = GlobalExceptionHandler().handle(genericRequest, alreadyExistsException)

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.status)
        assertNotNull(response.body())
        assertEquals(message, (response.body() as JsonError).message)
    }

    @Test
    internal fun `deve retornar 400 quando statusException for invalid argument`() {
        val message = "dados invalidos"
        val invalidArgumentException = StatusRuntimeException(Status.INVALID_ARGUMENT
            .withDescription(message))

        val response = GlobalExceptionHandler().handle(genericRequest, invalidArgumentException)

        assertEquals(HttpStatus.BAD_REQUEST, response.status)
        assertNotNull(response.body())
        assertEquals(message, (response.body() as JsonError).message)
    }
}