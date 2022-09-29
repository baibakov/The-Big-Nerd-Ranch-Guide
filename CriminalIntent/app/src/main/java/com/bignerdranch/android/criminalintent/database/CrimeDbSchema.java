package com.bignerdranch.android.criminalintent.database;

/**
 * Класс описывает схему хранения
 * */
public class CrimeDbSchema {

    /**
     * CrimeTable существует только для определения строковых констант, необ-
     * ходимых для описания основных частей определения таблицы. Определение на-
     * чинается с имени таблицы в базе данных CrimeTable.NAME, за которым следуют
     * описания столбцов.
     * */
    public static final class CrimeTable {
        public static final String NAME = "crimes";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String SOLVED = "solved";
            public static final String SUSPECT = "suspect";
        }
    }
}
