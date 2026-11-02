package com.example.atividade_2bim_2sem

import android.content.ContentValues
import android.content.Context
import android.database.Cursor

class ConsultaDao(context: Context) {

    private val dbHelper = DatabaseHelper(context)

    fun insert(consulta: Consulta): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_CODIGO_PRODUTO, consulta.codigoProduto)
            put(DatabaseHelper.COLUMN_NOME_PRODUTO, consulta.nomeProduto)
            put(DatabaseHelper.COLUMN_MARCA, consulta.marca)
            put(DatabaseHelper.COLUMN_RESULTADO_JSON, consulta.resultadoJson)
            put(DatabaseHelper.COLUMN_DATA_HORA, consulta.dataHora)
        }
        return db.insert(DatabaseHelper.TABLE_CONSULTAS, null, values)
    }

    fun getAll(): List<Consulta> {
        val lista = mutableListOf<Consulta>()
        val db = dbHelper.readableDatabase
        val projection = arrayOf(
            DatabaseHelper.COLUMN_ID,
            DatabaseHelper.COLUMN_CODIGO_PRODUTO,
            DatabaseHelper.COLUMN_NOME_PRODUTO,
            DatabaseHelper.COLUMN_MARCA,
            DatabaseHelper.COLUMN_RESULTADO_JSON,
            DatabaseHelper.COLUMN_DATA_HORA
        )

        val cursor: Cursor = db.query(
            DatabaseHelper.TABLE_CONSULTAS,
            projection,
            null,
            null,
            null,
            null,
            "${DatabaseHelper.COLUMN_ID} DESC"
        )

        cursor.use { c ->
            val idIndex = c.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)
            val codigoIndex = c.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CODIGO_PRODUTO)
            val nomeIndex = c.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOME_PRODUTO)
            val marcaIndex = c.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MARCA)
            val jsonIndex = c.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RESULTADO_JSON)
            val dataHoraIndex = c.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DATA_HORA)

            while (c.moveToNext()) {
                val consulta = Consulta(
                    id = c.getLong(idIndex),
                    codigoProduto = c.getString(codigoIndex),
                    nomeProduto = c.getString(nomeIndex),
                    marca = c.getString(marcaIndex) ?: "Não informada",
                    resultadoJson = c.getString(jsonIndex),
                    dataHora = c.getString(dataHoraIndex)
                )
                lista.add(consulta)
            }
        }
        return lista
    }

    fun getCursor(): Cursor {
        val db = dbHelper.readableDatabase
        return db.rawQuery(
            "SELECT * FROM ${DatabaseHelper.TABLE_CONSULTAS} ORDER BY ${DatabaseHelper.COLUMN_ID} DESC",
            null
        )
    }

    fun getById(id: Long): Consulta? {
        val db = dbHelper.readableDatabase
        val projection = arrayOf(
            DatabaseHelper.COLUMN_ID,
            DatabaseHelper.COLUMN_CODIGO_PRODUTO,
            DatabaseHelper.COLUMN_NOME_PRODUTO,
            DatabaseHelper.COLUMN_MARCA,
            DatabaseHelper.COLUMN_RESULTADO_JSON,
            DatabaseHelper.COLUMN_DATA_HORA
        )

        val cursor = db.query(
            DatabaseHelper.TABLE_CONSULTAS,
            projection,
            "${DatabaseHelper.COLUMN_ID} = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )

        cursor.use { c ->
            if (c.moveToFirst()) {
                val idIndex = c.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)
                val codigoIndex = c.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CODIGO_PRODUTO)
                val nomeIndex = c.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOME_PRODUTO)
                val marcaIndex = c.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MARCA)
                val jsonIndex = c.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RESULTADO_JSON)
                val dataHoraIndex = c.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DATA_HORA)

                return Consulta(
                    id = c.getLong(idIndex),
                    codigoProduto = c.getString(codigoIndex),
                    nomeProduto = c.getString(nomeIndex),
                    marca = c.getString(marcaIndex) ?: "Não informada",
                    resultadoJson = c.getString(jsonIndex),
                    dataHora = c.getString(dataHoraIndex)
                )
            }
        }
        return null
    }
}
