package com.simple.lightnote.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;
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
        db.execSQL("CREATE TABLE note (\n" +
                "\t_id INTEGER PRIMARY KEY autoincrement,\n" +
                "\t_guid VARCHAR(100),\n" +
                "\t_title VARCHAR(100),\n" +
                "\t_content VARCHAR(1000),\n" +
                "\t_notebookGuid VARCHAR(50),\n" +
                "\t_nid VARCHAR(100) ,\n" +
                "\tcreated Long,\n" +
                "\tupdated Long ,\n" +
                "\tdeleted Long,\n" +
                "\t_status INTEGER default 0,\n" +
                "\t_active INTEGER DEFAULT 0,\n" +
                "\tcontentLength Long,\n" +
                "\t_contentHash VARCHAR(50)," +
                "\t_tagNames VARCHAR(100),\n" +
                "\t_tagGuids VARCHAR(100));\n"
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

        String title = entity.getTitle();
        if (title != null) stmt.bindString(3, title);
        String content = entity.getContent();
        if (content != null) stmt.bindString(4, content);

        String notebookGuid = entity.getNotebookGuid();
        if (notebookGuid != null) stmt.bindString(5, notebookGuid);

        String nid = entity.getNid();
        if (nid != null) stmt.bindString(6, nid);


        Long created = entity.getCreated();
        if (created != null) stmt.bindLong(7, created);

        Long updated = entity.getUpdated();
        if (updated != null) stmt.bindLong(8, updated);

        Long deleted = entity.getDeleted();
        if (deleted != null) stmt.bindLong(9, deleted);

        stmt.bindLong(10, entity.getStatus());
        stmt.bindLong(11, entity.getActive());


        stmt.bindLong(12, entity.getContentLength());
        String contentHash = entity.getContentHash();
        if (contentHash != null) stmt.bindString(13, contentHash);

//
//        List<String> tagGuids = entity.getTagGuids();
//        if (tagGuids != null) stmt.bindString(14, tagGuids.toString());
//
//        List<String> tagNames = entity.getTagNames();
//        if (tagNames != null) stmt.bindString(15, tagNames.toString());


    }


    @Override
    protected void readEntity(Cursor cursor, SimpleNote entity, int offset) {

        entity.set_id(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));

        entity.setGuid(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setTitle(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setContent(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setNotebookGuid(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setNid(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));

        entity.setCreated(cursor.isNull(offset + 6) ? null : cursor.getLong(offset + 6));
        entity.setUpdated(cursor.isNull(offset + 7) ? null : cursor.getLong(offset + 7));
        entity.setDeleted(cursor.isNull(offset + 8) ? null : cursor.getLong(offset + 8));

        entity.setStatus(cursor.isNull(offset + 9) ? null : cursor.getInt(offset + 9));
        entity.setActive(cursor.isNull(offset + 10) ? null : cursor.getInt(offset + 10));

        entity.setContentLength(cursor.isNull(offset + 11) ? null : cursor.getInt(offset + 11));
        entity.setContentHash(cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12));

//        entity.setTagGuids(cursor.isNull(offset+13)?null:cursor.getString(offset+13));

    }

    @Override
    public SimpleNote readEntity(Cursor cursor, int offset) {
        SimpleNote entity = new SimpleNote();
        entity.set_id(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));

        entity.setGuid(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setTitle(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setContent(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setNotebookGuid(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setNid(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));

        entity.setCreated(cursor.isNull(offset + 6) ? null : cursor.getLong(offset + 6));
        entity.setUpdated(cursor.isNull(offset + 7) ? null : cursor.getLong(offset + 7));
        entity.setDeleted(cursor.isNull(offset + 8) ? null : cursor.getLong(offset + 8));

        entity.setStatus(cursor.isNull(offset + 9) ? null : cursor.getInt(offset + 9));
        entity.setActive(cursor.isNull(offset + 10) ? null : cursor.getInt(offset + 10));

        entity.setContentLength(cursor.isNull(offset + 11) ? null : cursor.getInt(offset + 11));
        entity.setContentHash(cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12));
        return entity;
    }

    @Override
    protected void bindValues(SQLiteStatement stmt, SimpleNote entity) {

        stmt.clearBindings();

        Long id = entity.get_id();
        if (id != null) stmt.bindLong(1, id);

        String guid = entity.getGuid();
        if (guid != null) stmt.bindString(2, guid);


        String title = entity.getTitle();
        if (title != null) stmt.bindString(3, title);
        String content = entity.getContent();
        if (content != null) stmt.bindString(4, content);
        String notebookGuid = entity.getNotebookGuid();
        if (notebookGuid != null) stmt.bindString(5, notebookGuid);

        String nid = entity.getNid();
        if (nid != null) stmt.bindString(6, nid);


        Long created = entity.getCreated();
        if (created != null) stmt.bindLong(7, created);

        Long updated = entity.getUpdated();
        if (updated != null) stmt.bindLong(8, updated);

        Long deleted = entity.getDeleted();
        if (deleted != null) stmt.bindLong(9, deleted);

        int status = entity.getStatus();
        stmt.bindLong(10, status);
        int active = entity.getActive();
        stmt.bindLong(11, active);

        int contentLength = entity.getContentLength();
        stmt.bindLong(12, contentLength);
        String contentHash = entity.getContentHash();
        if (contentHash != null) stmt.bindString(13, contentHash);


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
            String guid = entity.getGuid();
            if (!TextUtils.isEmpty(guid)) {
                long count = queryBuilder().where(Properties.Guid.eq(entity.getGuid())).count();
                if (count == 0) {
                    return super.insert(entity);
                }
            } else {
                return super.insert(entity);
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

        public final static Property Guid = new Property(1, String.class, "guid", false, "_guid");
        public final static Property Title = new Property(2, String.class, "title", false, "_title");
        public final static Property Content = new Property(3, String.class, "content", false, "_content");
        public final static Property NotebookGuid = new Property(4, String.class, "notebookGuid", false, "_notebookGuid");
        public final static Property Nid = new Property(5, String.class, "nid", false, "_nid");

        public final static Property CreateTime = new Property(6, Long.class, "created", false, "created");
        public final static Property UpdateTime = new Property(7, Long.class, "updated", false, "updated");
        public final static Property DeleteTime = new Property(8, Long.class, "deleted", false, "deleted");


        public final static Property Status = new Property(9, Integer.class, "status", false, "_status");
        public final static Property Active = new Property(10, Integer.class, "active", false, "_active");

        public final static Property ContentLength = new Property(11, Integer.class, "contentLength", false, "contentLength");
        public final static Property ContentHash = new Property(12, String.class, "contentHash", false, "_contentHash");

        public final static Property TagGuids = new Property(13, String.class, "tagGuids", false, "_tagGuids");
        public final static Property TagNames = new Property(14, String.class, "tagNames", false, "_tagNames");

    }


}
