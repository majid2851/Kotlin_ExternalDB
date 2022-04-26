package com.example.kotlinexternaldb

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.os.Build
import android.util.Log
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream


class ScriptDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, VERSION) {
    private var myDatabase: SQLiteDatabase? = null
    private val mContext: Context

    override fun onCreate(db: SQLiteDatabase) {}

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}

    fun openDatabase() {
        val myPath = DB_PATH + DATABASE_NAME
        myDatabase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE)
    }

    @Throws(IOException::class)
    fun checkAndCopyDatabase() {
        val dbexist = checkDatabase()
        if (dbexist) {
            Log.d("TAG", "already database is ")
        } else {
            this.readableDatabase
        }
        copyDataBase()
    }

    @Throws(IOException::class)
    fun copyDataBase() {
        val myInput = mContext.assets.open(DATABASE_NAME)
        val outFileName = DB_PATH + DATABASE_NAME
        val myOutput: OutputStream = FileOutputStream(outFileName)
        val buffer = ByteArray(1024)
        var length: Int
        while (myInput.read(buffer).also { length = it } > 0) {
            myOutput.write(buffer, 0, length)
        }
        myOutput.flush()
        myOutput.close()
        myInput.close()
    }

    fun QueryData(query: String?): Cursor {
        return myDatabase!!.rawQuery(query, null)
    }

    fun checkDatabase(): Boolean {
        var checkDB: SQLiteDatabase? = null
        try {
            val myPath = DB_PATH + DATABASE_NAME
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE)
        } catch (e: SQLiteException) {
        }
        checkDB?.close()
        return checkDB != null
    }

    @Synchronized
    override fun close() {
        if (myDatabase != null) {
            myDatabase!!.close()
        }
        super.close()
    } 

    //--------------------------------------------------------------------------------------------------------
    val moviesList: List<Any>
        get() {
            val arr: MutableList<MovieModel> = ArrayList<MovieModel>()
            try {
                checkAndCopyDatabase()
                openDatabase()
            } catch (e: SQLiteException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            try {
                val res = QueryData(
                    "SELECT * FROM " + TABLE_MOVIES //+" WHERE "+COL2+"='"+word+"'"
                )
                if (res != null) {
                    while (res.moveToNext()) {
                        val indexModel = MovieModel(
                            res.getInt(0),
                            res.getString(1), res.getString(2),
                            res.getString(3), res.getString(4),
                            res.getString(5)
                        )
                        arr.add(indexModel)
                    }
                }
            } catch (e: SQLiteException) {
                e.printStackTrace()
            }
            return arr
        }

    fun getMovieById(id: Int): MovieModel? {
        var indexModel: MovieModel? = null
        try {
            checkAndCopyDatabase()
            openDatabase()
        } catch (e: SQLiteException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        try {
            val res = QueryData(
                "SELECT * FROM " + TABLE_MOVIES + " WHERE " + COL1 + "='" + id + "'"
            )
            if (res != null) {
                if (res.moveToNext()) {
                    indexModel = MovieModel(
                        res.getInt(0),
                        res.getString(1), res.getString(2),
                        res.getString(3), res.getString(4),
                        res.getString(5)
                    )
                    return indexModel
                }
            }
        } catch (e: SQLiteException) {
            e.printStackTrace()
        }
        return indexModel
    }

    //+" WHERE "+COL2+"='"+word+"'"
    val cartoonsList: List<Any>
        get() {
            val arr: MutableList<MovieModel> = ArrayList<MovieModel>()
            try {
                checkAndCopyDatabase()
                openDatabase()
            } catch (e: SQLiteException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            try {
                val res = QueryData(
                    "SELECT * FROM " + TABLE_CARTOONS //+" WHERE "+COL2+"='"+word+"'"
                )
                if (res != null) {
                    while (res.moveToNext()) {
                        val indexModel = MovieModel(
                            res.getInt(0),
                            res.getString(1), res.getString(2),
                            res.getString(3), res.getString(4),
                            res.getString(5)
                        )
                        arr.add(indexModel)
                    }
                }
            } catch (e: SQLiteException) {
                e.printStackTrace()
            }
            return arr
        }

    fun getCartoonsById(id: Int): MovieModel? {
        var model: MovieModel? = null
        try {
            checkAndCopyDatabase()
            openDatabase()
        } catch (e: SQLiteException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        try {
            val res =
                QueryData("SELECT * FROM " + TABLE_CARTOONS + " WHERE " + COL1 + "='" + id + "'")
            if (res != null) {
                while (res.moveToNext()) {
                    model = MovieModel(
                        res.getInt(0),
                        res.getString(1), res.getString(2),
                        res.getString(3), res.getString(4),
                        res.getString(5)
                    )
                    return model
                }
            }
        } catch (e: SQLiteException) {
            e.printStackTrace()
        }
        return model
    } //------------------------------------------------------------------------------------------------------

    companion object {
        const val DATABASE_NAME = "db_504.db"
        const val TABLE_MOVIES = "movies"
        const val TABLE_CARTOONS = "cartoons"
        private var DB_PATH = ""
        const val VERSION = 2
        const val COL1 = "id"
    }

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            DB_PATH = context.getDatabasePath(DATABASE_NAME).path
        } else {
            DB_PATH = context.applicationInfo.dataDir + "/databases/"
        }
        mContext = context
    }
}
