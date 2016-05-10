package ru.volt.demovolt.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import ru.volt.demovolt.AppConfig;
import ru.volt.demovolt.RecordType;

/**
 * Created by dave on 07.05.16.
 */
public class DbHelper extends SQLiteOpenHelper {
    private static final int    DB_VERSION = 1;
    private static final String VERSION   = "0." + String.valueOf(DB_VERSION);
    private static final String DB_NAME   = "demovolt.db";


    private SQLiteDatabase myDataBase      = null;
    private String         myDataBaseError = null;

    private static DbHelper instance = null;

    public SQLiteDatabase getDatabase() {
        return this.myDataBase;
    }

    /**
     *
     * @return
     */
    public static DbHelper getInstance() {
        if (instance == null) {
            synchronized (DbHelper.class) {
                if (instance == null) {
                    try {
                        instance = new DbHelper();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
        return instance;
    }

    /**
     * Открыть базу
     *
     * @throws IOException
     * @throws InterruptedException
     */
    private DbHelper() throws IOException, InterruptedException {
        super(AppConfig.getContext(), DB_NAME, null, DB_VERSION);

        while (getDbExstAndRdble() != true) {
            this.getReadableDatabase();
            synchronized (this) {
                wait(AppConfig.DELAY);
            }
        }

        myDataBase = SQLiteDatabase.openDatabase(getDbPath(),
                null, SQLiteDatabase.OPEN_READWRITE);
        if (myDataBase == null) {
            throw new IOException();
        }
    }

    /**
     *
     * @return База существует и ее можно открыть на чтение?
     */
    public boolean getDbExstAndRdble() {
        File f = AppConfig.getContext().getDatabasePath(DB_NAME);
        boolean ret = f.canRead() && f.exists();
        return ret;
    }

    /**
     *
     * @return Путь к БД
     */
    private static String getDbPath() {
        return AppConfig.getContext().getDatabasePath(DB_NAME).toString();
    }

    /**
     *
     * @return База данных создана?
     */
    public static boolean checkDatabase() {
        File dbFile = new File(getDbPath());
        return dbFile.exists();
    }



    /**
     * Синхронизированное закрытие базы
     */
    public synchronized void close() {
        if (myDataBase != null && myDataBase.isOpen()) {
            myDataBase.close();
        }
        super.close();
    }

    /**
     * Создание базы
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            db.execSQL(Sql.sqlTableRecord);
            db.execSQL(Sql.sqlTableFavorites);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * Обновление базы при смене версии
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(Sql.sqlDropTableRecord);
        db.execSQL(Sql.sqlDropTableFavorites);
        onCreate(db);
    }

    /**
     * Очистка базы
     */
    private void clearDb() {
        myDataBase.beginTransaction();
        try {
            myDataBase.execSQL(Sql.sqlDeleteTableRecord);
            myDataBase.execSQL(Sql.sqlDeleteTableFavorites);
            myDataBase.setTransactionSuccessful();
        } finally {
            myDataBase.endTransaction();
        }
    }

    /**
     * Вернуть первое значение поля
     * @param tableName    - Выбор таблицы
     * @param fieldName    - Выбор поля
     * @param orderField   - Если заданно - сортировка
     * @param desc         - Обратная сортировка
     * @return               Значение либо null
     */
    private String getFirstValue(String tableName, String fieldName,
                                String orderField, boolean desc) {
        String ret = null;
        Cursor cursor = null;
        try {
            String sql = null;
            if (orderField != null) {
                sql = "select " + fieldName + " from " + tableName + " order by " + orderField;
                if (desc) {
                    sql = sql + " desc";
                }
            } else {
                sql = "select " + fieldName + " from " + tableName;
            }

            cursor = myDataBase.rawQuery(sql, null);

            if (cursor.getCount() >= 1) {
                cursor.moveToFirst();
                ret = cursor.getString(0);
            }

        } finally {
            cursor.close();
        }
        return ret;
    }

    /**
     * Возвращает значение поля из выбранной таблицы(поиск по одному полю)
     * @param tableName       - Таблица из которой хотим получить данные
     * @param fieldName       - Поле по которому будет производится поиск результата
     * @param fieldValue      - Значение поля по которому идет поиск
     * @param returnFieldName - Возвращаемое значение по этому полю(возвращается первая запись)
     * @return                  Значение поля либо null
     */
    private String getField(String tableName, String fieldName,
                           String fieldValue, String returnFieldName) {
        final String sql = "select " + returnFieldName + " from " + tableName
                + " where " + fieldName + " = \"" + fieldValue + "\"";
        Cursor curr = myDataBase.rawQuery(sql, null);
        String retId = null;
        try {
            if (curr.getCount() > 0) {
                curr.moveToFirst();
                int idx = curr.getColumnIndex(returnFieldName);
                retId = curr.getString(idx);
            }
        } finally {
            curr.close();
        }
        return retId;
    }

    /**
     * Обновить поле в таблице
     * @param tableName       - Выбор таблицы
     * @param fieldName       - Выбор поля
     * @param newFieldValue   - Новое значение для поля
     * @param whereFieldName  - Условия поиска записи, имя поля
     * @param whereFieldValue - Условие поиска записи, значение поля
     * @return Успех операции
     */
    private boolean updateField(String tableName, String fieldName,
                               String newFieldValue, String whereFieldName, String whereFieldValue) {

        myDataBase.beginTransaction();
        try {
            final String sql = "update " + tableName + " set " + fieldName
                    + " = \"" + newFieldValue + "\" where " + whereFieldName
                    + " = \"" + whereFieldValue + "\"";
            myDataBase.execSQL(sql);

            myDataBase.setTransactionSuccessful();
        } catch (SQLException e) {
            return false;
        } finally {
            myDataBase.endTransaction();
        }
        return true;
    }

    /**
     * Добавить запись
     * @param record
     */
    public void insertIntoRecord(RecordType record) {
        myDataBase.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(Sql.FLD_USER_ID, record.getUserId());
            values.put(Sql.FLD_RECORD_ID, record.getRecordId());
            values.put(Sql.FLD_TITLE, record.getTitle());
            values.put(Sql.FLD_BODY, record.getBody());

            myDataBase.insert(Sql.TBL_RECORD, null, values);
            myDataBase.setTransactionSuccessful();
        } finally{
            myDataBase.endTransaction();
        }
    }

    /**
     *
     * @return
     */
    public RecordType[] selectRecords() {
        final String sql = "select * from " + Sql.TBL_RECORD;
        Cursor curr = myDataBase.rawQuery(sql, null);
        RecordType[] records = null;
        try {
            final int size = curr.getCount();
            if (size >= 1) {
                records = new RecordType[size];
                curr.moveToFirst();

                int idxField = -1;
                int recordId = -1;
                for (int i = 0; i < size; i++) {
                    records[i] = new RecordType();

                    idxField = curr.getColumnIndex(Sql.FLD_USER_ID);
                    if (idxField >= 0) {
                        records[i].setUserId(curr.getInt(idxField));
                    }

                    idxField = curr.getColumnIndex(Sql.FLD_RECORD_ID);
                    if (idxField >= 0) {
                        recordId = curr.getInt(idxField);
                        records[i].setRecordId(recordId);
                    }

                    idxField = curr.getColumnIndex(Sql.FLD_TITLE);
                    if (idxField >= 0) {
                        records[i].setTitle(curr.getString(idxField));
                    }

                    idxField = curr.getColumnIndex(Sql.FLD_BODY);
                    if (idxField >= 0) {
                        records[i].setBody(curr.getString(idxField));
                    }

                    records[i].setFavorites(selectFavoriteFlag(recordId));

                    curr.moveToNext();
                }
            }
        } finally {
            curr.close();
        }
        return records;
    }

    private boolean selectFavoriteFlag(Integer recordId) {
        boolean flag = false;
        final String sql = "select " + Sql.FLD_FAVORITES + " from " + Sql.TBL_FAVORITES
                + " where " + Sql.FLD_RECORD_ID + " = " + recordId.toString();
        Cursor curr = myDataBase.rawQuery(sql, null);
        if (curr != null) {
            try {
                final int size = curr.getCount();
                if (size > 0) {
                    curr.moveToFirst();
                    int idx = curr.getColumnIndex(Sql.FLD_FAVORITES);
                    if (curr.getInt(idx) == 0) {
                        flag = false;
                    } else {
                        flag = true;
                    }
                }
            } finally {
                curr.close();
            }
        }
        return flag;
    }

    /**
     *
     * @param recordId
     * @param flag
     */
    private void insertIntoFavorites(Integer recordId, boolean flag) {
        myDataBase.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(Sql.FLD_RECORD_ID, recordId);
            values.put(Sql.FLD_FAVORITES, (flag == true) ? 1 : 0);
            myDataBase.insert(Sql.TBL_FAVORITES, null, values);
            myDataBase.setTransactionSuccessful();
        } finally{
            myDataBase.endTransaction();
        }
    }

    /**
     *
     * @param record
     * @param flag
     */
    public void setFavoritesFlag(RecordType record, boolean flag) {
        String systemId = getField(Sql.TBL_FAVORITES, Sql.FLD_RECORD_ID,
                String.valueOf(record.getRecordId()), Sql.FLD_SYSTEM_ID);
        if (systemId == null) {
            insertIntoFavorites(record.getRecordId(), flag);
        } else {
            updateField(Sql.TBL_FAVORITES, Sql.FLD_FAVORITES, (flag == true) ? "1" : "0",
                    Sql.FLD_SYSTEM_ID, systemId);
        }
    }
}
