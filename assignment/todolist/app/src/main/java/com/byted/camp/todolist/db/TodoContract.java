package com.byted.camp.todolist.db;

import android.provider.BaseColumns;

/**
 * Created on 2019/1/22.
 *
 * @author xuyingyi@bytedance.com (Yingyi Xu)
 */
public final class TodoContract {

    // TODO 定义表结构和 SQL 语句常量

    private TodoContract() {}
    public static class Todo implements BaseColumns{
        public static final String TABLE_NAME = "note";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_CONTENT = "content";
        public static final String COLUMN_NAME_STATE = "state";
    }

}
