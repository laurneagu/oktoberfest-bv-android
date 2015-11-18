package larc.ludicon.SqlConnection;

/**
 * Created by Ciprian on 11/17/2015.
 */
public class User {

    public int id;
    public String firstName;
    public String lastName;
    public String email;
    public int age;
    public char sex;
    public String password;
    //TODO add groups,ranks,rating,picture

    public User ( String i_firstName, String i_lastName, String i_email,String i_password)// int i_age, char i_sex)
    {   //TODO get age and sex
        firstName = i_firstName;
        lastName = i_lastName;
        email = i_email;
        //age = i_age;
        //sex = i_sex;
        password = i_password;
    }

}
