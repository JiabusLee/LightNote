package com.simple.lightnote.model;

public class Note {
    private int id;
    private String noteTitle;
    private String noteContent;
    private String noteMd5;
    private long createTime;
    private long lastModifyTime;
    private String noteType;
    private int noteState;
    private String   noteLabel ;
    private String   book ;



    public Note() {
    }


    public Note(int id, String noteTitle, String noteContent, String noteMd5, long createTime, long lastModifyTime, String noteType, int noteState, String noteLabel, String book) {
        this.id = id;
        this.noteTitle = noteTitle;
        this.noteContent = noteContent;
        this.noteMd5 = noteMd5;
        this.createTime = createTime;
        this.lastModifyTime = lastModifyTime;
        this.noteType = noteType;
        this.noteState = noteState;
        this.noteLabel = noteLabel;
        this.book = book;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNoteTitle() {
        return noteTitle;
    }

    public void setNoteTitle(String noteTitle) {
        this.noteTitle = noteTitle;
    }

    public String getNoteContent() {
        return noteContent;
    }

    public void setNoteContent(String noteContent) {
        this.noteContent = noteContent;
    }

    public String getNoteMd5() {
        return noteMd5;
    }

    public void setNoteMd5(String noteMd5) {
        this.noteMd5 = noteMd5;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(long lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    public String getNoteType() {
        return noteType;
    }

    public void setNoteType(String noteType) {
        this.noteType = noteType;
    }

    public int getNoteState() {
        return noteState;
    }

    public void setNoteState(int noteState) {
        this.noteState = noteState;
    }

    public String getNoteLabel() {
        return noteLabel;
    }

    public void setNoteLabel(String noteLabel) {
        this.noteLabel = noteLabel;
    }

    public String getBook() {
        return book;
    }

    public void setBook(String book) {
        this.book = book;
    }

    @Override
    public String toString() {
        return "Note{" +
                "id=" + id +
                ", noteTitle='" + noteTitle + '\'' +
                ", noteContent='" + noteContent + '\'' +
                ", noteMd5='" + noteMd5 + '\'' +
                ", createTime=" + createTime +
                ", lastModifyTime=" + lastModifyTime +
                ", noteType='" + noteType + '\'' +
                ", noteState=" + noteState +
                ", noteLabel='" + noteLabel + '\'' +
                ", book='" + book + '\'' +
                '}';
    }
}
