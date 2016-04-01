package com.simple.lightnote.model;

public class Note {
    private Integer id;
    private String noteTitle;
    private String noteContent;
    private String noteMd5;
    private Long createTime;
    private Long lastModifyTime;
    private String noteType;

    public Note() {
    }

    public Note(Integer id, String noteTitle, String noteContent, String noteMd5, Long createTime, Long lastModifyTime, String noteType) {
        this.id = id;
        this.noteTitle = noteTitle;
        this.noteContent = noteContent;
        this.noteMd5 = noteMd5;
        this.createTime = createTime;
        this.lastModifyTime = lastModifyTime;
        this.noteType = noteType;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(Long lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    public String getNoteType() {
        return noteType;
    }

    public void setNoteType(String noteType) {
        this.noteType = noteType;
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
                "}\n";
    }
}
