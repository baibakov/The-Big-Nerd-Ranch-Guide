package com.bignerdranch.android.criminalintent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bignerdranch.android.criminalintent.database.CrimeBaseHelper;
import com.bignerdranch.android.criminalintent.database.CrimeCursorWrapper;
import com.bignerdranch.android.criminalintent.database.CrimeDbSchema.CrimeTable;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CrimeLab {

    private static CrimeLab sCrimeLab;

    //private List<Crime> mCrimes;
    private Context mContext;
    private SQLiteDatabase mDataBase;

    private CrimeLab(Context context) {
        mContext = context.getApplicationContext();
        /**
         * При вызове getWritableDatabase() класс CrimeBaseHelper выполняет следующее:
         * 1) открывает /data/data/com.bignerdranch.android.criminalintent/databases/crimeBase.db.
         * Если файл базы данных не существует, то он создается;
         * 2) если база данных открывается впервые, вызывает метод onCreate(SQLite­
         * Database) с последующим сохранением последнего номера версии;
         * 3) если база данных открывается не впервые, проверяет номер ее версии.
         * Если версия базы данных в CrimeOpenHelper выше, то вызывается метод
         * onUpgrade(SQLiteDatabase, int, int).
         * */
        mDataBase = new CrimeBaseHelper(mContext).getWritableDatabase();

    }

    public static CrimeLab get(Context context) {
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    /**
     * Добавление нового преступления
     * */
    public void addCrime(Crime c) {
        ContentValues values = getContentValues(c);
        mDataBase.insert(CrimeTable.NAME, null, values);
    }

    /**
     * Обновление данных в таблице
     * */
    public void updateCrime(Crime crime) {
        String uuidString = crime.getId().toString();
        ContentValues values = getContentValues(crime);

        mDataBase.update(CrimeTable.NAME, values, CrimeTable.Cols.UUID + " = ?", new String[] {uuidString});
    }

    /**
     * Чтение данных из SQLite.
     * Курсор, полученный при запросе, упаковывается в CrimeCursorWrapper,
     * после чего его содержимое перебирается методом getCrime() для получения объ-
     * ектов Crime.
     * */
    private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs) {
        Cursor cursor = mDataBase.query(
                CrimeTable.NAME,
                null,           // columns - c null выбираются все столбцы
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        //return cursor;
        return new CrimeCursorWrapper(cursor);
    }

    /**
     * Функция заполняет список из БД и возвращает его
     * Таким образом, чтобы извлечь данные из курсора, его следует
     * перевести к первому элементу вызовом moveToFirst(), а затем прочитать данные
     * строки. Каждый раз, когда потребуется перейти к следующей записи, мы вызыва-
     * ем moveToNext(), пока isAfterLast() наконец не сообщит, что указатель вышел за
     * пределы набора данных.
     * */
    public List<Crime> getCrimes() {
        List<Crime> crimes = new ArrayList<>();

        CrimeCursorWrapper cursor = queryCrimes(null, null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                crimes.add(cursor.getCrime());
                cursor.moveToNext();
            }
        } finally {
            /**
             * Последнее, что осталось сделать, — вызвать close() для объекта Cursor. Не за-
             * бывайте об этой служебной операции, это важно. Если вы забудете закрыть кур-
             * сор, устройство Android начнет выдавать в журнал сообщения об ошибках. Что
             * еще хуже, если ваша забывчивость будет проявляться хронически, со временем
             * это приведет к исчерпанию файловых дескрипторов и сбою приложения.
             * */
            cursor.close();
        }
        return crimes;
    }

    /**
     * ContentValues обеспечивает хранение пар «ключ-значение», как и контей-
     * нер Java HashMap или объекты Bundle, уже встречавшиеся вам ранее. Однако в от-
     * личие от HashMap или Bundle, он предназначен для хранения типов данных, кото-
     * рые могут содержаться в базах данных SQLite.
     * */
    private static ContentValues getContentValues(Crime crime) {
        ContentValues values = new ContentValues();
        values.put(CrimeTable.Cols.UUID, crime.getId().toString());
        values.put(CrimeTable.Cols.TITLE, crime.getTitle());
        values.put(CrimeTable.Cols.DATE, crime.getDate().getTime());
        values.put(CrimeTable.Cols.SOLVED, crime.isSolved() ? 1 : 0);
        values.put(CrimeTable.Cols.SUSPECT, crime.getSuspect());
        return values;
    }

    public Crime getCrime(UUID id) {
        CrimeCursorWrapper cursor = queryCrimes(
                CrimeTable.Cols.UUID + " = ?",
                new String[] {id.toString()}
        );

        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getCrime();
        } finally {
            cursor.close();
        }
    }
}
