package com.example.pdietetyczny.database

import android.annotation.SuppressLint
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.Cursor
import com.example.pdietetyczny.models.FoodItem

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "Food_database.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "foods"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_CALORIES = "calories"
        private const val COLUMN_PROTEIN = "protein"
        private const val COLUMN_SUGAR = "sugar"
        private const val COLUMN_FAT = "fat"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = "CREATE TABLE $TABLE_NAME (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_NAME TEXT, " +
                "$COLUMN_CALORIES INTEGER, " +
                "$COLUMN_PROTEIN REAL, " +
                "$COLUMN_SUGAR REAL, " +
                "$COLUMN_FAT REAL)"
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    // Metoda wyszukująca produkt po nazwie
    @SuppressLint("Range")
    fun searchProductByName(productName: String): FoodItem? {
        val db = readableDatabase
        val selection = "$COLUMN_NAME LIKE ?"  // Szukamy w kolumnie `name`
        val selectionArgs = arrayOf("%$productName%") // Wyszukiwanie z dowolnymi znakami przed/po nazwie

        val cursor = db.query(
            TABLE_NAME, // Nazwa tabeli, np. "foods"
            null,       // Pobieramy wszystkie kolumny
            selection,  // Warunek WHERE
            selectionArgs, // Argumenty warunku
            null,
            null,
            null
        )

        // Jeśli znajdziemy rekord, wypełniamy obiekt FoodItem
        return if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID))
            val name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME))
            val calories = cursor.getFloat(cursor.getColumnIndex(COLUMN_CALORIES))
            val protein = cursor.getFloat(cursor.getColumnIndex(COLUMN_PROTEIN))
            val sugar = cursor.getFloat(cursor.getColumnIndex(COLUMN_SUGAR))
            val fat = cursor.getFloat(cursor.getColumnIndex(COLUMN_FAT))
            cursor.close()
            FoodItem(id, name, calories, protein, sugar, fat)
        } else {
            cursor.close()
            null // Jeśli nie znaleziono produktu
        }
    }
}
