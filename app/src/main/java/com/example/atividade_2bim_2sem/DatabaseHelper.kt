package com.example.atividade_2bim_2sem

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "historico_consultas.db"
        const val DATABASE_VERSION = 2

        const val TABLE_CONSULTAS = "consultas"
        const val COLUMN_ID = "_id"
        const val COLUMN_CODIGO_PRODUTO = "codigo_produto"
        const val COLUMN_NOME_PRODUTO = "nome_produto"
        const val COLUMN_MARCA = "marca"
        const val COLUMN_RESULTADO_JSON = "resultado_json"
        const val COLUMN_DATA_HORA = "data_hora"

        private const val CREATE_TABLE_CONSULTAS = """
            CREATE TABLE $TABLE_CONSULTAS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_CODIGO_PRODUTO TEXT NOT NULL,
                $COLUMN_NOME_PRODUTO TEXT NOT NULL,
                $COLUMN_MARCA TEXT,
                $COLUMN_RESULTADO_JSON TEXT NOT NULL,
                $COLUMN_DATA_HORA TEXT NOT NULL
            )
        """
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE_CONSULTAS)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL("DROP TABLE IF EXISTS $TABLE_CONSULTAS")
            onCreate(db)
        }
    }
}
