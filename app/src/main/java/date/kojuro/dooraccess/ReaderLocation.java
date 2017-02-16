package date.kojuro.dooraccess;

import android.location.Location;
import android.location.LocationManager;

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
public class ReaderLocation {

    @Id(autoincrement = true)
    private Long id;

    @NotNull
    private String description;

    @Property
    private double latitude;
    @Property
    private double longitude;


    @Generated(hash = 1429085647)
    public ReaderLocation(Long id, @NotNull String description, double latitude,
            double longitude) {
        this.id = id;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Generated(hash = 112996635)
    public ReaderLocation() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Location getLocation() {

        Location location = new Location(LocationManager.NETWORK_PROVIDER);
        location.setLatitude(getLatitude());
        location.setLongitude(getLongitude());
        return location;
    }

    public String getLocationString() {

        return String.format("Lat: %f, Lon: %f", getLatitude(), getLongitude());
    }
}
