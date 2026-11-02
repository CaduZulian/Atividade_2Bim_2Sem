package com.example.atividade_2bim_2sem

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class ConsultaTest {

    @Test
    fun testConsultaCreation() {
        val consulta = Consulta(
            id = 1L,
            codigoProduto = "7891000100103",
            nomeProduto = "Leite Moça",
            marca = "Nestlé",
            resultadoJson = "{}",
            dataHora = "29/10/2026 20:00:00"
        )

        assertNotNull(consulta)
        assertEquals(1L, consulta.id)
        assertEquals("7891000100103", consulta.codigoProduto)
        assertEquals("Leite Moça", consulta.nomeProduto)
        assertEquals("Nestlé", consulta.marca)
        assertEquals("{}", consulta.resultadoJson)
        assertEquals("29/10/2026 20:00:00", consulta.dataHora)
    }
}
