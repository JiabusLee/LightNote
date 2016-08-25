package com.simple.lightnote.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.simple.lightnote.model.SimpleNote;
import com.simple.lightnote.utils.LogUtils;

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
                "\tnid VARCHAR(100) ,\n" +
                "\ttitle VARCHAR(100),\n" +
                "\t_guid VARCHAR(100),\n" +
                "\ttagNames VARCHAR(100),\n" +
                "\ttagGuids VARCHAR(100),\n" +
                "\tcontent VARCHAR(1000),\n" +
                "\tcreated Long,\n" +
                "\tupdated Long ,\n" +
                "\tdeleted Long,\n" +
                "\tstatus INTEGER default 0,\n" +
                "\tactive INTEGER DEFAULT 0,\n" +
                "\tnotebookGuid VARCHAR(50),\n" +
                "\tcontentHash VARCHAR(50) );"
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
    protected Long readKey(Cursor cursor, int offset) {
        LogUtils.e(TAG, "readKey: " + cursor);
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }



    @Override
    protected void bindValues(DatabaseStatement stmt, SimpleNote entity) {
        LogUtils.e(TAG, "bindValues: " + entity);
        stmt.clearBindings();

        Long id = entity.get_id();
        if (id != null) stmt.bindLong(1, id);

        String guid = entity.getGuid();
        if (guid != null) stmt.bindString(2, guid);

        String content = entity.getContent();
        if (content != null) stmt.bindString(3, content);

        String title = entity.getTitle();
        if (title != null) stmt.bindString(4, title);

        String contentHash = entity.getContentHash();
        if (contentHash != null) stmt.bindString(5, contentHash);

        Long created = entity.getCreated();
        if (created != null) stmt.bindLong(6, created);

        Long updated = entity.getUpdated();
        if (updated != null) stmt.bindLong(7, updated);

        Long deleted = entity.getDeleted();
        if (deleted != null) stmt.bindLong(8, deleted);

        String nid = entity.getNid();
        if (nid != null) stmt.bindString(9, nid);

        String notebookGuid = entity.getNotebookGuid();
        if (notebookGuid != null) stmt.bindString(10, notebookGuid);



    }


    @Override
    protected void readEntity(Cursor cursor, SimpleNote entity, int offset) {

        entity.set_id(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setGuid(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setContent(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setTitle(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setContentHash(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));

        entity.setCreated(cursor.isNull(offset + 5) ? null : cursor.getLong(offset + 5));
        entity.setUpdated(cursor.isNull(offset + 6) ? null : cursor.getLong(offset + 6));
        entity.setDeleted(cursor.isNull(offset + 7) ? null : cursor.getLong(offset + 7));

        entity.setNid(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setNotebookGuid(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));


    }

    @Override
    public SimpleNote readEntity(Cursor cursor, int offset) {
        SimpleNote entity = new SimpleNote();
        entity.set_id(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setGuid(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setContent(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setTitle(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setContentHash(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));

        entity.setCreated(cursor.isNull(offset + 5) ? null : cursor.getLong(offset + 5));
        entity.setUpdated(cursor.isNull(offset + 6) ? null : cursor.getLong(offset + 6));
        entity.setDeleted(cursor.isNull(offset + 7) ? null : cursor.getLong(offset + 7));

        entity.setNid(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setNotebookGuid(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));

        return entity;
    }
    @Override
    protected void bindValues(SQLiteStatement stmt, SimpleNote entity) {

        stmt.clearBindings();

        Long id = entity.get_id();
        if (id != null) stmt.bindLong(1, id);

        String guid = entity.getGuid();
        if (guid != null) stmt.bindString(2, guid);

        String content = entity.getContent();
        if (content != null) stmt.bindString(3, content);

        String title = entity.getTitle();
        if (title != null) stmt.bindString(4, title);

        String contentHash = entity.getContentHash();
        if (contentHash != null) stmt.bindString(5, contentHash);

        Long created = entity.getCreated();
        if (created != null) stmt.bindLong(6, created);

        Long updated = entity.getUpdated();
        if (updated != null) stmt.bindLong(7, updated);

        Long deleted = entity.getDeleted();
        if (deleted != null) stmt.bindLong(8, deleted);

        String nid = entity.getNid();
        if (nid != null) stmt.bindString(9, nid);

        String notebookGuid = entity.getNotebookGuid();
        if (notebookGuid != null) stmt.bindString(10, notebookGuid);

    }


    @Override
    protected Long updateKeyAfterInsert(SimpleNote entity, long rowId) {
        entity.set_id(rowId);
        return rowId;
    }


    @Override
    protected Long getKey(SimpleNote entity) {
        if (entity != null)
            return entity.get_id();
        else return null;
    }

    @Override
    protected boolean isEntityUpdateable() {
        return true;
    }





    @Override
    public long insert(SimpleNote entity) {
        if (entity != null) {
            long count = queryBuilder().where(Properties.guid.eq(entity.getGuid())).count();
            if (count == 0) {
                super.insert(entity);
            }
        }
        return -1;
    }

    public void insertList(List<SimpleNote> lists) {
        Observable.from(lists).observeOn(Schedulers.io()).subscribeOn(Schedulers.io()).map(note -> {
            Log.e(TAG, "insertList: " + note);
            return insert(note);
        }).subscribe(l -> Log.e(TAG, "insertList: save success" + l));
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


        public final static Property guid = new Property(8, String.class, "guid", false, "_guid");
        public final static Property tagGuids = new Property(9, String.class, "tagGuids", false, "tagGuids");
        public final static Property tagNames = new Property(10, String.class, "tagNames", false, "tagNames");

        public final static Property notebookGuid = new Property(11, String.class, "notebookGuid", false, "notebookGuid");
        public final static Property status = new Property(12, Integer.class, "status", false, "status");
        public final static Property nid = new Property(13, String.class, "nid", false, "nid");
    }


}
