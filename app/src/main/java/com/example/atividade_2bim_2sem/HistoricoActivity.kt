package com.example.atividade_2bim_2sem

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class HistoricoActivity : AppCompatActivity() {

    private lateinit var listViewHistorico: ListView
    private lateinit var layoutEmptyState: LinearLayout
    private lateinit var btnVoltar: ImageButton
    private lateinit var btnPrimeiraConsulta: Button

    private lateinit var consultaDao: ConsultaDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historico)

        consultaDao = ConsultaDao(this)

        listViewHistorico = findViewById(R.id.listViewHistoricoCompleto)
        layoutEmptyState = findViewById(R.id.layoutEmptyState)
        btnVoltar = findViewById(R.id.btnVoltarHistorico)
        btnPrimeiraConsulta = findViewById(R.id.btnPrimeiraConsulta)

        btnVoltar.setOnClickListener {
            finish()
        }

        btnPrimeiraConsulta.setOnClickListener {
            finish()
        }

        listViewHistorico.setOnItemClickListener { _, _, position, _ ->
            val adapter = listViewHistorico.adapter
            if (adapter is ArrayAdapter<*>) {
                val item = adapter.getItem(position) as? Consulta
                if (item != null) {
                    abrirDetalhesConsulta(item)
                }
            }
        }

        carregarHistorico()
    }

    private fun carregarHistorico() {
        val listaConsultas = consultaDao.getAll()

        if (listaConsultas.isEmpty()) {
            layoutEmptyState.visibility = View.VISIBLE
            listViewHistorico.visibility = View.GONE
        } else {
            layoutEmptyState.visibility = View.GONE
            listViewHistorico.visibility = View.VISIBLE

            val adapter = object : ArrayAdapter<Consulta>(this, R.layout.item_consulta, listaConsultas) {
                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                    val view = convertView ?: layoutInflater.inflate(R.layout.item_consulta, parent, false)
                    val item = getItem(position)

                    val tvNome = view.findViewById<TextView>(R.id.tvNomeProdutoItem)
                    val tvCodigoMarca = view.findViewById<TextView>(R.id.tvCodigoMarcaItem)
                    val tvDataHora = view.findViewById<TextView>(R.id.tvDataHoraItem)

                    item?.let {
                        tvNome.text = it.nomeProduto
                        tvCodigoMarca.text = "Código: ${it.codigoProduto} | Marca: ${it.marca}"
                        tvDataHora.text = it.dataHora
                    }

                    return view
                }
            }

            listViewHistorico.adapter = adapter
        }
    }

    private fun abrirDetalhesConsulta(consulta: Consulta) {
        val produto = ProdutoResultado.fromStructuredJson(consulta.resultadoJson)
        val intent = Intent(this, DetalhesActivity::class.java)
        val bundle = Bundle().apply {
            putLong(DetalhesActivity.KEY_ID, consulta.id)
            putString(DetalhesActivity.KEY_CODIGO, consulta.codigoProduto)
            putString(DetalhesActivity.KEY_NOME, consulta.nomeProduto)
            putString(DetalhesActivity.KEY_MARCA, consulta.marca)
            putString(DetalhesActivity.KEY_QUANTIDADE, produto.quantidade)
            putString(DetalhesActivity.KEY_INGREDIENTES, produto.ingredientes)
            putString(DetalhesActivity.KEY_NUTRICAO, produto.tabelaNutricional)
            putString(DetalhesActivity.KEY_IMAGEM, produto.urlImagem)
            putString(DetalhesActivity.KEY_DATA_HORA, consulta.dataHora)
        }
        intent.putExtra(DetalhesActivity.EXTRA_BUNDLE, bundle)
        startActivity(intent)
    }
}
