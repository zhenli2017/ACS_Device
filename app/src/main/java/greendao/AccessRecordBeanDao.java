package greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.thdtek.acs.terminal.bean.AccessRecordBean;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "ACCESS_RECORD_BEAN".
*/
public class AccessRecordBeanDao extends AbstractDao<AccessRecordBean, Long> {

    public static final String TABLENAME = "ACCESS_RECORD_BEAN";

    /**
     * Properties of entity AccessRecordBean.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Time = new Property(1, long.class, "time", false, "TIME");
        public final static Property AccessSuccessful = new Property(2, int.class, "accessSuccessful", false, "ACCESS_SUCCESSFUL");
        public final static Property Type = new Property(3, int.class, "type", false, "TYPE");
        public final static Property AccessImage = new Property(4, String.class, "accessImage", false, "ACCESS_IMAGE");
        public final static Property PersonId = new Property(5, long.class, "personId", false, "PERSON_ID");
        public final static Property AuthorityId = new Property(6, long.class, "authorityId", false, "AUTHORITY_ID");
        public final static Property CardNum = new Property(7, String.class, "cardNum", false, "CARD_NUM");
        public final static Property IdNum = new Property(8, String.class, "idNum", false, "ID_NUM");
        public final static Property PersonImage = new Property(9, String.class, "personImage", false, "PERSON_IMAGE");
        public final static Property PersonName = new Property(10, String.class, "personName", false, "PERSON_NAME");
        public final static Property DefaultFaceFeatureNumber = new Property(11, float.class, "defaultFaceFeatureNumber", false, "DEFAULT_FACE_FEATURE_NUMBER");
        public final static Property CurrentFaceFeatureNumber = new Property(12, float.class, "currentFaceFeatureNumber", false, "CURRENT_FACE_FEATURE_NUMBER");
        public final static Property Count = new Property(13, long.class, "count", false, "COUNT");
        public final static Property PersonRate = new Property(14, float.class, "personRate", false, "PERSON_RATE");
        public final static Property AccordRate = new Property(15, float.class, "accordRate", false, "ACCORD_RATE");
        public final static Property UploadToHttp = new Property(16, boolean.class, "uploadToHttp", false, "UPLOAD_TO_HTTP");
        public final static Property Fid = new Property(17, String.class, "fid", false, "FID");
        public final static Property Gender = new Property(18, String.class, "gender", false, "GENDER");
        public final static Property Birthday = new Property(19, String.class, "birthday", false, "BIRTHDAY");
        public final static Property Location = new Property(20, String.class, "location", false, "LOCATION");
        public final static Property ValidityTime = new Property(21, String.class, "validityTime", false, "VALIDITY_TIME");
        public final static Property SigningOrganization = new Property(22, String.class, "signingOrganization", false, "SIGNING_ORGANIZATION");
        public final static Property Nation = new Property(23, String.class, "nation", false, "NATION");
    };


    public AccessRecordBeanDao(DaoConfig config) {
        super(config);
    }
    
    public AccessRecordBeanDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"ACCESS_RECORD_BEAN\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"TIME\" INTEGER NOT NULL ," + // 1: time
                "\"ACCESS_SUCCESSFUL\" INTEGER NOT NULL ," + // 2: accessSuccessful
                "\"TYPE\" INTEGER NOT NULL ," + // 3: type
                "\"ACCESS_IMAGE\" TEXT," + // 4: accessImage
                "\"PERSON_ID\" INTEGER NOT NULL ," + // 5: personId
                "\"AUTHORITY_ID\" INTEGER NOT NULL ," + // 6: authorityId
                "\"CARD_NUM\" TEXT," + // 7: cardNum
                "\"ID_NUM\" TEXT," + // 8: idNum
                "\"PERSON_IMAGE\" TEXT," + // 9: personImage
                "\"PERSON_NAME\" TEXT," + // 10: personName
                "\"DEFAULT_FACE_FEATURE_NUMBER\" REAL NOT NULL ," + // 11: defaultFaceFeatureNumber
                "\"CURRENT_FACE_FEATURE_NUMBER\" REAL NOT NULL ," + // 12: currentFaceFeatureNumber
                "\"COUNT\" INTEGER NOT NULL ," + // 13: count
                "\"PERSON_RATE\" REAL NOT NULL ," + // 14: personRate
                "\"ACCORD_RATE\" REAL NOT NULL ," + // 15: accordRate
                "\"UPLOAD_TO_HTTP\" INTEGER NOT NULL ," + // 16: uploadToHttp
                "\"FID\" TEXT," + // 17: fid
                "\"GENDER\" TEXT," + // 18: gender
                "\"BIRTHDAY\" TEXT," + // 19: birthday
                "\"LOCATION\" TEXT," + // 20: location
                "\"VALIDITY_TIME\" TEXT," + // 21: validityTime
                "\"SIGNING_ORGANIZATION\" TEXT," + // 22: signingOrganization
                "\"NATION\" TEXT);"); // 23: nation
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"ACCESS_RECORD_BEAN\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, AccessRecordBean entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getTime());
        stmt.bindLong(3, entity.getAccessSuccessful());
        stmt.bindLong(4, entity.getType());
 
        String accessImage = entity.getAccessImage();
        if (accessImage != null) {
            stmt.bindString(5, accessImage);
        }
        stmt.bindLong(6, entity.getPersonId());
        stmt.bindLong(7, entity.getAuthorityId());
 
        String cardNum = entity.getCardNum();
        if (cardNum != null) {
            stmt.bindString(8, cardNum);
        }
 
        String idNum = entity.getIdNum();
        if (idNum != null) {
            stmt.bindString(9, idNum);
        }
 
        String personImage = entity.getPersonImage();
        if (personImage != null) {
            stmt.bindString(10, personImage);
        }
 
        String personName = entity.getPersonName();
        if (personName != null) {
            stmt.bindString(11, personName);
        }
        stmt.bindDouble(12, entity.getDefaultFaceFeatureNumber());
        stmt.bindDouble(13, entity.getCurrentFaceFeatureNumber());
        stmt.bindLong(14, entity.getCount());
        stmt.bindDouble(15, entity.getPersonRate());
        stmt.bindDouble(16, entity.getAccordRate());
        stmt.bindLong(17, entity.getUploadToHttp() ? 1L: 0L);
 
        String fid = entity.getFid();
        if (fid != null) {
            stmt.bindString(18, fid);
        }
 
        String gender = entity.getGender();
        if (gender != null) {
            stmt.bindString(19, gender);
        }
 
        String birthday = entity.getBirthday();
        if (birthday != null) {
            stmt.bindString(20, birthday);
        }
 
        String location = entity.getLocation();
        if (location != null) {
            stmt.bindString(21, location);
        }
 
        String validityTime = entity.getValidityTime();
        if (validityTime != null) {
            stmt.bindString(22, validityTime);
        }
 
        String signingOrganization = entity.getSigningOrganization();
        if (signingOrganization != null) {
            stmt.bindString(23, signingOrganization);
        }
 
        String nation = entity.getNation();
        if (nation != null) {
            stmt.bindString(24, nation);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, AccessRecordBean entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getTime());
        stmt.bindLong(3, entity.getAccessSuccessful());
        stmt.bindLong(4, entity.getType());
 
        String accessImage = entity.getAccessImage();
        if (accessImage != null) {
            stmt.bindString(5, accessImage);
        }
        stmt.bindLong(6, entity.getPersonId());
        stmt.bindLong(7, entity.getAuthorityId());
 
        String cardNum = entity.getCardNum();
        if (cardNum != null) {
            stmt.bindString(8, cardNum);
        }
 
        String idNum = entity.getIdNum();
        if (idNum != null) {
            stmt.bindString(9, idNum);
        }
 
        String personImage = entity.getPersonImage();
        if (personImage != null) {
            stmt.bindString(10, personImage);
        }
 
        String personName = entity.getPersonName();
        if (personName != null) {
            stmt.bindString(11, personName);
        }
        stmt.bindDouble(12, entity.getDefaultFaceFeatureNumber());
        stmt.bindDouble(13, entity.getCurrentFaceFeatureNumber());
        stmt.bindLong(14, entity.getCount());
        stmt.bindDouble(15, entity.getPersonRate());
        stmt.bindDouble(16, entity.getAccordRate());
        stmt.bindLong(17, entity.getUploadToHttp() ? 1L: 0L);
 
        String fid = entity.getFid();
        if (fid != null) {
            stmt.bindString(18, fid);
        }
 
        String gender = entity.getGender();
        if (gender != null) {
            stmt.bindString(19, gender);
        }
 
        String birthday = entity.getBirthday();
        if (birthday != null) {
            stmt.bindString(20, birthday);
        }
 
        String location = entity.getLocation();
        if (location != null) {
            stmt.bindString(21, location);
        }
 
        String validityTime = entity.getValidityTime();
        if (validityTime != null) {
            stmt.bindString(22, validityTime);
        }
 
        String signingOrganization = entity.getSigningOrganization();
        if (signingOrganization != null) {
            stmt.bindString(23, signingOrganization);
        }
 
        String nation = entity.getNation();
        if (nation != null) {
            stmt.bindString(24, nation);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public AccessRecordBean readEntity(Cursor cursor, int offset) {
        AccessRecordBean entity = new AccessRecordBean( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getLong(offset + 1), // time
            cursor.getInt(offset + 2), // accessSuccessful
            cursor.getInt(offset + 3), // type
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // accessImage
            cursor.getLong(offset + 5), // personId
            cursor.getLong(offset + 6), // authorityId
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // cardNum
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // idNum
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // personImage
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // personName
            cursor.getFloat(offset + 11), // defaultFaceFeatureNumber
            cursor.getFloat(offset + 12), // currentFaceFeatureNumber
            cursor.getLong(offset + 13), // count
            cursor.getFloat(offset + 14), // personRate
            cursor.getFloat(offset + 15), // accordRate
            cursor.getShort(offset + 16) != 0, // uploadToHttp
            cursor.isNull(offset + 17) ? null : cursor.getString(offset + 17), // fid
            cursor.isNull(offset + 18) ? null : cursor.getString(offset + 18), // gender
            cursor.isNull(offset + 19) ? null : cursor.getString(offset + 19), // birthday
            cursor.isNull(offset + 20) ? null : cursor.getString(offset + 20), // location
            cursor.isNull(offset + 21) ? null : cursor.getString(offset + 21), // validityTime
            cursor.isNull(offset + 22) ? null : cursor.getString(offset + 22), // signingOrganization
            cursor.isNull(offset + 23) ? null : cursor.getString(offset + 23) // nation
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, AccessRecordBean entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setTime(cursor.getLong(offset + 1));
        entity.setAccessSuccessful(cursor.getInt(offset + 2));
        entity.setType(cursor.getInt(offset + 3));
        entity.setAccessImage(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setPersonId(cursor.getLong(offset + 5));
        entity.setAuthorityId(cursor.getLong(offset + 6));
        entity.setCardNum(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setIdNum(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setPersonImage(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setPersonName(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setDefaultFaceFeatureNumber(cursor.getFloat(offset + 11));
        entity.setCurrentFaceFeatureNumber(cursor.getFloat(offset + 12));
        entity.setCount(cursor.getLong(offset + 13));
        entity.setPersonRate(cursor.getFloat(offset + 14));
        entity.setAccordRate(cursor.getFloat(offset + 15));
        entity.setUploadToHttp(cursor.getShort(offset + 16) != 0);
        entity.setFid(cursor.isNull(offset + 17) ? null : cursor.getString(offset + 17));
        entity.setGender(cursor.isNull(offset + 18) ? null : cursor.getString(offset + 18));
        entity.setBirthday(cursor.isNull(offset + 19) ? null : cursor.getString(offset + 19));
        entity.setLocation(cursor.isNull(offset + 20) ? null : cursor.getString(offset + 20));
        entity.setValidityTime(cursor.isNull(offset + 21) ? null : cursor.getString(offset + 21));
        entity.setSigningOrganization(cursor.isNull(offset + 22) ? null : cursor.getString(offset + 22));
        entity.setNation(cursor.isNull(offset + 23) ? null : cursor.getString(offset + 23));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(AccessRecordBean entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(AccessRecordBean entity) {
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
