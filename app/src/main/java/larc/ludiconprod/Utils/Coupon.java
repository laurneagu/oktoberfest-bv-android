package larc.ludiconprod.Utils;

import java.io.Serializable;

/**
 * Created by alex_ on 18.08.2017.
 */

public class Coupon implements Serializable {

    public String couponBlockId;
    public String title;
    public String description;
    public String companyName;
    public int expiryDate;
    public int numberOfCoupons;
    public int ludicoins;
    public Integer discountCode;

    public Coupon() {

    }


}
