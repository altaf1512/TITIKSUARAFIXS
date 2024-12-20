package com.arin.titik_suara.Util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "titik_suara.db";
    private static final int DATABASE_VERSION = 1;

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableUser = "CREATE TABLE user (id_user TEXT PRIMARY KEY, nama TEXT, jabatan TEXT, alamat TEXT, no_telepon TEXT, foto_profil TEXT)";
        db.execSQL(createTableUser);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS user");
        onCreate(db);
    }

    public void updateUser(String id, String nama, String jabatan, String alamat, String noTelp, String fotoProfil) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE user SET nama=?, jabatan=?, alamat=?, no_telepon=?, foto_profil=? WHERE id_user=?",
                new Object[]{nama, jabatan, alamat, noTelp, fotoProfil, id});
    }
}