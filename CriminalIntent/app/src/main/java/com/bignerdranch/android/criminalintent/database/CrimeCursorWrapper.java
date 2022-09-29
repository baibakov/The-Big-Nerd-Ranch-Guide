package com.bignerdranch.android.criminalintent.database;

import static com.bignerdranch.android.criminalintent.database.CrimeDbSchema.CrimeTable;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.bignerdranch.android.criminalintent.Crime;

import java.util.Date;
import java.util.UUID;

/**
 * Вместо того чтобы записывать этот
 * код при каждом чтении данных из курсора, реализуем собственный суб-
 * класс Cursor, который выполняет эту операцию в одном месте. Для написания
 * субкласса курсора проще всего воспользоваться CursorWrapper — этот класс по-
 * зволяет дополнить класс Cursor, полученный извне, новыми методами.
 * */
public class CrimeCursorWrapper extends CursorWrapper {

    public CrimeCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Crime getCrime() {
        String uuidString = getString(getColumnIndex(CrimeTable.Cols.UUID));
        String title = getString(getColumnIndex(CrimeTable.Cols.TITLE));
        long date = getLong(getColumnIndex(CrimeTable.Cols.DATE));
        int isSolved = getInt(getColumnIndex(CrimeTable.Cols.SOLVED));
        String suspect = getString(getColumnIndex(CrimeTable.Cols.SUSPECT));

        Crime crime = new Crime(UUID.fromString(uuidString));
        crime.setTitle(title);
        crime.setDate(new Date(date));
        crime.setSolved(isSolved != 0);
        crime.setSuspect(suspect);
        return crime;
    }
}
