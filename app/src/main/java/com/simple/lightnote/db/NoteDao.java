package com.simple.lightnote.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.simple.lightnote.model.Note;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

/**
 * Created by homelink on 2016/3/29.
 */
public class NoteDao extends AbstractDao<Note, Long> {
    public static final String TABLENAME = "note";
    public static class Properties {
        public final static Property Id = new Property(0, Integer.class, "id", true, "_id");
        public final static Property NoteTitle = new Property(1, String.class, "noteTitle", false, "noteTitle");
        public final static Property NoteContent = new Property(2, String.class, "noteContent", false, "noteContent");
        public final static Property NoteMd5 = new Property(3, String.class, "noteMd5", false, "noteMd5");
        public final static Property CreateTime = new Property(4, Long.class, "createTime", false, "createTime");
        public final static Property LastModifyTime = new Property(5, Long.class, "lastModifyTime", false, "lastModifyTime");
        public final static Property NoteType = new Property(6, int.class, "noteType", false, "noteType");

    }

    ;

    public NoteDao(DaoConfig config, AbstractDaoSession daoSession) {
        super(config, daoSession);
    }

    public NoteDao(DaoConfig config) {
        super(config);
    }

    /**
     * Creates the underlying database table.
     */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists ? "IF NOT EXISTS " : "";
        db.execSQL("CREATE TABLE note (\n" +
                "\t_id INTEGER PRIMARY KEY autoincrement,\n" +
                "\tnoteTitle VARCHAR (20),\n" +
                "\tnoteContent VARCHAR (500) NOT NULL,\n" +
                "\tnoteMd5 VARCHAR (20) NOT NULL,\n" +
                "\tcreateTime VARCHAR (32) NOT NULL,\n" +
                "\tlastModifyTime VARCHAR (32) NOT NULL,\n" +
                "\tnoteType VARCHAR (10)\n" +
                "); \n"// 1: noteType

        ); // 3: date
    }


    @Override
    public Note readEntity(Cursor cursor, int offset) {
        Note entity = new Note(
                cursor.isNull(offset + 0) ? null : cursor.getInt(offset + 0), // id
                cursor.isNull(offset + 1) ? null:cursor.getString(offset + 1), // title
                cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // contnet
                cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // md5
                cursor.isNull(offset+4)?null:Long.valueOf(cursor.getString(offset + 4)),//createTime
                cursor.isNull(offset+5)?null:Long.valueOf(cursor.getString(offset + 5)),//lastModifyTime
                cursor.isNull(offset+6)?null:cursor.getString(offset + 6)//noteType
                //TODO 添加构造函数
        );
        return entity;
    }

    @Override
    protected Long readKey(Cursor cursor, int offset) {
        return null;
    }

    @Override
    protected void readEntity(Cursor cursor, Note entity, int offset) {

    }

    @Override
    protected void bindValues(SQLiteStatement stmt, Note entity) {
        stmt.clearBindings();
        Integer id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getNoteTitle());

        String noteContent = entity.getNoteContent();
        if (noteContent != null) {
            stmt.bindString(3, noteContent);
        }

        stmt.bindString(4,entity.getNoteMd5());
        Long createTime = entity.getCreateTime();
        if(createTime!=null){
            stmt.bindLong(5,createTime);
        }else{
            stmt.bindLong(5,System.currentTimeMillis());
        }
        Long lastModifyTime = entity.getLastModifyTime();

        if(lastModifyTime!=null){
            stmt.bindLong(6,System.currentTimeMillis());
        }
        stmt.bindString(7,entity.getNoteType());


    }
    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"NOTE\"";
        db.execSQL(sql);
    }
    @Override
    protected Long updateKeyAfterInsert(Note entity, long rowId) {
        return null;
    }

    @Override
    protected Long getKey(Note entity) {
        return null;
    }

    @Override
    protected boolean isEntityUpdateable() {
        return false;
    }

    @Override
    public long insert(Note entity) {
        if(entity.getNoteTitle()==null){
            entity.setNoteTitle("");
        }
        return super.insert(entity);
    }

    @Override
    public void delete(Note entity) {
        super.delete(entity);
    }


    public void deleteByKey(Integer key) {
        super.deleteByKey(Long.valueOf(key));
    }
}
