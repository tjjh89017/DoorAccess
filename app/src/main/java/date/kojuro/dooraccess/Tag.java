package date.kojuro.dooraccess;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;
import java.lang.Long;

/**
 * Created by date on 2017/2/13.
 */

@Entity
public class Tag {

    @Id(autoincrement = true)
    private Long id;
    @NotNull
    private String mUID;
    @Property
    private String mDescription;

    public Tag() {

        setUID("deadbeef");
        setDescription("Default UID 0xDEADBEEF");
    }

    public Tag(String UID, String Description) {

        setUID(UID);
        setDescription(Description);
    }

    @Generated(hash = 1299354376)
    public Tag(Long id, @NotNull String mUID, String mDescription) {
        this.id = id;
        this.mUID = mUID;
        this.mDescription = mDescription;
    }

    public String getUID() {

        return mUID;
    }

    public void setUID(String UID) {

        /**
         * Need Verify UID
         */
        mUID = UID;
    }

    public String getDescription() {

        return mDescription;
    }

    public void setDescription(String Description) {

        mDescription = Description;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMUID() {
        return this.mUID;
    }

    public void setMUID(String mUID) {
        this.mUID = mUID;
    }

    public String getMDescription() {
        return this.mDescription;
    }

    public void setMDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    @Override
    public String toString() {
        return getDescription();
    }
}
