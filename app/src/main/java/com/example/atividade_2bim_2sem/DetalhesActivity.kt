package com.example.atividade_2bim_2sem

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.net.HttpURLConnection
import java.net.URL

class DetalhesActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_BUNDLE = "extra_bundle"
        const val KEY_ID = "key_id"
        const val KEY_CODIGO = "key_codigo"
        const val KEY_NOME = "key_nome"
        const val KEY_MARCA = "key_marca"
        const val KEY_QUANTIDADE = "key_quantidade"
        const val KEY_INGREDIENTES = "key_ingredientes"
        const val KEY_NUTRICAO = "key_nutricao"
        const val KEY_IMAGEM = "key_imagem"
        const val KEY_DATA_HORA = "key_data_hora"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalhes)

        val btnVoltarTopo: ImageButton = findViewById(R.id.btnVoltarTopo)
        val tvNomeProduto: TextView = findViewById(R.id.tvNomeProduto)
        val tvMarca: TextView = findViewById(R.id.tvMarca)
        val tvCodigo: TextView = findViewById(R.id.tvCodigo)
        val tvQuantidade: TextView = findViewById(R.id.tvQuantidade)
        val tvIngredientes: TextView = findViewById(R.id.tvIngredientes)
        val tvTabelaNutricional: TextView = findViewById(R.id.tvTabelaNutricional)
        val cardImagem: LinearLayout = findViewById(R.id.cardImagem)
        val imgProduto: ImageView = findViewById(R.id.imgProduto)
        val tvUrlImagem: TextView = findViewById(R.id.tvUrlImagem)
        val btnVoltar: Button = findViewById(R.id.btnVoltar)
        val btnVerHistoricoDetalhes: Button = findViewById(R.id.btnVerHistoricoDetalhes)

        btnVoltarTopo.setOnClickListener { finish() }
        btnVoltar.setOnClickListener { finish() }

        btnVerHistoricoDetalhes.setOnClickListener {
            startActivity(Intent(this, HistoricoActivity::class.java))
        }

        val bundle = intent.getBundleExtra(EXTRA_BUNDLE) ?: intent.extras

        if (bundle != null) {
            val nome = bundle.getString(KEY_NOME, "Produto sem nome")
            val marca = bundle.getString(KEY_MARCA, "Não informada")
            val codigo = bundle.getString(KEY_CODIGO, "Não informado")
            val quantidade = bundle.getString(KEY_QUANTIDADE, "Não informada")
            val ingredientes = bundle.getString(KEY_INGREDIENTES, "Ingredientes não informados")
            val nutricao = bundle.getString(KEY_NUTRICAO, "Informações nutricionais não disponíveis")
            val urlImagem = bundle.getString(KEY_IMAGEM, "")

            tvNomeProduto.text = nome
            tvMarca.text = if (marca.isNotBlank()) marca else "Não informada"
            tvCodigo.text = "Código: ${if (codigo.isNotBlank()) codigo else "Não informado"}"
            tvQuantidade.text = if (quantidade.isNotBlank()) quantidade else "Não informada"
            tvIngredientes.text = if (ingredientes.isNotBlank()) ingredientes else "Ingredientes não informados"
            tvTabelaNutricional.text = if (nutricao.isNotBlank()) nutricao else "Informações nutricionais não disponíveis"

            if (!urlImagem.isNullOrBlank()) {
                cardImagem.visibility = View.VISIBLE
                tvUrlImagem.text = urlImagem

                // Carregamento assíncrono da imagem em Thread secundária
                Thread {
                    try {
                        val url = URL(urlImagem)
                        val connection = url.openConnection() as HttpURLConnection
                        connection.doInput = true
                        connection.connectTimeout = 10000
                        connection.readTimeout = 10000
                        connection.setRequestProperty("User-Agent", "ConsultaFacil/1.0 (Android App)")
                        connection.connect()

                        val inputStream = connection.inputStream
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        inputStream.close()

                        if (bitmap != null) {
                            runOnUiThread {
                                imgProduto.setImageBitmap(bitmap)
                                imgProduto.visibility = View.VISIBLE
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }.start()
            } else {
                cardImagem.visibility = View.GONE
            }
        } else {
            tvNomeProduto.text = "Detalhes do produto não encontrados."
        }
    }
}
