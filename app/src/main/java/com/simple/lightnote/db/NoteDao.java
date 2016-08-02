package com.simple.lightnote.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import com.evernote.edam.type.Note;
import com.evernote.edam.type.NoteAttributes;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;
import org.greenrobot.greendao.internal.DaoConfig;


/**
 * Created by homelink on 2016/3/29.
 */
public class NoteDao extends AbstractDao<Note, String> {
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
                "\tnoteTitle VARCHAR (20),\n" +
                "\tnoteContent VARCHAR (500) NOT NULL,\n" +
                "\tnoteMd5 VARCHAR (32) NOT NULL,\n" +
                "\tcreateTime Long NOT NULL,\n" +
                "\tlastModifyTime Long NOT NULL,\n" +
                "\tnoteType VARCHAR (10) DEFAULT 'normal' ,\n" +
                "\tnoteState INTEGER DEFAULT 0,\n" +
                "\tnoteLabel varchar(20),\n" +
                "\tbook varchar(20) DEFAULT 'normal'); "
        ); // 3: date
    }

    /**
     * Drops the underlying database table.
     */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"NOTE\"";
        db.execSQL(sql);
    }

    @Override
    public Note readEntity(Cursor cursor, int offset) {
        Note entity = new Note();
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
    protected String readKey(Cursor cursor, int offset) {
        return null;
    }


    @Override
    protected void readEntity(Cursor cursor, Note entity, int offset) {
        entity = readEntity(cursor, offset);
    }

    @Override
    protected void bindValues(DatabaseStatement stmt, Note entity) {

    }

    @Override
    protected void bindValues(SQLiteStatement stmt, Note entity) {
        stmt.clearBindings();
        NoteAttributes attributes = entity.getAttributes();
        int creatorId = attributes.getCreatorId();
        // TODO: 2016/7/28 绑定Value
        /*if()
        Long id = (Long) attributes;
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getTitle());

        String noteContent = entity.getContent();
        if (noteContent != null) {
            stmt.bindString(3, noteContent);
        }

        stmt.bindString(4, entity.getNoteMd5());
        Long createTime = entity.getCreated();
        if (createTime != null) {
            stmt.bindLong(5, createTime);
        } else {
            stmt.bindLong(5, System.currentTimeMillis());
        }
        Long lastModifyTime = entity.getUpdated();

        if (lastModifyTime != null) {
            stmt.bindLong(6, System.currentTimeMillis());
        }
        stmt.bindString(7, entity.getNoteType());
        stmt.bindLong(8, entity.getNoteState());
*/

    }

    @Override
    protected String updateKeyAfterInsert(Note entity, long rowId) {
        return null;
    }

    @Override
    public void update(Note entity) {
        if (entity.getTitle() == null)
            entity.setTitle("");
        System.out.println("update :" + entity);
        super.update(entity);
    }

    @Override
    protected String getKey(Note entity) {
        return entity.getGuid();
    }

    @Override
    protected boolean isEntityUpdateable() {
        return true;
    }

    @Override
    public long insert(Note entity) {
        if (entity.getTitle() == null) {
            entity.setTitle("");
        }
        return super.insert(entity);
    }

    @Override
    public void delete(Note entity) {
        super.delete(entity);
    }

    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property NoteTitle = new Property(1, String.class, "noteTitle", false, "noteTitle");
        public final static Property NoteContent = new Property(2, String.class, "noteContent", false, "noteContent");
        public final static Property NoteMd5 = new Property(3, String.class, "noteMd5", false, "noteMd5");
        public final static Property CreateTime = new Property(4, Long.class, "createTime", false, "createTime");
        public final static Property LastModifyTime = new Property(5, Long.class, "lastModifyTime", false, "lastModifyTime");
        public final static Property NoteType = new Property(6, String.class, "noteType", false, "noteType");
        public final static Property NoteState = new Property(7, Integer.class, "noteState", false, "noteState");
        public final static Property NoteLabel = new Property(8, String.class, "noteLabel", false, "noteLabel");
        public final static Property NoteBook = new Property(9, String.class, "book", false, "book");

    }

}
