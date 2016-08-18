package com.simple.lightnote.model;

import android.content.ContentValues;
import android.database.Cursor;

import com.evernote.client.android.EvernoteUtil;
import com.evernote.edam.type.Note;
import com.simple.lightnote.utils.LogUtils;

import java.io.Serializable;

public class Memo implements Serializable {
    public static final String STATUS_DELETE = "delete";
    public static final String STATUS_COMMON = "common";
    /**
     * need to do nothing
     */
    public static final int NEED_NOTHING = 0;
    /**
     * need to sync in Evernote
     */
    public static final int NEED_SYNC_UP = 1;
    /**
     * need to delete in Evernote
     */
    public static final int NEED_SYNC_DELETE = 3;
    /**
     * syning up
     */
    public static final int SYNCING_UP = 4;
    /**
     * syning down
     */
    public static final int SYNCING_DOWN = 5;
    private static final String TAG = "Memo";
    private static final long serialVersionUID = -1123549346312970903L;
    private static final String LogTag = "Memo";
    private long mCreatedTime;
    private long mUpdatedTime;
    private long mLastSyncTime;

    private int _id;

    private int mWallId;
    private int mOrder;
    private int mCursorPosition;
    private byte[] mHash;
    private int mSyncStatus = NEED_NOTHING;

    private String mGuid;
    private String mEnid;
    private String mContent;
    private String mAttributes;
    private String mStatus;

    public Memo() {
        mCreatedTime = System.currentTimeMillis();
        mUpdatedTime = System.currentTimeMillis();
        setContent("");
    }

    public Memo(Cursor cursor) {
        mHash = cursor.getBlob(cursor.getColumnIndex("hash"));
        mContent = cursor.getString(cursor.getColumnIndex("content"));
        mAttributes = cursor.getString(cursor.getColumnIndex("attributes"));
        mStatus = cursor.getString(cursor.getColumnIndex("status"));
        mGuid = cursor.getString(cursor.getColumnIndex("guid"));
        mEnid = cursor.getString(cursor.getColumnIndex("enid"));

        mLastSyncTime = cursor.getLong(cursor.getColumnIndex("lastsynctime"));
        mCreatedTime = cursor.getLong(cursor.getColumnIndex("createdtime"));
        mUpdatedTime = cursor.getLong(cursor.getColumnIndex("updatedtime"));

        _id = cursor.getInt(cursor.getColumnIndex("_id"));
        mWallId = cursor.getInt(cursor.getColumnIndex("wallid"));
        mOrder = cursor.getInt(cursor.getColumnIndex("orderid"));
        mCursorPosition = cursor
                .getInt(cursor.getColumnIndex("cursorposition"));
        mSyncStatus = cursor.getInt(cursor.getColumnIndex("syncstatus"));
    }

    public static Memo build(ContentValues values) {
        Memo memo = new Memo();
        if (values.containsKey("_id")) {
            memo._id = values.getAsInteger("_id");
        }
        memo.mGuid = values.getAsString("guid");
        memo.mEnid = values.getAsString("enid");
        memo.mWallId = values.getAsInteger("wallid");
        memo.mOrder = values.getAsInteger("orderid");
        memo.mSyncStatus = values.getAsInteger("syncstatus");
        memo.setContent(values.getAsString("content"));
        memo.mAttributes = values.getAsString("attributes");
        memo.mStatus = values.getAsString("status");
        memo.mCreatedTime = values.getAsLong("createdtime");
        memo.mUpdatedTime = values.getAsLong("updatedtime");
        memo.mLastSyncTime = values.getAsLong("lastsynctime");
        memo.mCursorPosition = values.getAsInteger("cursorposition");
        return memo;
    }

    public static Memo buildInsertMemoFromNote(Note note) {
        Memo memo = new Memo();
        memo.setContent(note.getContent());
        memo.setHash(note.getContentHash());
        memo.setUpdatedTime(note.getUpdated());
        memo.setCreatedTime(note.getCreated());
        memo.setEnid(note.getGuid());
        memo.setSyncStatus(NEED_NOTHING);
        memo.mStatus = STATUS_COMMON;
        memo.mCursorPosition = 0;
        return memo;
    }

    public static Memo buildUpdateMemoFromNote(Note note, int _id) {
        Memo memo = buildInsertMemoFromNote(note);
        memo.setId(_id);
        return memo;
    }

    public ContentValues toContentValues() {
        ContentValues values = toInsertContentValues();
        values.put("_id", _id);
        return values;
    }

    public ContentValues toInsertContentValues() {
        ContentValues values = new ContentValues();
        values.put("guid", mGuid);
        values.put("enid", mEnid);
        values.put("wallid", mWallId);
        values.put("orderid", mOrder);
        values.put("lastsynctime", mLastSyncTime);
        values.put("createdtime", mCreatedTime);
        values.put("updatedtime", mUpdatedTime);
        values.put("status", mStatus);
        values.put("attributes", mAttributes);
        values.put("content", mContent);
        values.put("hash", mHash);
        values.put("cursorposition", mCursorPosition);
        values.put("syncstatus", mSyncStatus);
        return values;
    }

    public ContentValues toUpdateContentValues() {
        ContentValues values = new ContentValues();
        values.put("guid", mGuid);
        values.put("enid", mEnid);
        values.put("wallid", mWallId);
        values.put("orderid", mOrder);
        values.put("lastsynctime", mLastSyncTime);
        values.put("createdtime", mCreatedTime);
        values.put("updatedtime", mUpdatedTime);
        values.put("status", mStatus);
        values.put("attributes", mAttributes);
        values.put("content", mContent);
        values.put("hash", mHash);
        values.put("cursorposition", mCursorPosition);
        values.put("syncstatus", mSyncStatus);
        return values;
    }

    public String getTitle() {
        return "EverMemo";
    }

    public Note toNote(String notebookGuid) {
        Note note = toNote();
        note.setNotebookGuid(notebookGuid);
        return note;
    }

    public Note toNote() {
        Note note = new Note();
        note.setTitle(getTitle());
        note.setContent(convertContentToEvernote());
        return note;
    }

    public Note toUpdateNote() {
        Note note = toNote();
        note.setGuid(mEnid);
        return note;
    }

    public Note toDeleteNote() {
        Note note = new Note();
        note.setGuid(mEnid);
        return note;
    }

    private String convertContentToEvernote() {
        String EvernoteContent = EvernoteUtil.NOTE_PREFIX
                + getContent().replace("<br>", "<br/>")
                + EvernoteUtil.NOTE_SUFFIX;
        LogUtils.e(LogTag, "同步文字:" + EvernoteContent);
        return EvernoteContent;
    }

    public long getUpdatedTime() {
        return mUpdatedTime;
    }

    public void setUpdatedTime(long updatedtime) {
        mUpdatedTime = updatedtime;
    }

    public long getCreatedTime() {
        return mCreatedTime;
    }

    public void setCreatedTime(long createdtime) {
        mCreatedTime = createdtime;
    }

    public long getLastSyncTime() {
        return mLastSyncTime;
    }

    public int getId() {
        return _id;
    }

    public void setId(int _id) {
        this._id = _id;
    }

    public int getOrder() {
        return mOrder;
    }

    public int getWallId() {
        return mWallId;
    }

    public int getCursorPosition() {
        return mCursorPosition;
    }

    public void setCursorPosition(int cursorPosition) {
        mCursorPosition = cursorPosition;
    }

    public int getSyncStatus() {
        return mSyncStatus;
    }

    private void setSyncStatus(int syncstatus) {
        mSyncStatus = syncstatus;
    }

    public String getEnid() {
        return mEnid;
    }

    public void setEnid(String enid) {
        mEnid = enid;
    }

    public String getGuid() {
        return mGuid;
    }

    public void setGuid(String guid) {
        mGuid = guid;
    }

    public byte[] getHash() {
        return mHash;
    }

    public void setHash(byte[] hash) {
        mHash = hash;
    }

    public String getAttributes() {
        return mAttributes;
    }

    public String getContent() {
        return mContent;
    }

    public Memo setContent(String content) {
        mContent = content;
        LogUtils.e(TAG, "下载下来的文本：" + content);
        mStatus = STATUS_COMMON;
        mOrder = 0;
        mAttributes = "";
        return this;
    }

    public String getStatus() {
        return mStatus;
    }

    public void setNeedSyncDelete() {
        setSyncStatus(NEED_SYNC_DELETE);
    }

    public void setNeedSyncUp() {
        setSyncStatus(NEED_SYNC_UP);
    }

    public boolean isNeedSyncUp() {
        if (mSyncStatus == NEED_SYNC_UP) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isSyncingUp() {
        if (mSyncStatus == SYNCING_UP) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isNeedSyncDelete() {
        if (mSyncStatus == NEED_SYNC_DELETE) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isDeleted() {
        if (mStatus.equals(STATUS_DELETE)) {
            return true;
        } else {
            return false;
        }
    }
}
