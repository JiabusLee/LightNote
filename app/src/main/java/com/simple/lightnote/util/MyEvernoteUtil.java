package com.simple.lightnote.util;

import android.content.Context;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.asyncclient.EvernoteNoteStoreClient;
import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.type.Note;
import com.evernote.thrift.TException;
import com.evernote.thrift.transport.TTransportException;
import com.simple.lightnote.LightNoteApplication;
import com.simple.lightnote.db.NoteDao;
import com.simple.lightnote.model.SimpleNote;

import java.util.List;

/**
 * Created by homelink on 2016/8/18.
 */
public class MyEvernoteUtil {

    private static EvernoteNoteStoreClient noteStoreClient = null;

    public MyEvernoteUtil() {
        noteStoreClient = EvernoteSession.getInstance().getEvernoteClientFactory().getNoteStoreClient();
    }

    public static void updateLocalNote(String guid, long _id, NoteDao dao) {
        try {
            Note note = noteStoreClient.getNote(guid, true, false, false, false);
            dao.update(SimpleNote.toSimpleNote(note));
        } catch (TTransportException e) {
            e.printStackTrace();
        } catch (EDAMUserException e) {
            e.printStackTrace();
        } catch (EDAMSystemException e) {
            e.printStackTrace();
        } catch (EDAMNotFoundException e) {
            e.printStackTrace();
        } catch (TException e) {
            e.printStackTrace();
        }
    }

    public List<SimpleNote> getAllNote(Context context) {
        LightNoteApplication app = (LightNoteApplication) context;
        List<SimpleNote> list = app.getDaoSession().getNoteDao().queryBuilder().build().list();
        return list;
    }

}
