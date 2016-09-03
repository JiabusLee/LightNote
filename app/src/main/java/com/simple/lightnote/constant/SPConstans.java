package com.simple.lightnote.constant;

import com.simple.lightnote.db.NoteDao;

/**
 * SP常量
 */
public interface SPConstans {
    String EDIT_TOOL_BAR = "EDIT_TOOLBAR";
    String ORDER_SORTBY="ORDER_SORTBY";
    String ORDER_SORTBY_DEFAULT= NoteDao.Properties.UpdateTime.columnName;
//    String ORDER_SORTBY_LASTMODIFYTIME="lastModifyTime";
//    String ORDER_SORTBY_CREATETIME="createTime";
//    String ORDER_SORTBY_NOTECONTENT="noteContent";

}
