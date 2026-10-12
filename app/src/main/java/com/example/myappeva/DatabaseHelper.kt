package com.example.myappeva

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "UsuariosDB"
        private const val DATABASE_VERSION = 8
        private const val TABLE_NAME = "usuarios"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NOMBRE = "nombre"
        private const val COLUMN_APELLIDOS = "apellidos"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_PASSWORD = "password"
        private const val COLUMN_IS_BLOCKED = "is_blocked"
        private const val COLUMN_RECOVERY_CODE = "recovery_code"
        private const val COLUMN_CODE_TIMESTAMP = "code_timestamp"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = ("CREATE TABLE $TABLE_NAME (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_NOMBRE TEXT, " +
                "$COLUMN_APELLIDOS TEXT, " +
                "$COLUMN_EMAIL TEXT UNIQUE, " +
                "$COLUMN_PASSWORD TEXT, " +
                "$COLUMN_IS_BLOCKED INTEGER DEFAULT 0, " +
                "$COLUMN_RECOVERY_CODE TEXT, " +
                "$COLUMN_CODE_TIMESTAMP INTEGER)")
        db?.execSQL(createTable)
        
        // Insertar solo 2 personajes de Genshin Impact para simplificar
        insertGenshinCharacters(db)
    }

    private fun insertGenshinCharacters(db: SQLiteDatabase?) {
        val genshinUsers = listOf(
            Triple("Diluc", "Ragnvindr", "diluc@genshin.com"),
            Triple("Jean", "Gunnhildr", "jean@genshin.com")
        )
        
        for (char in genshinUsers) {
            val values = ContentValues().apply {
                put(COLUMN_NOMBRE, char.first)
                put(COLUMN_APELLIDOS, char.second)
                put(COLUMN_EMAIL, char.third)
                put(COLUMN_PASSWORD, "Genshin123!") // Contraseña robusta por defecto
                put(COLUMN_IS_BLOCKED, 0)
            }
            db?.insert(TABLE_NAME, null, values)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addUser(user: User): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_NOMBRE, user.nombre)
        values.put(COLUMN_APELLIDOS, user.apellidos)
        values.put(COLUMN_EMAIL, user.email)
        values.put(COLUMN_PASSWORD, user.password)
        values.put(COLUMN_IS_BLOCKED, 0)
        
        val result = db.insert(TABLE_NAME, null, values)
        db.close()
        return result
    }

    fun getAllUsers(): List<User> {
        val userList = mutableListOf<User>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)

        if (cursor.moveToFirst()) {
            do {
                val user = User(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMBRE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_APELLIDOS)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD))
                )
                userList.add(user)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return userList
    }

    fun updateUser(user: User): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_NOMBRE, user.nombre)
        values.put(COLUMN_APELLIDOS, user.apellidos)
        values.put(COLUMN_EMAIL, user.email)
        
        val result = db.update(TABLE_NAME, values, "$COLUMN_ID = ?", arrayOf(user.id.toString()))
        db.close()
        return result
    }

    fun deleteUser(id: Int): Int {
        val db = this.writableDatabase
        val result = db.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(id.toString()))
        db.close()
        return result
    }

    fun checkUser(email: String, password: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COLUMN_EMAIL = ? AND $COLUMN_PASSWORD = ? AND $COLUMN_IS_BLOCKED = 0", arrayOf(email, password))
        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }

    fun isUserBlocked(email: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT $COLUMN_IS_BLOCKED FROM $TABLE_NAME WHERE $COLUMN_EMAIL = ?", arrayOf(email))
        var blocked = false
        if (cursor.moveToFirst()) {
            blocked = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_BLOCKED)) == 1
        }
        cursor.close()
        db.close()
        return blocked
    }

    fun initiateRecovery(email: String, code: String, timestamp: Long): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_IS_BLOCKED, 1)
            put(COLUMN_RECOVERY_CODE, code)
            put(COLUMN_CODE_TIMESTAMP, timestamp)
        }
        val rows = db.update(TABLE_NAME, values, "$COLUMN_EMAIL = ?", arrayOf(email))
        db.close()
        return rows > 0
    }

    fun verifyRecoveryCode(email: String, code: String): Int {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT $COLUMN_RECOVERY_CODE, $COLUMN_CODE_TIMESTAMP FROM $TABLE_NAME WHERE $COLUMN_EMAIL = ?", arrayOf(email))
        if (!cursor.moveToFirst()) {
            cursor.close()
            db.close()
            return 3 // Email not found
        }
        val savedCode = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RECOVERY_CODE))
        val timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_CODE_TIMESTAMP))
        cursor.close()
        db.close()

        if (savedCode != code) {
            return 2 // Invalid code
        }

        val currentTime = System.currentTimeMillis()
        val diff = currentTime - timestamp
        if (diff > 60000) { // 1 minute = 60,000 ms
            return 1 // Expired
        }

        return 0 // Success
    }

    fun updatePasswordAndUnblock(email: String, newPassword: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_PASSWORD, newPassword)
            put(COLUMN_IS_BLOCKED, 0)
            putNull(COLUMN_RECOVERY_CODE)
            putNull(COLUMN_CODE_TIMESTAMP)
        }
        val rows = db.update(TABLE_NAME, values, "$COLUMN_EMAIL = ?", arrayOf(email))
        db.close()
        return rows > 0
    }

    fun isEmailExists(email: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COLUMN_EMAIL = ?", arrayOf(email))
        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }

    fun isNameExists(nombre: String, apellidos: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COLUMN_NOMBRE = ? AND $COLUMN_APELLIDOS = ?", arrayOf(nombre, apellidos))
        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }

    fun isEmailExistsForOther(email: String, id: Int): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COLUMN_EMAIL = ? AND $COLUMN_ID != ?", arrayOf(email, id.toString()))
        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }

    fun isNameExistsForOther(nombre: String, apellidos: String, id: Int): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COLUMN_NOMBRE = ? AND $COLUMN_APELLIDOS = ? AND $COLUMN_ID != ?", arrayOf(nombre, apellidos, id.toString()))
        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }
}
