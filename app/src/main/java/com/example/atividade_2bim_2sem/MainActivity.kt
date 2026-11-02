package com.example.atividade_2bim_2sem

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var btnEscanear: Button
    private lateinit var etCodigoManual: EditText
    private lateinit var btnBuscarManual: Button
    private lateinit var cardLoading: LinearLayout
    private lateinit var tvEmptyHistory: TextView
    private lateinit var listViewHistorico: ListView
    private lateinit var btnVerHistoricoCompleto: Button

    private lateinit var consultaDao: ConsultaDao

    private val qrScannerLauncher = registerForActivityResult(ScanContract()) { result ->
        if (result.contents != null) {
            processarCodigoLido(result.contents)
        } else {
            Toast.makeText(this, "Leitura do QR Code cancelada.", Toast.LENGTH_SHORT).show()
        }
    }

    private val requestCameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            iniciarScannerQrCode()
        } else {
            Toast.makeText(this, "Permissão de câmera é necessária para escanear QR Code.", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        consultaDao = ConsultaDao(this)

        btnEscanear = findViewById(R.id.btnEscanear)
        etCodigoManual = findViewById(R.id.etCodigoManual)
        btnBuscarManual = findViewById(R.id.btnBuscarManual)
        cardLoading = findViewById(R.id.cardLoading)
        tvEmptyHistory = findViewById(R.id.tvEmptyHistory)
        listViewHistorico = findViewById(R.id.listViewHistorico)
        btnVerHistoricoCompleto = findViewById(R.id.btnVerHistoricoCompleto)

        btnEscanear.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                iniciarScannerQrCode()
            } else {
                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }

        btnBuscarManual.setOnClickListener {
            val entrada = etCodigoManual.text.toString().trim()
            if (entrada.isNotEmpty()) {
                processarCodigoLido(entrada)
                etCodigoManual.text.clear()
            } else {
                Toast.makeText(this, "Por favor, digite um código de barras de produto.", Toast.LENGTH_SHORT).show()
            }
        }

        btnVerHistoricoCompleto.setOnClickListener {
            startActivity(Intent(this, HistoricoActivity::class.java))
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
    }

    override fun onResume() {
        super.onResume()
        carregarHistorico()
    }

    private fun iniciarScannerQrCode() {
        val options = ScanOptions().apply {
            setPrompt("Aponte a câmera para o QR Code do produto")
            setBeepEnabled(true)
            setOrientationLocked(true)
            setCaptureActivity(CustomScannerActivity::class.java)
        }
        qrScannerLauncher.launch(options)
    }

    private fun processarCodigoLido(input: String) {
        val codigoValido = ApiService.extrairEValidarCodigo(input)

        if (codigoValido == null) {
            Toast.makeText(
                this,
                "Código inválido: O QR Code não contém um código de barras de produto válido (8 a 14 dígitos).",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        Toast.makeText(this, "Iniciando consulta para o produto: $codigoValido", Toast.LENGTH_SHORT).show()
        cardLoading.visibility = View.VISIBLE

        Thread {
            try {
                val produtoResult = ApiService.buscarProduto(codigoValido)

                if (produtoResult.encontrado) {
                    val dataHoraAtual = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date())

                    val consulta = Consulta(
                        codigoProduto = produtoResult.codigo,
                        nomeProduto = produtoResult.nome,
                        marca = produtoResult.marca,
                        resultadoJson = produtoResult.toStructuredJson(),
                        dataHora = dataHoraAtual
                    )

                    val idSalvo = consultaDao.insert(consulta)

                    runOnUiThread {
                        cardLoading.visibility = View.GONE
                        Toast.makeText(this, "Produto encontrado e salvo no histórico!", Toast.LENGTH_SHORT).show()
                        carregarHistorico()
                        abrirDetalhesProduto(produtoResult, dataHoraAtual, idSalvo)
                    }
                } else {
                    runOnUiThread {
                        cardLoading.visibility = View.GONE
                        Toast.makeText(this, produtoResult.mensagemErro, Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                val erroMsg = e.localizedMessage ?: "Falha ao conectar à API Open Food Facts"
                runOnUiThread {
                    cardLoading.visibility = View.GONE
                    Toast.makeText(this, "Erro de conexão: $erroMsg", Toast.LENGTH_LONG).show()
                }
            }
        }.start()
    }

    private fun carregarHistorico() {
        val listaConsultas = consultaDao.getAll()

        if (listaConsultas.isEmpty()) {
            tvEmptyHistory.visibility = View.VISIBLE
            listViewHistorico.visibility = View.GONE
        } else {
            tvEmptyHistory.visibility = View.GONE
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

    private fun abrirDetalhesProduto(produto: ProdutoResultado, dataHora: String, id: Long) {
        val intent = Intent(this, DetalhesActivity::class.java)
        val bundle = Bundle().apply {
            putLong(DetalhesActivity.KEY_ID, id)
            putString(DetalhesActivity.KEY_CODIGO, produto.codigo)
            putString(DetalhesActivity.KEY_NOME, produto.nome)
            putString(DetalhesActivity.KEY_MARCA, produto.marca)
            putString(DetalhesActivity.KEY_QUANTIDADE, produto.quantidade)
            putString(DetalhesActivity.KEY_INGREDIENTES, produto.ingredientes)
            putString(DetalhesActivity.KEY_NUTRICAO, produto.tabelaNutricional)
            putString(DetalhesActivity.KEY_IMAGEM, produto.urlImagem)
            putString(DetalhesActivity.KEY_DATA_HORA, dataHora)
        }
        intent.putExtra(DetalhesActivity.EXTRA_BUNDLE, bundle)
        startActivity(intent)
    }

    private fun abrirDetalhesConsulta(consulta: Consulta) {
        val produto = ProdutoResultado.fromStructuredJson(consulta.resultadoJson)
        abrirDetalhesProduto(produto, consulta.dataHora, consulta.id)
    }
}
