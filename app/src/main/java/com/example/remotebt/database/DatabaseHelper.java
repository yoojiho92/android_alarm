package com.example.remotebt.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.remotebt.model.AlarmDAO;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "AlarmDatabase.db";
    private static final String TABLE_ALARMS = "alarms";
    private static final String COLUMN_ALARM_NO = "alarm_no";
    private static final String COLUMN_TIME = "time";
    private static final String COLUMN_USER = "user";
    private static final String COLUMN_MEALTIME = "mealtime";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // 데이터 베이스 생성 쿼리
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS alarms (" +
                "alarm_no INTEGER PRIMARY KEY AUTOINCREMENT," +
                "time TEXT NOT NULL," +
                "user TEXT NOT NULL," +
                "mealtime TEXT NOT NULL" +
                ");");
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 데이터베이스 업그레이드
    }

    // 알람 데이터 삽입 또는 업데이트 메서드
    public void insertOrUpdateAlarm(AlarmDAO alarm) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_TIME, alarm.getTime());
        values.put(COLUMN_USER, alarm.getUser());
        values.put(COLUMN_MEALTIME, alarm.getMealtime());

        // time 값을 기준으로 중복 확인
        Cursor cursor = db.query(TABLE_ALARMS, new String[]{COLUMN_ALARM_NO}, COLUMN_TIME + " = ?", new String[]{alarm.getTime()}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            // 중복되는 time 값이 있는 경우 업데이트
            @SuppressLint("Range") int alarmNo = cursor.getInt(cursor.getColumnIndex(COLUMN_ALARM_NO));
            db.update(TABLE_ALARMS, values, COLUMN_ALARM_NO + " = ?", new String[]{String.valueOf(alarmNo)});
        } else {
            // 중복되는 time 값이 없는 경우 삽입
            db.insert(TABLE_ALARMS, null, values);
        }

        if (cursor != null) {
            cursor.close();
        }
        db.close();
    }


    // 알람 데이터 삽입 메서드
    public void insertAlarm(AlarmDAO alarm) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_TIME, alarm.getTime());
        values.put(COLUMN_USER, alarm.getUser());
        values.put(COLUMN_MEALTIME, alarm.getMealtime());

        db.insert(TABLE_ALARMS, null, values);
        db.close();
    }

    // 모든 알람 데이터 조회 메서드
    public List<AlarmDAO> getAllAlarms() {
        List<AlarmDAO> alarmList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_ALARMS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") AlarmDAO alarm = new AlarmDAO(
                        cursor.getString(cursor.getColumnIndex(COLUMN_TIME)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_USER)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_MEALTIME))
                );
                alarmList.add(alarm);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return alarmList;
    }

    // 알람 데이터 삭제 메서드
    public void deleteAlarm(int alarmNo) {
        SQLiteDatabase db = this.getWritableDatabase();

        // alarm_no 기준으로 레코드 삭제
        db.delete(TABLE_ALARMS, COLUMN_ALARM_NO + " = ?", new String[] { String.valueOf(alarmNo) });

        db.close();
    }

    // 매개변수로 넘겨온 시간의 알람을 삭제하는 메소드
    public void deleteAlarm(String alarmTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        // TIME 기준으로 레코드 삭제
        db.delete(TABLE_ALARMS, COLUMN_TIME + " = ?", new String[] { String.valueOf(alarmTime) });

        db.close();
    }

    // 알람 테이블의 모든 데이터를 삭제하는 메소드
    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ALARMS, null, null);
        db.close();
    }
}
