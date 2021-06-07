package br.com.zup.ot4.keymanager.registry

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*

internal class KeyTypeRequestTest{

    @Nested
    inner class RandomKeyTest{

        @Test
        internal fun `deve ser valido quando chave for nula ou vazia`() {
            val keyType = KeyTypeRequest.RANDOM_KEY

            assertTrue(keyType.validate(null))
            assertTrue(keyType.validate(""))
        }

        @Test
        internal fun `nao deve ser valido quando chave for preenchida`() {
            val keyType = KeyTypeRequest.RANDOM_KEY

            assertFalse(keyType.validate("magno@gmail.com"))
        }
    }

    @Nested
    inner class CpfKeyTest{

        @Test
        internal fun `deve ser valido quando chave tiver formato de CPF`() {
            val keyType = KeyTypeRequest.CPF

            assertTrue(keyType.validate("66368963026"))
        }

        @Test
        internal fun `nao deve ser valido quando CPF tiver formato invalido`() {
            val keyType = KeyTypeRequest.CPF

            assertFalse(keyType.validate("6636896302"))
        }

        @Test
        internal fun `nao deve ser valido quando CPF for nulo ou em branco`() {
            val keyType = KeyTypeRequest.CPF

            assertFalse(keyType.validate(null))
            assertFalse(keyType.validate(""))
        }
    }

    @Nested
    inner class EmailKeyTest {

        @Test
        internal fun `deve ser valido quando chave tiver formato de EMAIL`() {
            val keyType = KeyTypeRequest.EMAIL

            assertTrue(keyType.validate("magno@gmail.com"))
        }

        @Test
        internal fun `nao deve ser valido quando EMAIL tiver formato invalido`() {
            val keyType = KeyTypeRequest.EMAIL

            assertFalse(keyType.validate("random"))
        }

        @Test
        internal fun `nao deve ser valido quando EMAIL for nulo ou em branco`() {
            val keyType = KeyTypeRequest.EMAIL

            assertFalse(keyType.validate(null))
            assertFalse(keyType.validate(""))
        }
    }

    @Nested
    inner class PhoneKeyTest{

        @Test
        internal fun `deve ser valido quando chave tiver formato de PHONE`() {
            val keyType = KeyTypeRequest.PHONE_NUMBER

            assertTrue(keyType.validate("+5586988530000"))
        }

        @Test
        internal fun `nao deve ser valido quando PHONE tiver formato invalido`() {
            val keyType = KeyTypeRequest.EMAIL

            assertFalse(keyType.validate("random"))
        }

        @Test
        internal fun `nao deve ser valido quando PHONE for nulo ou em branco`() {
            val keyType = KeyTypeRequest.EMAIL

            assertFalse(keyType.validate(null))
            assertFalse(keyType.validate(""))
        }
    }

}