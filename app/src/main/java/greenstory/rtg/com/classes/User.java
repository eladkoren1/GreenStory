package greenstory.rtg.com.classes;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Elad on 17/12/2017.
 */

public class User implements Serializable{

    private String uId;
    private String userName;
    private String familyName;
    private String partnerName;
    private int userAge=0;
    private int partnerAge=0;
    private boolean isFamily = false;
    private ArrayList<Child> children;
    private int points;


    public User() {

    }

    public User(String userName, String familyName, String partnerName,Integer userAge,Integer partnerAge,
                 Boolean isFamily, int points) {
        this.userName = userName;
        this.familyName = familyName;
        this.partnerName = partnerName;
        this.userAge = userAge;
        this.partnerAge = partnerAge;
        this.isFamily = isFamily;
        children = new ArrayList<Child>(1);
        uId = String.valueOf(java.util.Calendar.getInstance().getTimeInMillis());
        this.points = points;
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

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
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

    public void setIsFamily(boolean isFamily) {
        this.isFamily = isFamily;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public class Child {

        private String firstName;
        private String lastName;
        private int age;

        public Child(String firstName, String lastName, Integer age){
            this.firstName = firstName;
            this.lastName = lastName;
            this.age = age;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }

    public class Points {

        private String trackName;
        private int totalPoints;

        public Points() {

            this.trackName = null;
            this.totalPoints = 0;
        }

        public void setTrackName(String trackName) {
            this.trackName = trackName;
        }

        public void setTotalPoints(int totalPoints) {
            this.totalPoints = totalPoints;
        }

        public String getTrackName() {
            return trackName;
        }

        public int getTotalPoints() {
            return totalPoints;
        }


    }


}
