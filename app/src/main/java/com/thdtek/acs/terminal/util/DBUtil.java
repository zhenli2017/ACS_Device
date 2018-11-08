package com.thdtek.acs.terminal.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.thdtek.acs.terminal.bean.AccessRecordBean;
import com.thdtek.acs.terminal.bean.PersonBean;
import com.thdtek.acs.terminal.dao.PersonDao;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.internal.DaoConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import greendao.AccessRecordBeanDao;
import greendao.ConfigBeanDao;
import greendao.DaoMaster;
import greendao.DaoSession;
import greendao.FaceFeatureHexBeanDao;
import greendao.NowPicFeatureHexBeanDao;
import greendao.PersonBeanDao;


/**
 * Time:2017/6/13
 * User:lizhen
 * Description:
 */

public class DBUtil {

    private DBUtil() {
    }

    private static DaoSession mDaoSession;

    public static void init(Context context, String dbNmae) {
        if (mDaoSession == null) {
            synchronized (DBUtil.class) {
                if (mDaoSession == null) {
                    SQLiteDatabase writableDatabase = new OwnDaoMaster.DbHelper(context, dbNmae).getWritableDatabase();
                    try {
                        mDaoSession = new DaoMaster(writableDatabase).newSession();
                    } catch (Exception e) {
                        Log.d("e", String.valueOf(e));
                    }
                }
            }
        }
    }

    public static DaoSession getDaoSession() {
        if (mDaoSession == null) {
            throw new RuntimeException("需要初始化DBUtil");
        }
        return mDaoSession;
    }

    public static boolean checkList(List list) {
        return list != null && list.size() > 0;
    }

    public static class OwnDaoMaster extends DaoMaster {
        public OwnDaoMaster(SQLiteDatabase db) {
            super(db);
        }

        public OwnDaoMaster(Database db) {
            super(db);
        }

        public static class DbHelper extends OpenHelper {
            public DbHelper(Context context, String name) {
                super(context, name);
            }

            public DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
                super(context, name, factory);
            }

            @Override
            public void onUpgrade(Database db, int oldVersion, int newVersion) {
                switch (oldVersion) {
                    case 7:
                        GreenDaoUpdateUtil.getInstance().generateTempTables(db, PersonBeanDao.class);
                    case 8:
                        GreenDaoUpdateUtil.getInstance().generateTempTables(db, ConfigBeanDao.class);
                    case 9:
                        GreenDaoUpdateUtil.getInstance().generateTempTables(db, FaceFeatureHexBeanDao.class);
                    case 10:
                    case 11:
                    case 12:
                    case 13:
                        NowPicFeatureHexBeanDao.createTable(db, true);
                    case 14:
                        FaceFeatureHexBeanDao.createTable(db, true);
                        NowPicFeatureHexBeanDao.createTable(db, true);
                    case 15:
                        GreenDaoUpdateUtil.getInstance().generateTempTables(db, ConfigBeanDao.class);
                    case 16:
                        GreenDaoUpdateUtil.getInstance().generateTempTables(db, PersonBeanDao.class);
                    case 17:
                        GreenDaoUpdateUtil.getInstance().generateTempTables(db, AccessRecordBeanDao.class);
                    case 18:
                        GreenDaoUpdateUtil.getInstance().generateTempTables(db, NowPicFeatureHexBeanDao.class);
                    case 19:
                        GreenDaoUpdateUtil.getInstance().generateTempTables(db, ConfigBeanDao.class);
                    case 20:
                        GreenDaoUpdateUtil.getInstance().generateTempTables(db, PersonBeanDao.class);
                    case 21:
                    case 22:
                        GreenDaoUpdateUtil.getInstance().generateTempTables(db, PersonBeanDao.class);
                    case 23:
                        GreenDaoUpdateUtil.getInstance().generateTempTables(db, AccessRecordBeanDao.class);
                    case 24:
                    case 25:
                        GreenDaoUpdateUtil.getInstance().generateTempTables(db,PersonBeanDao.class);
                    case 26:
                    case 27:
                        GreenDaoUpdateUtil.getInstance().generateTempTables(db,PersonBeanDao.class);
                    case 28:

                    default:
                        break;
                }
            }
        }
    }

    public static class GreenDaoUpdateUtil {

        private static final String CONVERSION_CLASS_NOT_FOUND_EXCEPTION = "MIGRATION HELPER - CLASS DOESN'T MATCH WITH THE CURRENT PARAMETERS";
        private static GreenDaoUpdateUtil instance;

        public static GreenDaoUpdateUtil getInstance() {
            if (instance == null) {
                instance = new GreenDaoUpdateUtil();
            }
            return instance;
        }

        public void migrate(Database db, Class<? extends AbstractDao<?, ?>>... daoClasses) {
            generateTempTables(db, daoClasses);
        }

        private void generateTempTables(Database db, Class<? extends AbstractDao<?, ?>>... daoClasses) {
            for (int i = 0; i < daoClasses.length; i++) {
                DaoConfig daoConfig = new DaoConfig(db, daoClasses[i]);
                String tableName = daoConfig.tablename;
                //通过表明获取当前表中的所有字段
                List<String> oldColumns = getColumns(db, tableName);

                //分隔符
                String divider = "";
                //创建新表
                StringBuilder tempTableStringBuilder = new StringBuilder();
                String tempTableName = daoConfig.tablename.concat("_TEMP");
                tempTableStringBuilder.append("CREATE TABLE ").append(tempTableName).append(" (");
                //插入数据时候的字段
                ArrayList<String> properties = new ArrayList<>();
                for (int j = 0; j < daoConfig.properties.length; j++) {
                    String columnName = daoConfig.properties[j].columnName;
                    String type = null;

                    try {
                        type = getTypeByClass(daoConfig.properties[j].type);
                    } catch (Exception exception) {
                    }

                    tempTableStringBuilder.append(divider).append(columnName).append(" ").append(type);

                    if (daoConfig.properties[j].primaryKey) {
                        tempTableStringBuilder.append(" PRIMARY KEY");
                    }

                    divider = ",";
                    if (oldColumns.contains(columnName)) {
                        properties.add(columnName);
                    }

                }
                tempTableStringBuilder.append(");");
                //执行创建临时表
                db.execSQL(tempTableStringBuilder.toString());

                //把旧表的数据插入到新表中
                StringBuilder insertTableStringBuilder = new StringBuilder();
                insertTableStringBuilder.append("INSERT INTO ").append(tempTableName).append(" (");
                insertTableStringBuilder.append(TextUtils.join(",", properties));
                insertTableStringBuilder.append(") SELECT ");
                insertTableStringBuilder.append(TextUtils.join(",", properties));
                insertTableStringBuilder.append(" FROM ").append(tableName).append(";");

                db.execSQL(insertTableStringBuilder.toString());

                //删除旧表
                db.execSQL("DROP TABLE " + tableName + ";");
                //新表改名
                db.execSQL("ALTER TABLE " + tempTableName + " RENAME TO " + tableName + ";");
            }
        }

        private String getTypeByClass(Class<?> type) throws Exception {
            if (type.equals(String.class)) {
                return "TEXT";
            }
            if (type.equals(Long.class) || type.equals(Integer.class) || type.equals(long.class)) {
                return "INTEGER";
            }
            if (type.equals(Boolean.class)) {
                return "BOOLEAN";
            }

            Exception exception = new Exception(CONVERSION_CLASS_NOT_FOUND_EXCEPTION.concat(" - Class: ").concat(type.toString()));
            throw exception;
        }

        private static List<String> getColumns(Database db, String tableName) {
            List<String> columns = new ArrayList<>();
            Cursor cursor = null;
            try {
                cursor = db.rawQuery("SELECT * FROM " + tableName + " limit 1", null);
                if (cursor != null) {
                    columns = new ArrayList<>(Arrays.asList(cursor.getColumnNames()));
                }
            } catch (Exception e) {
                Log.v(tableName, e.getMessage(), e);
                e.printStackTrace();
            } finally {
                if (cursor != null)
                    cursor.close();
            }
            return columns;
        }
    }


}
