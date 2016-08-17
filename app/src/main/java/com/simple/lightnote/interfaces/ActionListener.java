package com.simple.lightnote.interfaces;

import com.simple.lightnote.model.SimpleNote;

public interface ActionListener {
    void onDelete(SimpleNote note);
    void onRecovery(SimpleNote note);
    void onAdd(SimpleNote note);
    void onModify(SimpleNote note);
    void onSelect();
}
