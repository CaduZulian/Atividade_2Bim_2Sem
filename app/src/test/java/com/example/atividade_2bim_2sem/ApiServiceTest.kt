package com.example.atividade_2bim_2sem

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ApiServiceTest {

    @Test
    fun testExtrairEValidarCodigo() {
        // Códigos de barras válidos
        assertEquals("7891000100103", ApiService.extrairEValidarCodigo("7891000100103"))
        assertEquals("3017620422003", ApiService.extrairEValidarCodigo(" 3017620422003 "))
        assertEquals("12345678", ApiService.extrairEValidarCodigo("1234-5678"))

        // Entradas inválidas ou URLs
        assertNull(ApiService.extrairEValidarCodigo("http://world.openfoodfacts.org"))
        assertNull(ApiService.extrairEValidarCodigo("https://example.com/12345678"))
        assertNull(ApiService.extrairEValidarCodigo("texto_invalido"))
        assertNull(ApiService.extrairEValidarCodigo("12345")) // muito curto
    }

    @Test
    fun testProcessarJsonOpenFoodFactsFound() {
        val jsonInput = """
            {
              "code": "3017620422003",
              "status": 1,
              "status_verbose": "product found",
              "product": {
                "product_name": "Nutella",
                "brands": "Ferrero",
                "quantity": "400 g",
                "ingredients_text_pt": "Açúcar, óleo de palma, avelãs (13%).",
                "image_front_url": "https://images.openfoodfacts.org/front.jpg",
                "nutriments": {
                  "energy-kcal_100g": 539,
                  "carbohydrates_100g": 57.5,
                  "proteins_100g": 6.3,
                  "fat_100g": 30.9
                }
              }
            }
        """.trimIndent()

        val resultado = ApiService.processarJsonOpenFoodFacts("3017620422003", jsonInput)

        assertTrue("Produto deve ser considerado encontrado", resultado.encontrado)
        assertEquals("3017620422003", resultado.codigo)
        assertEquals("Nutella", resultado.nome)
        assertEquals("Ferrero", resultado.marca)
        assertEquals("400 g", resultado.quantidade)
        assertEquals("Açúcar, óleo de palma, avelãs (13%).", resultado.ingredientes)
        assertTrue("Deve conter informações nutricionais", resultado.tabelaNutricional.contains("Valor Energético"))
        assertEquals("https://images.openfoodfacts.org/front.jpg", resultado.urlImagem)
    }

    @Test
    fun testProcessarJsonOpenFoodFactsNotFound() {
        val jsonInput = """
            {
              "code": "0000000000000",
              "status": 0,
              "status_verbose": "product not found"
            }
        """.trimIndent()

        val resultado = ApiService.processarJsonOpenFoodFacts("0000000000000", jsonInput)

        assertFalse("Produto não deve ser encontrado", resultado.encontrado)
        assertTrue("Deve indicar produto não cadastrado", resultado.mensagemErro.contains("não cadastrado"))
    }

    @Test
    fun testProdutoResultadoStructuredJsonConversion() {
        val produtoOriginal = ProdutoResultado(
            encontrado = true,
            codigo = "7891000100103",
            nome = "Leite Condensado",
            marca = "Nestlé",
            quantidade = "395 g",
            ingredientes = "Leite integral, açúcar.",
            tabelaNutricional = "Valor Energético: 320 kcal",
            urlImagem = "https://images.openfoodfacts.org/nestle.jpg"
        )

        val jsonString = produtoOriginal.toStructuredJson()
        val produtoRestaurado = ProdutoResultado.fromStructuredJson(jsonString)

        assertEquals(produtoOriginal.codigo, produtoRestaurado.codigo)
        assertEquals(produtoOriginal.nome, produtoRestaurado.nome)
        assertEquals(produtoOriginal.marca, produtoRestaurado.marca)
        assertEquals(produtoOriginal.quantidade, produtoRestaurado.quantidade)
        assertEquals(produtoOriginal.ingredientes, produtoRestaurado.ingredientes)
        assertEquals(produtoOriginal.tabelaNutricional, produtoRestaurado.tabelaNutricional)
        assertEquals(produtoOriginal.urlImagem, produtoRestaurado.urlImagem)
    }
}
