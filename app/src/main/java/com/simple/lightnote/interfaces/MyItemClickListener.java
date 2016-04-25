package com.simple.lightnote.interfaces;

import com.simple.lightnote.model.Note;

public interface MyItemClickListener {
    void onDelete(Note note);
    void onRecovery(Note note);
    void onAdd(Note note);
    void onModify(Note note);
    void onSelect();
}
