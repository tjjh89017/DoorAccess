package date.kojuro.dooraccess;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

/**
 * Created by date on 2017/2/16.
 */

@Entity
public class UIDLocationRelation {

    @Id(autoincrement = true)
    private Long id;

    private long tagId;
    private long readerLocationId;

    @NotNull
    @ToOne(joinProperty = "tagId")
    private Tag tag;

    @NotNull
    @ToOne(joinProperty = "readerLocationId")
    private ReaderLocation readerLocation;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 1317362407)
    private transient UIDLocationRelationDao myDao;

    @Generated(hash = 705288424)
    public UIDLocationRelation(Long id, long tagId, long readerLocationId) {
        this.id = id;
        this.tagId = tagId;
        this.readerLocationId = readerLocationId;
    }

    @Generated(hash = 1987986243)
    public UIDLocationRelation() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getTagId() {
        return this.tagId;
    }

    public void setTagId(long tagId) {
        this.tagId = tagId;
    }

    public long getReaderLocationId() {
        return this.readerLocationId;
    }

    public void setReaderLocationId(long readerLocationId) {
        this.readerLocationId = readerLocationId;
    }

    @Generated(hash = 1006483784)
    private transient Long tag__resolvedKey;

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1607739560)
    public Tag getTag() {
        long __key = this.tagId;
        if (tag__resolvedKey == null || !tag__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            TagDao targetDao = daoSession.getTagDao();
            Tag tagNew = targetDao.load(__key);
            synchronized (this) {
                tag = tagNew;
                tag__resolvedKey = __key;
            }
        }
        return tag;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1728988431)
    public void setTag(@NotNull Tag tag) {
        if (tag == null) {
            throw new DaoException(
                    "To-one property 'tagId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.tag = tag;
            tagId = tag.getId();
            tag__resolvedKey = tagId;
        }
    }

    @Generated(hash = 1723793669)
    private transient Long readerLocation__resolvedKey;

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 502890283)
    public ReaderLocation getReaderLocation() {
        long __key = this.readerLocationId;
        if (readerLocation__resolvedKey == null
                || !readerLocation__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ReaderLocationDao targetDao = daoSession.getReaderLocationDao();
            ReaderLocation readerLocationNew = targetDao.load(__key);
            synchronized (this) {
                readerLocation = readerLocationNew;
                readerLocation__resolvedKey = __key;
            }
        }
        return readerLocation;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1154319556)
    public void setReaderLocation(@NotNull ReaderLocation readerLocation) {
        if (readerLocation == null) {
            throw new DaoException(
                    "To-one property 'readerLocationId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.readerLocation = readerLocation;
            readerLocationId = readerLocation.getId();
            readerLocation__resolvedKey = readerLocationId;
        }
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 747990283)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getUIDLocationRelationDao() : null;
    }

   
}
