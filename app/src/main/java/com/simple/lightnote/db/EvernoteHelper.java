package com.simple.lightnote.db;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.asyncclient.EvernoteNoteStoreClient;
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
import com.evernote.thrift.TException;
import com.evernote.thrift.transport.TTransportException;
import com.simple.lightnote.constant.Constans;
import com.simple.lightnote.model.SimpleNote;
import com.simple.lightnote.util.SPUtil;
import com.simple.lightnote.utils.ListUtils;
import com.simple.lightnote.utils.LogUtils;

import org.greenrobot.greendao.query.LazyList;

import java.util.List;
import java.util.Map;

/**
 * Created by homelink on 2016/8/24.
 */
public class EvernoteHelper {
    public static final EvernoteNoteStoreClient noteStoreClient = EvernoteSession.getInstance().getEvernoteClientFactory().getNoteStoreClient();
    public static final int SYNC_START = 1;
    public static final int SYNC_END = 10;
    public static final int SYNC_ERROR = 100;
    public static final int SYNC_SUCCESS = 1000;
    private static final String TAG = "EverNoteHelper";
    private static final String NOTEBOOK_NAME = "LightNote";
    public static boolean SyncingUp = false;
    public static boolean SyncingDown = false;
    private Context context;
    private NoteDao noteDao;

    public EvernoteHelper(Context context, NoteDao dao) {
        this.context = context;
        this.noteDao = dao;
    }

    public boolean isNotebookExsist(String guid, String name) throws Exception {


        boolean result = false;
        try {
            Notebook notebook = noteStoreClient
                    .getNotebook(guid);
            if (notebook.getName().equals(name)) {
                result = true;
                LogUtils.e(TAG, guid + "笔记本存在");
                SPUtil.saveString(context, Constans.DEFAULT_NOTEBOOK_GUID, notebook.getGuid());

            }
        } catch (EDAMNotFoundException e) {
            e.printStackTrace();
            if (e.getIdentifier().equals("Notebook.guid")) {
                result = false;
                LogUtils.e(TAG, guid + "笔记本不存在");
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
            Notebook resultNotebook = noteStoreClient.createNotebook(notebook);
            result = true;
            LogUtils.e(TAG, "Notebook" + bookname + "不存在，创建成功");
            SPUtil.saveString(context, Constans.DEFAULT_NOTEBOOK_GUID, notebook.getGuid());


        } catch (EDAMUserException e) {
            if (e.getErrorCode() == EDAMErrorCode.DATA_CONFLICT) {
                result = true;
                LogUtils.e(TAG, "已经存在，无需创建");
            }
        } catch (Exception e) {
            LogUtils.e(TAG, "传输出现错误");
            throw e;
        }
        return result;
    }

    private Note createNote(SimpleNote simpleNote) throws Exception {
        try {
            Note note = simpleNote.toNote();
            note.setNotebookGuid(SPUtil.getString(context, Constans.DEFAULT_NOTEBOOK_GUID, null));
            Note responseNote = noteStoreClient.createNote(note);
            LogUtils.e(TAG, "Note创建成功");
          /*  ContentValues values = new ContentValues();
            values.put(MemoDB.ENID, responseNote.getGuid());
            values.put(MemoDB.SYNCSTATUS, Memo.NEED_NOTHING);
            values.put(MemoDB.UPDATEDTIME, responseNote.getUpdated());
            values.put(MemoDB.HASH, responseNote.getContentHash());
            mContentResolver.update(
                    ContentUris.withAppendedId(MemoProvider.MEMO_URI,
                            memo.getId()), values, null, null);*/
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
            LogUtils.e(TAG, "GUID是空，无需删除");
            return true;
        } else {
            try {
                noteStoreClient.deleteNote(note.getGuid());
                LogUtils.e(TAG, "Note删除成功");
                return true;
            } catch (EDAMUserException e) {
                LogUtils.e(TAG, "Note早已被删除，说明删除成功");
                return true;
            } catch (EDAMNotFoundException e) {
                LogUtils.e(TAG, "Note未找到，说明无需删除");
                return true;
            } catch (Exception e) {
                LogUtils.e(TAG, "传输失败，说明删除失败");
                return false;
            }
        }
    }

    private Note updateNote(SimpleNote simpleNote) throws Exception {

        try {
            Note responseNote = noteStoreClient.updateNote(simpleNote.toUpdateNote());
            noteDao.update(SimpleNote.toSimpleNote(responseNote));
            LogUtils.e(TAG, "Note更新成功");
            return responseNote;
        } catch (EDAMUserException e) {
            LogUtils.e(TAG, "数据格式有误");
            throw new Exception(e.getCause());
        } catch (EDAMNotFoundException e) {
            LogUtils.e(TAG, "Note根据GUID没有找到:" + e.getCause());
            throw new Exception("Note未找到");
        } catch (Exception e) {
            LogUtils.e(TAG, "传输出现错误:" + e.getCause());
            throw new Exception("传输出现错误:" + e.getCause());
        }
    }

    private void makeSureNotebookExsits(String NotebookName) throws Exception {
        try {
            if (SPUtil.getInstance(context).contains(Constans.DEFAULT_NOTEBOOK_GUID)) {
                if (!isNotebookExsist(SPUtil.getString(context, Constans.DEFAULT_NOTEBOOK_GUID, ""), NOTEBOOK_NAME)) {
                    createNotebook(NOTEBOOK_NAME);
                }
            } else {
                List<Notebook> books = noteStoreClient.listNotebooks();
                int count = books.size();
                for (int i = 0; i < count; i++) {
                    Notebook book = books.get(i);
                    if (book.getName().equals(NotebookName)) {
                        SPUtil.saveString(context, Constans.DEFAULT_NOTEBOOK_GUID, NotebookName);
                        return;
                    }
                }
                createNotebook(NOTEBOOK_NAME);
            }

        } catch (Exception e) {
            LogUtils.e(TAG, "检查笔记本是否存和创建笔记本的时候出现异常");
            throw e;
        }
    }

    private void downloadNote(String guid) {
        LogUtils.e(TAG, "准备添加:" + guid);
        try {
            Note note = noteStoreClient
                    .getNote(guid, true,
                            false, false, false);
            LogUtils.e(TAG, "获取到的文本：" + note.getContent());
            SimpleNote simpleNote = SimpleNote.toSimpleNote(note);
            noteDao.insert(simpleNote);
        } catch (TTransportException e) {
        } catch (EDAMUserException e) {
        } catch (EDAMSystemException e) {
        } catch (EDAMNotFoundException e) {
        } catch (TException e) {
        }
    }

    private void updateLocalNote(String guid, long _id) {
        LogUtils.e(TAG, "准备更新:" + guid);
        try {
            Note note = noteStoreClient
                    .getNote(guid, true,
                            false, false, false);
            SimpleNote simpleNote = SimpleNote.toSimpleNote(note);
            noteDao.update(simpleNote);
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

    private void syncDown() {
        if (SyncingDown) {
            return;
        }
        SyncingDown = true;
        NoteFilter noteFilter = new NoteFilter();

        String guid = SPUtil.getString(context, Constans.DEFAULT_NOTEBOOK_GUID, "");
        noteFilter.setNotebookGuid(guid);
        NotesMetadataResultSpec notesMetadataResultSpec = new NotesMetadataResultSpec();
        notesMetadataResultSpec.setIncludeUpdated(true);
        try {
            NoteCollectionCounts noteCollectionCounts =
                    noteStoreClient
                            .findNoteCounts(
                                    noteFilter, false);
            Map<String, Integer> maps = noteCollectionCounts
                    .getNotebookCounts();
            if (maps == null || maps.size() == 0)
                return;
            int maxcount = maps.get(guid);
            NotesMetadataList notesMetadata = noteStoreClient.findNotesMetadata(noteFilter, 0, maxcount, notesMetadataResultSpec);
            List<NoteMetadata> notes = notesMetadata.getNotes();
            for (NoteMetadata metadata : notes) {
                long updated = metadata.getUpdated();
                List<SimpleNote> list = noteDao.queryBuilder().where(NoteDao.Properties.guid.eq(metadata.getGuid())).list();


                if (!ListUtils.isEmpty(list)) {
                    SimpleNote simpleNote = list.get(0);

                    if (simpleNote.getUpdated() < metadata.getUpdated()) {
                        updateNote(simpleNote);
                    } else if (simpleNote.getUpdated() > metadata.getUpdated()) {
                        updateLocalNote(metadata.getGuid(), simpleNote.get_id());
                    }
                } else {
                    downloadNote(metadata.getGuid());
                }
            }

        } catch (TTransportException e) {
        } catch (EDAMUserException e) {
        } catch (EDAMSystemException e) {
        } catch (EDAMNotFoundException e) {
        } catch (TException e) {
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            SyncingDown = false;
        }
    }

    private void syncUp() throws Exception {
        if (SyncingUp) {
            LogUtils.e(TAG, "正在同步");
            return;
        }
        LogUtils.e(TAG, "开始同步");
        SyncingUp = true;
        LazyList<SimpleNote> simpleNotes = noteDao.queryBuilder().listLazy();

        for (SimpleNote simpleNote : simpleNotes) {
            if (simpleNote.getDeleted() > 0) {
                deleteNote(simpleNote.toDeleteNote());
            } else {
                if (simpleNote.isNeedSyncUp()) {
                    try {
                        updateNote(simpleNote);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    createNote(simpleNote);
                }

            }
        }

        SyncingUp = false;

    }

    public synchronized void sync(final boolean syncUp, final boolean syncDown,
                                  Handler handler) {
        if (handler != null) {
            handler.sendEmptyMessage(SYNC_START);
        }
        toSync(syncUp, syncDown, handler);
    }

    private synchronized void toSync(final boolean syncUp,
                                     final boolean syncDown, Handler handler) {
        new SyncTask(syncUp, syncDown, handler).execute();
    }

    class SyncTask extends AsyncTask<Void, Integer, Void> {

        Handler mHandler;
        boolean mSyncUp;
        boolean mSyncDown;

        public SyncTask(Boolean syncUp, Boolean syncDown, Handler handler) {
            this(syncUp, syncDown);
            mHandler = handler;
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
            if (EvernoteSession.getInstance().isLoggedIn() == false) {
                LogUtils.e(TAG, "未登录");
                publishProgress(new Integer[]{SYNC_ERROR});
                return null;
            }
            publishProgress(new Integer[]{SYNC_START});
            try {
                makeSureNotebookExsits(NOTEBOOK_NAME);
                if (mSyncUp)
                    syncUp();
                if (mSyncDown)
                    syncDown();
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
