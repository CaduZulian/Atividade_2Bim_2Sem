package com.example.atividade_2bim_2sem

import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.Serializable
import java.net.HttpURLConnection
import java.net.URL

data class ProdutoResultado(
    val encontrado: Boolean,
    val codigo: String,
    val nome: String = "",
    val marca: String = "",
    val quantidade: String = "",
    val ingredientes: String = "",
    val tabelaNutricional: String = "",
    val urlImagem: String = "",
    val mensagemErro: String = ""
) : Serializable {

    fun toStructuredJson(): String {
        val json = JSONObject()
        json.put("codigo", codigo)
        json.put("nome", nome)
        json.put("marca", marca)
        json.put("quantidade", quantidade)
        json.put("ingredientes", ingredientes)
        json.put("tabelaNutricional", tabelaNutricional)
        json.put("urlImagem", urlImagem)
        return json.toString()
    }

    companion object {
        fun fromStructuredJson(jsonString: String): ProdutoResultado {
            return try {
                val json = JSONObject(jsonString)
                ProdutoResultado(
                    encontrado = true,
                    codigo = json.optString("codigo", ""),
                    nome = json.optString("nome", "Produto"),
                    marca = json.optString("marca", "Não informada"),
                    quantidade = json.optString("quantidade", "Não informada"),
                    ingredientes = json.optString("ingredientes", "Não informados"),
                    tabelaNutricional = json.optString("tabelaNutricional", "Não disponível"),
                    urlImagem = json.optString("urlImagem", "")
                )
            } catch (e: Exception) {
                ProdutoResultado(
                    encontrado = false,
                    codigo = "",
                    mensagemErro = "Erro ao carregar dados salvos."
                )
            }
        }
    }
}

object ApiService {

    fun extrairEValidarCodigo(input: String): String? {
        val limpo = input.trim().replace(" ", "").replace("-", "")

        // Se for uma URL ou possuir letras/símbolos inválidos
        if (limpo.startsWith("http://", ignoreCase = true) ||
            limpo.startsWith("https://", ignoreCase = true) ||
            limpo.startsWith("www.", ignoreCase = true)
        ) {
            return null
        }

        // Valida se contém entre 8 e 14 dígitos numéricos
        if (limpo.matches(Regex("^\\d{8,14}$"))) {
            return limpo
        }

        return null
    }

    fun buscarProduto(codigo: String): ProdutoResultado {
        val urlString = "https://world.openfoodfacts.org/api/v2/product/$codigo.json"
        val url = URL(urlString)
        val connection = url.openConnection() as HttpURLConnection

        connection.requestMethod = "GET"
        connection.connectTimeout = 10000
        connection.readTimeout = 10000
        connection.setRequestProperty("User-Agent", "ConsultaFacil/1.0 (Android App)")

        try {
            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val stringBuilder = StringBuilder()
                var line: String?

                while (reader.readLine().also { line = it } != null) {
                    stringBuilder.append(line)
                }
                reader.close()

                return processarJsonOpenFoodFacts(codigo, stringBuilder.toString())
            } else {
                return ProdutoResultado(
                    encontrado = false,
                    codigo = codigo,
                    mensagemErro = "Servidor respondeu com erro HTTP $responseCode."
                )
            }
        } finally {
            connection.disconnect()
        }
    }

    fun processarJsonOpenFoodFacts(codigo: String, rawJson: String): ProdutoResultado {
        val root = JSONObject(rawJson)

        val status = root.optInt("status", 0)
        if (status == 0 || !root.has("product")) {
            val verbose = root.optString("status_verbose", "Produto não encontrado")
            return ProdutoResultado(
                encontrado = false,
                codigo = codigo,
                mensagemErro = "Produto não cadastrado na base Open Food Facts ($verbose)."
            )
        }

        val productObj = root.getJSONObject("product")

        val nome = when {
            productObj.has("product_name") && productObj.getString("product_name").isNotBlank() -> productObj.getString("product_name")
            productObj.has("product_name_pt") && productObj.getString("product_name_pt").isNotBlank() -> productObj.getString("product_name_pt")
            productObj.has("product_name_en") && productObj.getString("product_name_en").isNotBlank() -> productObj.getString("product_name_en")
            else -> "Produto sem nome cadastrado"
        }

        val marca = productObj.optString("brands", "").ifBlank { "Não informada" }
        val quantidade = productObj.optString("quantity", "").ifBlank { "Não informada" }

        val ingredientes = when {
            productObj.has("ingredients_text_pt") && productObj.getString("ingredients_text_pt").isNotBlank() -> productObj.getString("ingredients_text_pt")
            productObj.has("ingredients_text") && productObj.getString("ingredients_text").isNotBlank() -> productObj.getString("ingredients_text")
            productObj.has("ingredients_text_en") && productObj.getString("ingredients_text_en").isNotBlank() -> productObj.getString("ingredients_text_en")
            else -> "Ingredientes não informados"
        }

        val urlImagem = when {
            productObj.has("image_front_url") && productObj.getString("image_front_url").isNotBlank() -> productObj.getString("image_front_url")
            productObj.has("image_url") && productObj.getString("image_url").isNotBlank() -> productObj.getString("image_url")
            else -> ""
        }

        val tabelaNutricional = extrairTabelaNutricional(productObj)

        return ProdutoResultado(
            encontrado = true,
            codigo = codigo,
            nome = nome,
            marca = marca,
            quantidade = quantidade,
            ingredientes = ingredientes,
            tabelaNutricional = tabelaNutricional,
            urlImagem = urlImagem
        )
    }

    private fun extrairTabelaNutricional(productObj: JSONObject): String {
        val nutrimentsObj = productObj.optJSONObject("nutriments") ?: return "Informações nutricionais não disponíveis."

        val sb = StringBuilder()

        if (nutrimentsObj.has("energy-kcal_100g")) {
            sb.append("• Valor Energético: ").append(nutrimentsObj.opt("energy-kcal_100g")).append(" kcal / 100g\n")
        }
        if (nutrimentsObj.has("carbohydrates_100g")) {
            sb.append("• Carboidratos: ").append(nutrimentsObj.opt("carbohydrates_100g")).append(" g / 100g\n")
        }
        if (nutrimentsObj.has("proteins_100g")) {
            sb.append("• Proteínas: ").append(nutrimentsObj.opt("proteins_100g")).append(" g / 100g\n")
        }
        if (nutrimentsObj.has("fat_100g")) {
            sb.append("• Gorduras Totais: ").append(nutrimentsObj.opt("fat_100g")).append(" g / 100g\n")
        }
        if (nutrimentsObj.has("fiber_100g")) {
            sb.append("• Fibras: ").append(nutrimentsObj.opt("fiber_100g")).append(" g / 100g\n")
        }
        if (nutrimentsObj.has("salt_100g")) {
            sb.append("• Sal: ").append(nutrimentsObj.opt("salt_100g")).append(" g / 100g\n")
        }

        return if (sb.isEmpty()) {
            "Informações nutricionais não disponíveis."
        } else {
            sb.toString().trim()
        }
    }
}
