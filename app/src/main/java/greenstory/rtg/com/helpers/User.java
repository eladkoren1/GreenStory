package greenstory.rtg.com.helpers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Elad on 17/12/2017.
 */

public class User {

    private String uId;
    private String userName;
    private String partnerName;
    private int userAge;
    private int partnerAge;
    private String familyName;
    private boolean isFamily = false;
    private ArrayList<Child> children;
    private Points points;


    public User(String userName, String partnerName,Integer userAge,Integer partnerAge,
                String familyName, Boolean isFamily) {
        this.userName = userName;
        this.partnerName = partnerName;
        this.userAge = userAge;
        this.partnerAge = partnerAge;
        this.familyName = familyName;
        this.isFamily = isFamily;
        children = new ArrayList<Child>(1);
        uId = String.valueOf(java.util.Calendar.getInstance().getTimeInMillis());
        points = new Points();
    }

    public User(String userName, String familyName, boolean isFamily) {
        this.userName = userName;
        this.familyName = familyName;
        this.isFamily = isFamily;
    }

    public void addChild(String FirstName, String LastName, Integer Age){
        children.add(new Child(FirstName,LastName,Age));
    }

    public ArrayList<Child> getChildren(){
        return children;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    public int getUserAge() {
        return userAge;
    }

    public void setUserAge(int userAge) {
        this.userAge = userAge;
    }

    public int getPartnerAge() {
        return partnerAge;
    }

    public void setPartnerAge(int partnerAge) {
        this.partnerAge = partnerAge;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public boolean isFamily() {
        return isFamily;
    }

    public void setFamily(boolean family) {
        isFamily = family;
    }

}
