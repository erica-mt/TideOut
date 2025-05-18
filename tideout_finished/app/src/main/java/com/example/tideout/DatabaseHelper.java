package com.example.tideout;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "praias.db";
    private static final int DATABASE_VERSION = 1;
    private final Context context;
    private String databasePath;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        databasePath = context.getDatabasePath(DATABASE_NAME).getPath();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        copyDatabase();
        db.execSQL("CREATE TABLE IF NOT EXISTS favorites (id INTEGER PRIMARY KEY, beach_id INTEGER UNIQUE)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    public void copyDatabase() {
        try {
            File dbFile = new File(databasePath);
            if (!dbFile.exists()) {
                copyDBFromAssets();
            }
        } catch (IOException e) {
            Log.e("DatabaseHelper","Erro ao copiar base de dados.", e);
        }
    }

    private void copyDBFromAssets() throws IOException {
        InputStream input = context.getAssets().open(DATABASE_NAME);
        OutputStream output = new FileOutputStream(databasePath);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = input.read(buffer)) > 0) {
            output.write(buffer, 0, length);
        }
        output.flush();
        output.close();
        input.close();
    }

    public SQLiteDatabase openDatabase() {
        return SQLiteDatabase.openDatabase(databasePath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    public Cursor getData(String query) {
        SQLiteDatabase db = this.openDatabase();
        return db.rawQuery(query, null);
    }



    public List<ResultModel> searchbyName(String searchText) {
        SQLiteDatabase db = openDatabase();
        List<ResultModel> results = new ArrayList<>();

        String query = "SELECT praias.*, concelho.nome AS concelho_nome FROM praias JOIN concelho ON praias.id_concelho = concelho.id WHERE praias.nome_praia LIKE ?";
        Cursor cursor = db.rawQuery(query, new String[]{"%" + searchText + "%"});
        int idIndex = cursor.getColumnIndex("objectid");
        int nameIndex = cursor.getColumnIndex("nome_praia");
        int regionIndex = cursor.getColumnIndex("concelho_nome");
        int latIndex = cursor.getColumnIndex("latitude");
        int lonIndex = cursor.getColumnIndex("longitude");


        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(idIndex);
                String name = cursor.getString(nameIndex);
                String region = cursor.getString(regionIndex);
                float latitude = cursor.getFloat(latIndex);
                float longitude = cursor.getFloat(lonIndex);
                results.add(new ResultModel(id, name, region, latitude, longitude));
            } while (cursor.moveToNext());

        }
        else {
            Log.e("DatabaseHelper", "Colunas não encontradas na base de dados.");
        }
        cursor.close();
        return results;
    }

    public List<ResultModel> searchbyRegion(String searchText) {
        SQLiteDatabase db = openDatabase();
        List<ResultModel> results = new ArrayList<>();

        String query = "SELECT praias.*, concelho.nome FROM praias JOIN concelho ON praias.id_concelho = concelho.id WHERE concelho.nome LIKE ?";
        Cursor cursor = db.rawQuery(query, new String[]{"%" + searchText + "%"});
        int idIndex = cursor.getColumnIndex("objectid");
        int nameIndex = cursor.getColumnIndex("nome_praia");
        int regionIndex = cursor.getColumnIndex("concelho.nome");
        int latIndex = cursor.getColumnIndex("latitude");
        int lonIndex = cursor.getColumnIndex("longitude");

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(idIndex);
                String name = cursor.getString(nameIndex);
                String region = cursor.getString(regionIndex);
                float latitude = cursor.getFloat(latIndex);
                float longitude = cursor.getFloat(lonIndex);
                results.add(new ResultModel(id, name, region, latitude, longitude));
            } while (cursor.moveToNext());
        }
        else {
            Log.e("DatabaseHelper", "Colunas não encontradas na base de dados.");
        }
        cursor.close();
        return results;
    }


    public List<ResultModel> getAllBeaches() {
        List<ResultModel> beaches = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM praias JOIN concelho ON praias.id_concelho = concelho.id", null);
        int idIndex = cursor.getColumnIndex("objectid");
        int nameIndex = cursor.getColumnIndex("nome_praia");
        int regionIndex = cursor.getColumnIndex("concelho.nome");
        int latIndex = cursor.getColumnIndex("latitude");
        int lonIndex = cursor.getColumnIndex("longitude");

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(idIndex);
                String name = cursor.getString(nameIndex);
                String region = cursor.getString(regionIndex);
                float latitude = cursor.getFloat(latIndex);
                float longitude = cursor.getFloat(lonIndex);
                beaches.add(new ResultModel(id, name, region, latitude, longitude));
            } while (cursor.moveToNext());
        }
        else {
            Log.e("DatabaseHelper", "Colunas não encontradas na base de dados.");
        }
        cursor.close();
        db.close();
        return beaches;
    }

    public void addFavorite(int beachId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("beach_id", beachId);
        db.insert("favorites", null, values);
        db.close();
    }

    public void removeFavorite(int beachId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("favorites", "beach_id = ?", new String[]{String.valueOf(beachId)});
        db.close();
    }

    public boolean isFavorite(int beachId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT 1 FROM favorites WHERE beach_id = ?", new String[]{String.valueOf(beachId)});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }


    public List<ResultModel> getFavoriteBeaches() {
        List<ResultModel> favoriteBeaches = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT p.objectid, p.nome_praia, c.nome AS concelho, p.latitude, p.longitude " +
                "FROM favorites f " +
                "JOIN praias p ON f.beach_id = p.objectid " +
                "JOIN concelho c ON p.id_concelho = c.id";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("objectid"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("nome_praia"));
                String concelho = cursor.getString(cursor.getColumnIndexOrThrow("concelho"));
                float latitude = cursor.getFloat(cursor.getColumnIndexOrThrow("latitude"));
                float longitude = cursor.getFloat(cursor.getColumnIndexOrThrow("longitude"));

                favoriteBeaches.add(new ResultModel(id, name, concelho, latitude, longitude));
            } while (cursor.moveToNext());
        }
        else {
            Log.e("DatabaseHelper", "Colunas não encontradas na base de dados.");
        }

        cursor.close();
        db.close();
        return favoriteBeaches;
    }


}
