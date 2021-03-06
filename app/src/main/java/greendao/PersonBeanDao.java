package greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.thdtek.acs.terminal.bean.PersonBean;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "PERSON_BEAN".
*/
public class PersonBeanDao extends AbstractDao<PersonBean, Long> {

    public static final String TABLENAME = "PERSON_BEAN";

    /**
     * Properties of entity PersonBean.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Person_id = new Property(1, long.class, "person_id", false, "PERSON_ID");
        public final static Property Person_ts = new Property(2, double.class, "person_ts", false, "PERSON_TS");
        public final static Property Auth_id = new Property(3, long.class, "auth_id", false, "AUTH_ID");
        public final static Property Auth_ts = new Property(4, double.class, "auth_ts", false, "AUTH_TS");
        public final static Property FacePic = new Property(5, String.class, "facePic", false, "FACE_PIC");
        public final static Property OldFacePic = new Property(6, String.class, "oldFacePic", false, "OLD_FACE_PIC");
        public final static Property Name = new Property(7, String.class, "name", false, "NAME");
        public final static Property ID_no = new Property(8, String.class, "iD_no", false, "I_D_NO");
        public final static Property Employee_card_id = new Property(9, String.class, "employee_card_id", false, "EMPLOYEE_CARD_ID");
        public final static Property Start_ts = new Property(10, double.class, "start_ts", false, "START_TS");
        public final static Property End_ts = new Property(11, double.class, "end_ts", false, "END_TS");
        public final static Property Count = new Property(12, long.class, "count", false, "COUNT");
        public final static Property Now_pic = new Property(13, String.class, "now_pic", false, "NOW_PIC");
        public final static Property Fid = new Property(14, String.class, "fid", false, "FID");
        public final static Property PersonalizedPermissions = new Property(15, String.class, "personalizedPermissions", false, "PERSONALIZED_PERMISSIONS");
        public final static Property Weekly = new Property(16, String.class, "weekly", false, "WEEKLY");
        public final static Property Department = new Property(17, String.class, "department", false, "DEPARTMENT");
        public final static Property Position = new Property(18, String.class, "position", false, "POSITION");
        public final static Property TermOfValidity = new Property(19, String.class, "termOfValidity", false, "TERM_OF_VALIDITY");
        public final static Property CardStatus = new Property(20, String.class, "cardStatus", false, "CARD_STATUS");
        public final static Property PersonNumber = new Property(21, String.class, "personNumber", false, "PERSON_NUMBER");
        public final static Property PicNumber = new Property(22, String.class, "picNumber", false, "PIC_NUMBER");
        public final static Property Name_yingze = new Property(23, String.class, "name_yingze", false, "NAME_YINGZE");
        public final static Property IcNoHex = new Property(24, String.class, "icNoHex", false, "IC_NO_HEX");
    };


    public PersonBeanDao(DaoConfig config) {
        super(config);
    }
    
    public PersonBeanDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"PERSON_BEAN\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"PERSON_ID\" INTEGER NOT NULL ," + // 1: person_id
                "\"PERSON_TS\" REAL NOT NULL ," + // 2: person_ts
                "\"AUTH_ID\" INTEGER NOT NULL ," + // 3: auth_id
                "\"AUTH_TS\" REAL NOT NULL ," + // 4: auth_ts
                "\"FACE_PIC\" TEXT," + // 5: facePic
                "\"OLD_FACE_PIC\" TEXT," + // 6: oldFacePic
                "\"NAME\" TEXT," + // 7: name
                "\"I_D_NO\" TEXT," + // 8: iD_no
                "\"EMPLOYEE_CARD_ID\" TEXT," + // 9: employee_card_id
                "\"START_TS\" REAL NOT NULL ," + // 10: start_ts
                "\"END_TS\" REAL NOT NULL ," + // 11: end_ts
                "\"COUNT\" INTEGER NOT NULL ," + // 12: count
                "\"NOW_PIC\" TEXT," + // 13: now_pic
                "\"FID\" TEXT UNIQUE ," + // 14: fid
                "\"PERSONALIZED_PERMISSIONS\" TEXT," + // 15: personalizedPermissions
                "\"WEEKLY\" TEXT," + // 16: weekly
                "\"DEPARTMENT\" TEXT," + // 17: department
                "\"POSITION\" TEXT," + // 18: position
                "\"TERM_OF_VALIDITY\" TEXT," + // 19: termOfValidity
                "\"CARD_STATUS\" TEXT," + // 20: cardStatus
                "\"PERSON_NUMBER\" TEXT," + // 21: personNumber
                "\"PIC_NUMBER\" TEXT," + // 22: picNumber
                "\"NAME_YINGZE\" TEXT," + // 23: name_yingze
                "\"IC_NO_HEX\" TEXT);"); // 24: icNoHex
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"PERSON_BEAN\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, PersonBean entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getPerson_id());
        stmt.bindDouble(3, entity.getPerson_ts());
        stmt.bindLong(4, entity.getAuth_id());
        stmt.bindDouble(5, entity.getAuth_ts());
 
        String facePic = entity.getFacePic();
        if (facePic != null) {
            stmt.bindString(6, facePic);
        }
 
        String oldFacePic = entity.getOldFacePic();
        if (oldFacePic != null) {
            stmt.bindString(7, oldFacePic);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(8, name);
        }
 
        String iD_no = entity.getID_no();
        if (iD_no != null) {
            stmt.bindString(9, iD_no);
        }
 
        String employee_card_id = entity.getEmployee_card_id();
        if (employee_card_id != null) {
            stmt.bindString(10, employee_card_id);
        }
        stmt.bindDouble(11, entity.getStart_ts());
        stmt.bindDouble(12, entity.getEnd_ts());
        stmt.bindLong(13, entity.getCount());
 
        String now_pic = entity.getNow_pic();
        if (now_pic != null) {
            stmt.bindString(14, now_pic);
        }
 
        String fid = entity.getFid();
        if (fid != null) {
            stmt.bindString(15, fid);
        }
 
        String personalizedPermissions = entity.getPersonalizedPermissions();
        if (personalizedPermissions != null) {
            stmt.bindString(16, personalizedPermissions);
        }
 
        String weekly = entity.getWeekly();
        if (weekly != null) {
            stmt.bindString(17, weekly);
        }
 
        String department = entity.getDepartment();
        if (department != null) {
            stmt.bindString(18, department);
        }
 
        String position = entity.getPosition();
        if (position != null) {
            stmt.bindString(19, position);
        }
 
        String termOfValidity = entity.getTermOfValidity();
        if (termOfValidity != null) {
            stmt.bindString(20, termOfValidity);
        }
 
        String cardStatus = entity.getCardStatus();
        if (cardStatus != null) {
            stmt.bindString(21, cardStatus);
        }
 
        String personNumber = entity.getPersonNumber();
        if (personNumber != null) {
            stmt.bindString(22, personNumber);
        }
 
        String picNumber = entity.getPicNumber();
        if (picNumber != null) {
            stmt.bindString(23, picNumber);
        }
 
        String name_yingze = entity.getName_yingze();
        if (name_yingze != null) {
            stmt.bindString(24, name_yingze);
        }
 
        String icNoHex = entity.getIcNoHex();
        if (icNoHex != null) {
            stmt.bindString(25, icNoHex);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, PersonBean entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getPerson_id());
        stmt.bindDouble(3, entity.getPerson_ts());
        stmt.bindLong(4, entity.getAuth_id());
        stmt.bindDouble(5, entity.getAuth_ts());
 
        String facePic = entity.getFacePic();
        if (facePic != null) {
            stmt.bindString(6, facePic);
        }
 
        String oldFacePic = entity.getOldFacePic();
        if (oldFacePic != null) {
            stmt.bindString(7, oldFacePic);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(8, name);
        }
 
        String iD_no = entity.getID_no();
        if (iD_no != null) {
            stmt.bindString(9, iD_no);
        }
 
        String employee_card_id = entity.getEmployee_card_id();
        if (employee_card_id != null) {
            stmt.bindString(10, employee_card_id);
        }
        stmt.bindDouble(11, entity.getStart_ts());
        stmt.bindDouble(12, entity.getEnd_ts());
        stmt.bindLong(13, entity.getCount());
 
        String now_pic = entity.getNow_pic();
        if (now_pic != null) {
            stmt.bindString(14, now_pic);
        }
 
        String fid = entity.getFid();
        if (fid != null) {
            stmt.bindString(15, fid);
        }
 
        String personalizedPermissions = entity.getPersonalizedPermissions();
        if (personalizedPermissions != null) {
            stmt.bindString(16, personalizedPermissions);
        }
 
        String weekly = entity.getWeekly();
        if (weekly != null) {
            stmt.bindString(17, weekly);
        }
 
        String department = entity.getDepartment();
        if (department != null) {
            stmt.bindString(18, department);
        }
 
        String position = entity.getPosition();
        if (position != null) {
            stmt.bindString(19, position);
        }
 
        String termOfValidity = entity.getTermOfValidity();
        if (termOfValidity != null) {
            stmt.bindString(20, termOfValidity);
        }
 
        String cardStatus = entity.getCardStatus();
        if (cardStatus != null) {
            stmt.bindString(21, cardStatus);
        }
 
        String personNumber = entity.getPersonNumber();
        if (personNumber != null) {
            stmt.bindString(22, personNumber);
        }
 
        String picNumber = entity.getPicNumber();
        if (picNumber != null) {
            stmt.bindString(23, picNumber);
        }
 
        String name_yingze = entity.getName_yingze();
        if (name_yingze != null) {
            stmt.bindString(24, name_yingze);
        }
 
        String icNoHex = entity.getIcNoHex();
        if (icNoHex != null) {
            stmt.bindString(25, icNoHex);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public PersonBean readEntity(Cursor cursor, int offset) {
        PersonBean entity = new PersonBean( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getLong(offset + 1), // person_id
            cursor.getDouble(offset + 2), // person_ts
            cursor.getLong(offset + 3), // auth_id
            cursor.getDouble(offset + 4), // auth_ts
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // facePic
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // oldFacePic
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // name
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // iD_no
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // employee_card_id
            cursor.getDouble(offset + 10), // start_ts
            cursor.getDouble(offset + 11), // end_ts
            cursor.getLong(offset + 12), // count
            cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13), // now_pic
            cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14), // fid
            cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15), // personalizedPermissions
            cursor.isNull(offset + 16) ? null : cursor.getString(offset + 16), // weekly
            cursor.isNull(offset + 17) ? null : cursor.getString(offset + 17), // department
            cursor.isNull(offset + 18) ? null : cursor.getString(offset + 18), // position
            cursor.isNull(offset + 19) ? null : cursor.getString(offset + 19), // termOfValidity
            cursor.isNull(offset + 20) ? null : cursor.getString(offset + 20), // cardStatus
            cursor.isNull(offset + 21) ? null : cursor.getString(offset + 21), // personNumber
            cursor.isNull(offset + 22) ? null : cursor.getString(offset + 22), // picNumber
            cursor.isNull(offset + 23) ? null : cursor.getString(offset + 23), // name_yingze
            cursor.isNull(offset + 24) ? null : cursor.getString(offset + 24) // icNoHex
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, PersonBean entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setPerson_id(cursor.getLong(offset + 1));
        entity.setPerson_ts(cursor.getDouble(offset + 2));
        entity.setAuth_id(cursor.getLong(offset + 3));
        entity.setAuth_ts(cursor.getDouble(offset + 4));
        entity.setFacePic(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setOldFacePic(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setName(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setID_no(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setEmployee_card_id(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setStart_ts(cursor.getDouble(offset + 10));
        entity.setEnd_ts(cursor.getDouble(offset + 11));
        entity.setCount(cursor.getLong(offset + 12));
        entity.setNow_pic(cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13));
        entity.setFid(cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14));
        entity.setPersonalizedPermissions(cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15));
        entity.setWeekly(cursor.isNull(offset + 16) ? null : cursor.getString(offset + 16));
        entity.setDepartment(cursor.isNull(offset + 17) ? null : cursor.getString(offset + 17));
        entity.setPosition(cursor.isNull(offset + 18) ? null : cursor.getString(offset + 18));
        entity.setTermOfValidity(cursor.isNull(offset + 19) ? null : cursor.getString(offset + 19));
        entity.setCardStatus(cursor.isNull(offset + 20) ? null : cursor.getString(offset + 20));
        entity.setPersonNumber(cursor.isNull(offset + 21) ? null : cursor.getString(offset + 21));
        entity.setPicNumber(cursor.isNull(offset + 22) ? null : cursor.getString(offset + 22));
        entity.setName_yingze(cursor.isNull(offset + 23) ? null : cursor.getString(offset + 23));
        entity.setIcNoHex(cursor.isNull(offset + 24) ? null : cursor.getString(offset + 24));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(PersonBean entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(PersonBean entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
