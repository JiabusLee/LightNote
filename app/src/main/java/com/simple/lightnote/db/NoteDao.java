package com.simple.lightnote.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.evernote.edam.type.Note;
import com.simple.lightnote.model.SimpleNote;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;
import org.greenrobot.greendao.internal.DaoConfig;

import java.util.List;

import rx.Observable;
import rx.schedulers.Schedulers;


/**
 * Created by homelink on 2016/3/29.
 */
public class NoteDao extends AbstractDao<SimpleNote, Long> {
    public static final String TABLENAME = "note";
    private static final String TAG = "NoteDao";

    public NoteDao(DaoConfig config, AbstractDaoSession daoSession) {
        super(config, daoSession);
    }

    public NoteDao(DaoConfig config) {
        super(config);
    }

    /**
     * Creates the underlying database table.
     */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists ? "IF NOT EXISTS " : "";
        db.execSQL("CREATE TABLE note (\n" +
                "\t_id INTEGER PRIMARY KEY autoincrement,\n" +
                "\ttitle VARCHAR(100),\n" +
                "\tguid VARCHAR(40),\n" +
                "\ttagNames VARCHAR(40),\n" +
                "\ttagGuids VARCHAR(40),\n" +
                "\tcontent VARCHAR(1000),\n" +
                "\tcreated Long,\n" +
                "\tupdated Long ,\n" +
                "\tdeleted Long,\n" +
                "\tstatus INTEGER default 0,\n" +
                "\tactive INTEGER DEFAULT 0,\n" +
                "\tnotebookGuid VARCHAR(40),\n" +
                "\tcontentHash VARCHAR(40) );"
        );
    }

    /**
     * Drops the underlying database table.
     */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"NOTE\"";
        db.execSQL(sql);
    }

    @Override
    public SimpleNote readEntity(Cursor cursor, int offset) {
        SimpleNote entity = new SimpleNote();
        int guid = cursor.getColumnIndex("guid");
        String string = cursor.getString(guid);
        entity.setGuid(string);
        int title = cursor.getColumnIndex("title");
        String title1 = cursor.getString(title);
        entity.setTitle(title1);

        int content = cursor.getColumnIndex("content");
        String content1 = cursor.getString(content);
        entity.setContent(content1);


        int created = cursor.getColumnIndex("created");
        long created1 = cursor.getLong(created);
        entity.setCreated(created1);

        int updated = cursor.getColumnIndex("updated");
        long updated1 = cursor.getLong(updated);
        entity.setCreated(updated1);

        return entity;
    }

    @Override
    protected Long readKey(Cursor cursor, int offset) {
        return null;
    }

    @Override
    protected void readEntity(Cursor cursor, SimpleNote entity, int offset) {
        entity = readEntity(cursor, offset);
    }

    @Override
    protected void bindValues(DatabaseStatement stmt, SimpleNote entity) {

    }

    @Override
    protected void bindValues(SQLiteStatement stmt, SimpleNote entity) {

    }

    @Override
    public void update(SimpleNote entity) {


        super.update(entity);
    }

    public void update(Note entity) {
        update(new SimpleNote());
    }

    @Override
    protected Long updateKeyAfterInsert(SimpleNote entity, long rowId) {
        entity.set_id(rowId);
        return rowId;
    }

    @Override
    protected Long getKey(SimpleNote entity) {
        return entity.get_id();
    }

    @Override
    protected boolean isEntityUpdateable() {
        return true;
    }

    @Override
    public long insert(SimpleNote entity) {
        return super.insert(entity);
    }


    public void insertAll(List<SimpleNote> lists) {
        Observable.from(lists).observeOn(Schedulers.io()).subscribeOn(Schedulers.io()).map(note -> {
            Log.e(TAG, "insertAll: " + note);
            return insert(note);
        }).subscribe(l -> Log.e(TAG, "insertAll: save success" + l));
/*
        for (SimpleNote note:lists)
            insert(note);
*/
    }


    @Override
    public void delete(SimpleNote entity) {
        super.delete(entity);
    }

    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "_id", true, "_id");

        public final static Property title = new Property(1, String.class, "title", false, "title");
        public final static Property content = new Property(2, String.class, "content", false, "content");
        public final static Property contentHash = new Property(3, String.class, "contentHash", false, "contentHash");
        public final static Property active = new Property(4, Integer.class, "active", false, "active");

        public final static Property createTime = new Property(5, Long.class, "created", false, "created");
        public final static Property updateTime = new Property(6, Long.class, "updated", false, "updated");
        public final static Property deleteTime = new Property(7, Long.class, "deleted", false, "deleted");


        public final static Property guid = new Property(8, String.class, "guid", false, "guid");
        public final static Property tagGuids = new Property(9, String.class, "tagGuids", false, "tagGuids");
        public final static Property tagNames = new Property(10, String.class, "tagNames", false, "tagNames");

        public final static Property notebookGuid = new Property(11, String.class, "notebookGuid", false, "notebookGuid");
        public final static Property status = new Property(12, Integer.class, "status", false, "status");

    }


}
