package com.bignerdranch.android.criminalintent.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.bignerdranch.android.criminalintent.database.CrimeDbSchema.CrimeTable;

/**
 * при открытии базы данных всегда следует выполнить
 * ряд простых действий:
 * 1. Проверить, существует ли база данных.
 * 2. Если база данных не существует, создать ее, создать таблицы и заполнить их
 * необходимыми исходными данными.
 * 3. Если база данных существует, открыть ее и проверить версию CrimeDbSchema
 * (возможно, в будущих версиях CriminalIntent вы захотите добавить или уда-
 * лить какие-то аспекты).
 * 4. Если это старая версия, выполнить код преобразования ее в новую версию.
 * Android предоставляет класс SQLiteOpenHelper, который сделает это все
 * */
public class CrimeBaseHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "crimeBase.db";

    public CrimeBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    /**
     * код создания исходной базы данных размещается в onCreate(SQLiteDatabase)
     * */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + CrimeTable.NAME + "(" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                CrimeTable.Cols.UUID + ", " +
                CrimeTable.Cols.TITLE + ", " +
                CrimeTable.Cols.DATE + ", " +
                CrimeTable.Cols.SOLVED + ", " +
                CrimeTable.Cols.SUSPECT +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
