package com.simple.lightnote.sync;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;

import com.evernote.client.android.EvernoteSession;
import com.evernote.edam.error.EDAMErrorCode;
import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.notestore.NoteCollectionCounts;
import com.evernote.edam.notestore.NoteFilter;
import com.evernote.edam.notestore.NoteMetadata;
import com.evernote.edam.notestore.NotesMetadataList;
import com.evernote.edam.notestore.NotesMetadataResultSpec;
import com.evernote.edam.type.Note;
import com.evernote.edam.type.Notebook;
import com.evernote.edam.type.User;
import com.evernote.thrift.TException;
import com.evernote.thrift.transport.TTransportException;
import com.simple.lightnote.LightNoteApplication;
import com.simple.lightnote.db.NoteDao;
import com.simple.lightnote.model.SimpleNote;
import com.simple.lightnote.util.MyEvernoteUtil;
import com.simple.lightnote.utils.ListUtils;
import com.simple.lightnote.utils.LogUtils;

import org.greenrobot.greendao.query.LazyList;

import java.util.List;
import java.util.Map;

public class Evernote {

    public static final String EVERNOTE_TOKEN = "Evernote_Token";
    public static final String EVERNOTE_TOKEN_TIME = "Evernote_Token_Time";
    public static final String EVERNOTE_USER_NAME = "Evernote_User_Name";
    public static final String EVERNOTE_USER_EMAIL = "Evernote_User_Email";
    public static final String EVERNOTE_NOTEBOOK_GUID = "Evenote_Note_Guid";
    public static final String LAST_SYNC_DOWN = "LAST_SYNC_DOWN";
    public static final int SYNC_START = 1;
    public static final int SYNC_END = 10;
    public static final int SYNC_ERROR = 100;
    public static final int SYNC_SUCCESS = 1000;
    private static final String CONSUMER_KEY = "milkliker";
    private static final String CONSUMER_SECRET = "f479109c186d284b";
    private static final String NOTEBOOK_NAME = "EverMemo";
    private static final EvernoteSession.EvernoteService EVERNOTE_SERVICE = EvernoteSession.EvernoteService.PRODUCTION;
    public static boolean SyncingUp = false;
    public static boolean SyncingDown = false;
    public String LogTag = "EverNote";
    public Context mContext;
    private EvernoteSession mEvernoteSession;
    private SharedPreferences mSharedPreferences;
    private ContentResolver mContentResolver;
    private EvernoteLoginCallback mEvernoteLoginCallback;


    private NoteDao dao;

    public Evernote(Context context) {
        mContext = context;
        mContentResolver = context.getContentResolver();
        mSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        mEvernoteSession = EvernoteSession.getInstance();
        dao = ((LightNoteApplication) context).getDaoSession().getNoteDao();
    }

    public Evernote(Context context, EvernoteLoginCallback l) {
        this(context);
        mEvernoteLoginCallback = l;
    }


    public boolean isLogin() {
        return mEvernoteSession.isLoggedIn();
    }

    public void onAuthFinish(int resultCode) {
        if (resultCode == Activity.RESULT_OK) {
            mSharedPreferences.edit()
                    .putString(EVERNOTE_TOKEN, mEvernoteSession.getAuthToken())
                    .putLong(EVERNOTE_TOKEN_TIME, System.currentTimeMillis())
                    .commit();
            if (mEvernoteLoginCallback != null) {
                mEvernoteLoginCallback.onLoginResult(true);
            }
            sync(true, true, null, dao);
        } else {
            if (mEvernoteLoginCallback != null) {
                mEvernoteLoginCallback.onLoginResult(false);
            }
        }
    }

    public String getUsername() {
        return mSharedPreferences.getString(EVERNOTE_USER_NAME, null);
    }

    public boolean isNotebookExsist(String guid, String name) throws Exception {
        boolean result = false;
        try {
            Notebook notebook = mEvernoteSession.getEvernoteClientFactory().getNoteStoreClient()
                    .getNotebook(guid);
            if (notebook.getName().equals(name)) {
                result = true;
                mSharedPreferences.edit()
                        .putString(EVERNOTE_NOTEBOOK_GUID, notebook.getGuid())
                        .commit();
            }
        } catch (EDAMNotFoundException e) {
            e.printStackTrace();
            if (e.getIdentifier().equals("Notebook.guid")) {
                result = false;
            }
        }
        return result;
    }

    /**
     * create a notebook by bookname
     *
     * @param bookname
     * @return
     * @throws Exception
     */
    public boolean createNotebook(String bookname) throws Exception {
        Notebook notebook = new Notebook();
        notebook.setDefaultNotebook(false);
        notebook.setName(bookname);
        boolean result = false;
        try {
            Notebook resultNotebook = mEvernoteSession.getEvernoteClientFactory().getNoteStoreClient()
                    .createNotebook(notebook);
            result = true;
            mSharedPreferences
                    .edit()
                    .putString(EVERNOTE_NOTEBOOK_GUID, resultNotebook.getGuid())
                    .commit();
        } catch (EDAMUserException e) {
            if (e.getErrorCode() == EDAMErrorCode.DATA_CONFLICT) {
                result = true;
            }
        } catch (Exception e) {
            throw e;
        }
        return result;
    }

    /**
     * 创建笔记
     *
     * @param simpleNote
     * @param dao
     * @return
     * @throws Exception
     */
    private Note createNote(SimpleNote simpleNote, NoteDao dao) throws Exception {
        try {
            Note note = simpleNote.toNote();
            note.setNotebookGuid(mSharedPreferences.getString(
                    EVERNOTE_NOTEBOOK_GUID, null));
            Note responseNote = mEvernoteSession.getEvernoteClientFactory().getNoteStoreClient()
                    .createNote(note);
            LogUtils.e(LogTag, "Note创建成功");
            dao.insert(simpleNote);
            return responseNote;
        } catch (EDAMUserException e) {
            throw new Exception("Note格式不合理");
        } catch (EDAMNotFoundException e) {
            throw new Exception("笔记本不存在");
        } catch (Exception e) {
            throw e;
        }
    }

    private boolean deleteNote(Note note) {
        if (note.getGuid() == null) {
            LogUtils.e(LogTag, "GUID是空，无需删除");
            return true;
        } else {
            try {
                mEvernoteSession
                        .getEvernoteClientFactory().getNoteStoreClient()
                        .deleteNote(
                                note.getGuid());
                LogUtils.e(LogTag, "Note删除成功");
                return true;
            } catch (EDAMUserException e) {
                LogUtils.e(LogTag, "Note早已被删除，说明删除成功");
                return true;
            } catch (EDAMNotFoundException e) {
                LogUtils.e(LogTag, "Note未找到，说明无需删除");
                return true;
            } catch (Exception e) {
                LogUtils.e(LogTag, "传输失败，说明删除失败");
                return false;
            }
        }
    }

    private Note updateNote(SimpleNote simpleNote, NoteDao dao) throws Exception {
        try {
            Note responseNote = mEvernoteSession
                    .getEvernoteClientFactory().getNoteStoreClient()
                    .updateNote(
                            simpleNote.toUpdateNote());
            dao.update(simpleNote);
            LogUtils.e(LogTag, "Note更新成功");
            return responseNote;
        } catch (EDAMUserException e) {
            LogUtils.e(LogTag, "数据格式有误");
            throw new Exception(e.getCause());
        } catch (EDAMNotFoundException e) {
            LogUtils.e(LogTag, "Note根据GUID没有找到:" + e.getCause());
            throw new Exception("Note未找到");
        } catch (Exception e) {
            LogUtils.e(LogTag, "传输出现错误:" + e.getCause());
            throw new Exception("传输出现错误:" + e.getCause());
        }
    }

    private void makeSureNotebookExsits(String NotebookName) throws Exception {
        try {
            if (mSharedPreferences.contains(EVERNOTE_NOTEBOOK_GUID)) {
                if (!isNotebookExsist(mSharedPreferences.getString(
                        EVERNOTE_NOTEBOOK_GUID, ""), NOTEBOOK_NAME)) {
                    createNotebook(NOTEBOOK_NAME);
                }
            } else {
                List<Notebook> books = mEvernoteSession.getEvernoteClientFactory().getNoteStoreClient()
                        .listNotebooks();

                int count = books.size();
                for (int i = 0; i < count; i++) {
                    Notebook book = books.get(i);
                    if (book.getName().equals(NotebookName)) {
                        mSharedPreferences
                                .edit()
                                .putString(EVERNOTE_NOTEBOOK_GUID,
                                        book.getGuid()).commit();
                        return;
                    }
                }
                createNotebook(NOTEBOOK_NAME);
            }

        } catch (Exception e) {
            throw e;
        }
    }

    private Note downloadNote(String guid) {
        try {
            Note note = mEvernoteSession
                    .getEvernoteClientFactory().getNoteStoreClient()
                    .getNote(guid, true,
                            false, false, false);
            return note;
        } catch (TTransportException e) {
        } catch (EDAMUserException e) {
        } catch (EDAMSystemException e) {
        } catch (EDAMNotFoundException e) {
        } catch (TException e) {
        }
        return null;
    }


    private void syncDown(NoteDao noteDao) {
        if (SyncingDown) {
            return;
        }
        SyncingDown = true;
        NoteFilter noteFilter = new NoteFilter();
        String guid = mSharedPreferences.getString(EVERNOTE_NOTEBOOK_GUID, "");
        noteFilter.setNotebookGuid(guid);
        NotesMetadataResultSpec notesMetadataResultSpec = new NotesMetadataResultSpec();
        notesMetadataResultSpec.setIncludeUpdated(true);
        try {
            NoteCollectionCounts noteCollectionCounts = mEvernoteSession
                    .getEvernoteClientFactory().getNoteStoreClient()
                    .findNoteCounts(
                            noteFilter, false);
            Map<String, Integer> maps = noteCollectionCounts.getNotebookCounts();
            if (maps == null || maps.size() == 0)
                return;
            int maxcount = maps.get(guid);
            NotesMetadataList list = mEvernoteSession
                    .getEvernoteClientFactory().getNoteStoreClient().findNotesMetadata(
                            noteFilter, 0, maxcount, notesMetadataResultSpec);

            for (int i = 0; i < list.getNotes().size(); i++) {
                NoteMetadata note = list.getNotes().get(i);
                List<SimpleNote> noteList = noteDao.queryBuilder().where(NoteDao.Properties.guid.eq(note.getGuid())).build().list();
                if (!ListUtils.isEmpty(noteList)) {
                    SimpleNote simpleNote = noteList.get(0);
                    if (note.getUpdated() != simpleNote.getUpdated()) {

                        MyEvernoteUtil.updateLocalNote(note.getGuid(), simpleNote.get_id(), noteDao);
                    }
                } else {
                    downloadNote(note.getGuid());
                }


            }

        } catch (TTransportException e) {
        } catch (EDAMUserException e) {
        } catch (EDAMSystemException e) {
        } catch (EDAMNotFoundException e) {
        } catch (TException e) {
        } finally {
            SyncingDown = false;
        }
    }

    private void syncUp(NoteDao dao) {
        if (SyncingUp) {
            LogUtils.e(LogTag, "正在同步");
            return;
        }
        LogUtils.e(LogTag, "开始同步");
        SyncingUp = true;

        LazyList<SimpleNote> simpleNotes = dao.queryBuilder().listLazy();
        for (int i = 0; i < simpleNotes.size(); i++) {
            SimpleNote simpleNote = simpleNotes.get(i);
            int status = simpleNote.getStatus();
            if (SimpleNote.st_delete == status) {
                if (deleteNote(simpleNote.toDeleteNote())) {
                    simpleNote.setActive(false);
                    dao.update(simpleNote);
                }
            } else if (simpleNote.st_update == status) {
                if (simpleNote.getGuid() != null) {
                    try {
                        updateNote(simpleNote, dao);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        createNote(simpleNote, dao);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }


        }

    }

    public synchronized void sync(final boolean syncUp, final boolean syncDown,
                                  Handler hanler, NoteDao dao) {
        if (hanler != null) {
            hanler.sendEmptyMessage(SYNC_START);
        }
        toSync(syncUp, syncDown, hanler, dao);
    }

    private synchronized void toSync(final boolean syncUp,
                                     final boolean syncDown, Handler handler, NoteDao dao) {
        new SyncTask(syncUp, syncDown, handler, dao).execute();
    }

    public interface EvernoteLoginCallback {
        public void onLoginResult(Boolean result);

        public void onUserinfo(Boolean result, User user);

        public void onLogout(Boolean reuslt);
    }

    class SyncTask extends AsyncTask<Void, Integer, Void> {

        Handler mHandler;
        boolean mSyncUp;
        boolean mSyncDown;
        NoteDao dao;

        public SyncTask(Boolean syncUp, Boolean syncDown, Handler handler, NoteDao dao) {
            this(syncUp, syncDown);
            mHandler = handler;
            this.dao = dao;
        }

        private SyncTask(Boolean syncUp, Boolean syncDown) {
            mSyncUp = syncUp;
            mSyncDown = syncDown;
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (mSyncUp == false && mSyncDown == false) {
                return null;
            }
            if (mEvernoteSession.isLoggedIn() == false) {
                LogUtils.e(LogTag, "未登录");
                publishProgress(new Integer[]{SYNC_ERROR});
                return null;
            }
            publishProgress(new Integer[]{SYNC_START});
            try {
                makeSureNotebookExsits(NOTEBOOK_NAME);
                if (mSyncUp)
                    syncUp(dao);
                if (mSyncDown)
                    syncDown(dao);
                publishProgress(new Integer[]{SYNC_SUCCESS});
            } catch (Exception e) {
                publishProgress(new Integer[]{SYNC_ERROR});
                return null;
            } finally {
                publishProgress(new Integer[]{SYNC_END});
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if (mHandler == null) {
                return;
            }
            switch (values[0]) {
                case SYNC_START:
                    mHandler.sendEmptyMessage(SYNC_START);
                    break;
                case SYNC_END:
                    mHandler.sendEmptyMessage(SYNC_END);
                    break;
                case SYNC_ERROR:
                    mHandler.sendEmptyMessage(SYNC_ERROR);
                    break;
                case SYNC_SUCCESS:
                    mHandler.sendEmptyMessage(SYNC_SUCCESS);
                    break;
                default:
                    break;
            }
        }
    }
}