package com.simple.lightnote.interfaces;

import com.evernote.edam.type.Note;

public interface ActionListener {
    void onDelete(Note note);
    void onRecovery(Note note);
    void onAdd(Note note);
    void onModify(Note note);
    void onSelect();
}
