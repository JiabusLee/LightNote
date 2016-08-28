package com.simple.lightnote.model;

import android.support.annotation.NonNull;

import com.evernote.client.android.EvernoteUtil;
import com.evernote.edam.type.Note;

import java.util.List;


public class SimpleNote {

    public static final int st_noting = 0;//没有修改
    public static final int st_delete = 1;//要删除
    public static final int st_sync = 2;//要同步
    public static final int st_syncing_up = 3;//正在同步

    public static final int ac_live = 0;
    public static final int ac_die = 1;


    private Long _id;

    private String guid;
    private String title;
    private String content;
    private String notebookGuid;
    private String nid;

    private long created;
    private long updated;
    private long deleted;

    private int active = ac_live;
    private int status = st_noting;

    private int contentLength;
    private String contentHash;

    private List<String> tagGuids;
    private List<String> tagNames;




    public SimpleNote() {
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    @NonNull
    public static SimpleNote toSimpleNote(Note note) {
        SimpleNote simpleNote;
        simpleNote = new SimpleNote();
        return simple(note, simpleNote);
    }

    public static SimpleNote simple(Note note, SimpleNote simpleNote) {
        simpleNote.set_id(simpleNote.get_id());
        simpleNote.setContent(note.getContent());
        simpleNote.setActive(note.isSetActive() ? ac_live : ac_die);
        simpleNote.setGuid(note.getGuid());
        simpleNote.setNid(note.getGuid());
        simpleNote.setContentHash(EvernoteUtil.bytesToHex(note.getContentHash()));
        simpleNote.setUpdated(note.getUpdated());
        simpleNote.setCreated(note.getCreated());
        simpleNote.setDeleted(note.getDeleted());
        simpleNote.setNotebookGuid(note.getNotebookGuid());
        simpleNote.setTitle(note.getTitle());
        simpleNote.setTagNames(note.getTagNames());
        simpleNote.setContentLength(note.getContentLength());
        simpleNote.setTagGuids(note.getTagGuids());
        simpleNote.setStatus(st_noting);
        return simpleNote;
    }

    @Override
    public String toString() {
        return "SimpleNote{" +
                "_id=" + _id +
                ", nid='" + nid + '\'' +
                ", guid='" + guid + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", contentHash='" + contentHash + '\'' +
                ", contentLength=" + contentLength +
                ", created=" + created +
                ", updated=" + updated +
                ", deleted=" + deleted +
                ", active=" + active +
                ", notebookGuid='" + notebookGuid + '\'' +
                ", tagGuids=" + tagGuids +
                ", tagNames=" + tagNames +
                ", status=" + status +
                '}';
    }

    public Long get_id() {
        return _id;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContentHash() {
        return contentHash;
    }

    public void setContentHash(String contentHash) {
        this.contentHash = contentHash;
    }

    public int getContentLength() {
        return contentLength;
    }

    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public long getUpdated() {
        return updated;
    }

    public void setUpdated(long updated) {
        this.updated = updated;
    }

    public long getDeleted() {
        return deleted;
    }

    public void setDeleted(long deleted) {
        this.deleted = deleted;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public String getNotebookGuid() {
        return notebookGuid;
    }

    public void setNotebookGuid(String notebookGuid) {
        this.notebookGuid = notebookGuid;
    }

    public List<String> getTagGuids() {
        return tagGuids;
    }

    public void setTagGuids(List<String> tagGuids) {
        this.tagGuids = tagGuids;
    }

    public List<String> getTagNames() {
        return tagNames;
    }

    public void setTagNames(List<String> tagNames) {
        this.tagNames = tagNames;
    }

    public Note toNote() {
        Note note = new Note();
        note.setContent(this.getContent());
        note.setTitle(this.getTitle());
        return note;
    }

    public Note toUpdateNote() {
        Note note = toNote();
        note.setGuid(this.getGuid());
        note.setContent(this.getContent());
        note.setTitle(this.getTitle());
        return note;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Note toDeleteNote() {
        Note note = new Note();
        note.setGuid(this.getGuid());
        return note;
    }

    public String getNid() {
        return nid;
    }

    public void setNid(String nid) {
        this.nid = nid;
    }

    public boolean isNeedSyncUp() {
        if (status == st_sync) {
            return true;
        } else {
            return false;
        }
    }
}

