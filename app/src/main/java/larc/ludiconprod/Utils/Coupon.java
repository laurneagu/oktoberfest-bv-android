package larc.ludiconprod.Utils;

import java.io.Serializable;

/**
 * Created by alex_ on 18.08.2017.
 */

public class Coupon implements Serializable {

    public String couponBlockId;
    public String companyPicture;
    public String title;
    public String description;
    public String companyName;
    public long expiryDate;
    public int numberOfCoupons;
    public int ludicoins;
    public String discountCode;

    public Coupon() {

    }


}
