/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package src.util;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import src.models.*;


public class database {
    /** SQL URL */
    private static final String SQLurl = "jdbc:mysql://localhost:3306/DOCS";
    /** SQL Username */
    private static final String SQLusername = "root";
    /** SQL Password */
    private static final String SQLpassword = "Sankalp2704@";
    /** SQL Connection */
    private static Connection con = null;
    

    private static Connection connect(){
        try{  
            if(con != null){
                return con;
            }
            Class.forName("com.mysql.jdbc.Driver");  
            con = DriverManager.getConnection(SQLurl,SQLusername,SQLpassword);   
        }catch(Exception ex){ 
            ex.printStackTrace();
        }  
        return con;
    }

    private static void closeConnection(){
        if(con != null){
            try {
                con.close();
                con = null;
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
    

    public static boolean createAppointment(user user, appointment appointment){
        con = connect();
        String query = "INSERT INTO appointment (healthproblem, dateandtime, reminder, user_id, doctor_id) VALUES(?, ?, ?, ?, ?)";
        try {
            int doctorID = createDoctor(appointment.getDoctor());
            if(doctorID<0){
                closeConnection();
                return false;
            }
            PreparedStatement statement = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, appointment.getHealthproblem());
            statement.setTimestamp(2, Timestamp.valueOf(appointment.getDateandtime()));
            statement.setTimestamp(3, Timestamp.valueOf(appointment.getReminder()));
            statement.setInt(4, user.getId());
            statement.setInt(5, doctorID);
            statement.executeUpdate();
            ResultSet primarykey = statement.getGeneratedKeys();
            if(primarykey.next()){
                appointment.setId(primarykey.getInt(1));
            }
            statement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            closeConnection();
            return false;
        }
        closeConnection();
        user.addAppointment(appointment);
        return true;
    }



    public static boolean shiftAppointment(appointment appointment){
        con = connect();
        String query = "UPDATE appointment SET dateandtime = ?, reminder = ? WHERE id = ?";
        try {
            PreparedStatement statement = con.prepareStatement(query);
            statement.setTimestamp(1, Timestamp.valueOf(appointment.getDateandtime()));
            statement.setTimestamp(2, Timestamp.valueOf(appointment.getReminder()));
            statement.setInt(3, appointment.getId());
            statement.executeUpdate();
            statement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            closeConnection();
            return false;
        }
        closeConnection();
        return true;
    }


    public static boolean deleteAppointment(appointment appointment){
    
        con = connect();
        String query = "DELETE FROM appointment WHERE id = ?";
        try {
            PreparedStatement statement = con.prepareStatement(query);
            statement.setInt(1, appointment.getId());
            statement.executeUpdate();
            statement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            closeConnection();
            return false;
        }
        closeConnection();
        return true;
    }
    

    public static ArrayList<appointment> getAppointments(user user){
        ArrayList<appointment> appointments = new ArrayList<appointment>();
        con = connect();
        String query = "SELECT * FROM appointment a INNER JOIN doctor d on d.id = a.doctor_id WHERE a.user_id = ? ORDER by a.dateandtime DESC";
        try {
            PreparedStatement statement = con.prepareStatement(query);
            statement.setInt(1, user.getId());
            ResultSet result = statement.executeQuery();
            while(result.next()){
                int id = result.getInt("id");
                String healthproblem = result.getString("healthproblem");
                doctor doctor = new doctor(result.getString("place_id"), result.getString("name"), result.getString("address"));
                LocalDateTime dateandtime = result.getTimestamp("dateandtime").toLocalDateTime();
                LocalDateTime reminder = result.getTimestamp("reminder").toLocalDateTime();
                boolean remindercheck = (result.getInt("remindercheck") != 0);
                appointments.add(new appointment(id, doctor, dateandtime, reminder, healthproblem, remindercheck));
            }
            statement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            closeConnection();
            return null;
        }
        closeConnection();
        return appointments;
    }
    

    public static ArrayList<appointment> getReminderNotSendAppointments(){
        ArrayList<appointment> appointments = new ArrayList<appointment>();
        con = connect();
        String query = "SELECT * FROM appointment a INNER JOIN doctor d on d.id = a.doctor_id WHERE remindercheck = 0";
        try {
            PreparedStatement statement = con.prepareStatement(query);
            ResultSet result = statement.executeQuery();
            while(result.next()){
                int id = result.getInt("id");
                String healthproblem = result.getString("healthproblem");
                doctor doctor = new doctor(result.getString("place_id"), result.getString("name"), result.getString("address"));
                LocalDateTime dateandtime = result.getTimestamp("dateandtime").toLocalDateTime();
                LocalDateTime reminder = result.getTimestamp("reminder").toLocalDateTime();
                boolean remindercheck = (result.getInt("remindercheck") != 0);
                appointments.add(new appointment(id, doctor, dateandtime, reminder, healthproblem, remindercheck));
            }
            statement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            closeConnection();
            return null;
        }
        closeConnection();
        return appointments;
    }
    
    /**
     * Save in the Database that the Remindermail of an Appointment was sent
    e
     */
    public static boolean reminderSend(appointment appointment){
        con = connect();
        String query = "UPDATE appointment SET remindercheck = ? WHERE id = ?";
        try {
            PreparedStatement statement = con.prepareStatement(query);
            statement.setInt(1, 1);
            statement.setInt(2, appointment.getId());
            statement.executeUpdate();
            statement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            closeConnection();
            return false;
        }
        closeConnection();
        return true;
    }
    
    /**
     * Create a new Doctor

     */
    private static int createDoctor(doctor doctor){ 
        int id = checkDoctor(doctor.getPlace_id());
        if(id == -1){
            String query = "INSERT INTO doctor (place_id, name, address) VALUES(?, ?, ?)";
            try {
                PreparedStatement statement = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                statement.setString(1, doctor.getPlace_id());
                statement.setString(2, doctor.getName());
                statement.setString(3, doctor.getAddress());
                statement.executeUpdate();
                ResultSet primarykey = statement.getGeneratedKeys();
                if(primarykey.next()){
                    id = primarykey.getInt(1);
                }
                statement.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
                return -2;
            }
            return id;     
        }
        
        return id;
    }
    
    /**
     * Check if Doctor alredy exist to prevent redundancy in the database
     * place_id the id provided by the google maps api
     *  return the id of the doctor if the doctor is alredy in the database, otherwise it returns -1
     */
    private static int checkDoctor(String place_id){ 
        String query = "SELECT id FROM doctor WHERE place_id = ?";
        try {
            PreparedStatement statement = con.prepareStatement(query);
            statement.setString(1, place_id);
            ResultSet result = statement.executeQuery();
            if(result.next()){
                int id = result.getInt("id");
                statement.close();
                return id;
            }
            statement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            return -2;
        }
        return -1;
    }



    public static ArrayList<String> getUsersAsString(){
        ArrayList<String> users = new ArrayList<String>();
        con = connect();
        String query = "SELECT * FROM user";
        try {
            PreparedStatement statement = con.prepareStatement(query);
            ResultSet result = statement.executeQuery();
            while(result.next()){
                String username = result.getString("username");
                users.add(username);
            }
            statement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            closeConnection();
            return null;
        }
        closeConnection();
        return users;
    }
    

    public static user getUser(String username){
        con = connect();
        user user = null;
        String query = "SELECT * FROM user WHERE username = ?";
        try {
            PreparedStatement statement = con.prepareStatement(query);
            statement.setString(1, username);
            ResultSet result = statement.executeQuery();
            while(result.next()){
                int id = result.getInt("id");
                String password = result.getString("password");
                String email = result.getString("email");
                String firstname = result.getString("firstname");
                String lastname = result.getString("lastname");
                String gender = result.getString("gender");
                LocalDate dateofbirth = result.getDate("dateofbirth").toLocalDate();
                String street = result.getString("street");
                String housenumber = result.getString("housenumber");
                String city = result.getString("city");
                String zipcode = result.getString("zipcode");
                double lat = result.getDouble("lat");
                double lng = result.getDouble("lng");
                String healthinformation = result.getString("healthinformation");
                String insurancename = result.getString("insurancename");
                String insurancetype = result.getString("insurancetype");
                boolean admin = (result.getInt("admin") != 0);
                user = new user(id,username,password,email,firstname,lastname,gender,dateofbirth,street,housenumber,city,zipcode,lat,lng,healthinformation,insurancename,insurancetype,admin);
            }
            statement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            closeConnection();
            return null;
        }
        closeConnection();
        return user;
    }
    

    public static user getUserofAppointment(int AppointmentID){
        con = connect();
        user user = null;
        String query = "SELECT * FROM appointment a INNER JOIN user u on u.id = a.user_id WHERE a.id = ?";
        try {
            PreparedStatement statement = con.prepareStatement(query);
            statement.setInt(1, AppointmentID);
            ResultSet result = statement.executeQuery();
            while(result.next()){
                int id = result.getInt("id");
                String username = result.getString("username");
                String password = result.getString("password");
                String email = result.getString("email");
                String firstname = result.getString("firstname");
                String lastname = result.getString("lastname");
                String gender = result.getString("gender");
                LocalDate dateofbirth = result.getDate("dateofbirth").toLocalDate();
                String street = result.getString("street");
                String housenumber = result.getString("housenumber");
                String city = result.getString("city");
                String zipcode = result.getString("zipcode");
                double lat = result.getDouble("lat");
                double lng = result.getDouble("lng");
                String healthinformation = result.getString("healthinformation");
                String insurancename = result.getString("insurancename");
                String insurancetype = result.getString("insurancetype");
                boolean admin = (result.getInt("admin") != 0);
                user = new user(id,username,password,email,firstname,lastname,gender,dateofbirth,street,housenumber,city,zipcode,lat,lng,healthinformation,insurancename,insurancetype,admin);
            }
            statement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            closeConnection();
            return null;
        }
        closeConnection();
        return user;
    }
    

    public static user createUser(user user){ 
        con = connect();
        String query = "INSERT INTO user (username, password, email, firstname, lastname, gender, dateofbirth, street, housenumber, city, zipcode, lat, lng, healthinformation, insurancename, insurancetype) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement statement = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getEmail());
            statement.setString(4, user.getFirstname());
            statement.setString(5, user.getLastname());
            statement.setString(6, user.getGender());
            statement.setDate(7, Date.valueOf(user.getDateofbirth()));
            statement.setString(8, user.getStreet());
            statement.setString(9, user.getHousenumber());
            statement.setString(10, user.getCity());
            statement.setString(11, user.getZipcode());
            statement.setDouble(12, user.getLat());
            statement.setDouble(13, user.getLng());
            statement.setString(14, user.getHealthinformation());
            statement.setString(15, user.getInsurancename());
            statement.setString(16, user.getInsurancetype());
            statement.executeUpdate();
            ResultSet primarykey = statement.getGeneratedKeys();
            if(primarykey.next()){
                user.setId(primarykey.getInt(1));
            }
            statement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            closeConnection();
            return null;
        }
        closeConnection();
        return user;
    }

    /**
     * Update User

     */
    public static boolean updateUser(user user){
        con = connect();
        String query = "UPDATE user SET username = ?, password = ?, email = ?, firstname = ?, lastname = ?, gender = ?, dateofbirth = ?, street = ?, housenumber = ?, city = ?, zipcode = ?, lat = ?, lng = ?, healthinformation = ?, insurancename = ?, insurancetype = ?, admin = ? WHERE id = ?";
        try {
            PreparedStatement statement = con.prepareStatement(query);
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getEmail());
            statement.setString(4, user.getFirstname());
            statement.setString(5, user.getLastname());
            statement.setString(6, user.getGender());
            statement.setDate(7, Date.valueOf(user.getDateofbirth()));
            statement.setString(8, user.getStreet());
            statement.setString(9, user.getHousenumber());
            statement.setString(10, user.getCity());
            statement.setString(11, user.getZipcode());
            statement.setDouble(12, user.getLat());
            statement.setDouble(13, user.getLng());
            statement.setString(14, user.getHealthinformation());
            statement.setString(15, user.getInsurancename());
            statement.setString(16, user.getInsurancetype());
            statement.setInt(17, user.isAdmin() ? 1 : 0);
            statement.setInt(18, user.getId());
            statement.executeUpdate();
            statement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            closeConnection();
            return false;
        }
        closeConnection();
        return true;
    }
    

    public static user login(String username, String passwordCheck){
        user user = getUser(username);
        if(user == null){
            return null;
        }
        if(!password.check(passwordCheck, user.getPassword())){
            return null;
        }
        return user;
    }
    

    public static boolean checkAvailable(String username, String email){
        con = connect();
        String query = "SELECT * FROM user WHERE username = ? or email = ?";
        try {
            PreparedStatement statement = con.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, email);
            ResultSet result = statement.executeQuery();
            boolean check = result.next();
            statement.close();
            closeConnection();
            return !check;
        } catch (SQLException ex) {
            ex.printStackTrace();
            closeConnection();
            return false;
        }
    }
    

    public static boolean deleteUser(user user){ 
        con = connect();
        String query = "DELETE FROM user WHERE id = ?";
        try {
            PreparedStatement statement = con.prepareStatement(query);
            statement.setInt(1, user.getId());
            statement.executeUpdate();
            statement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            closeConnection();
            return false;
        }
        closeConnection();
        return true;
    }

    public static ArrayList<symptom> getSymptoms(){
        ArrayList<symptom> symptoms = new ArrayList<symptom>();
        con = connect();
        String query = "SELECT s.symptom, d.doctortype from symptoms s INNER JOIN doctortype d on s.doctortype_id = d.id order by s.symptom";
        try {
            PreparedStatement statement = con.prepareStatement(query);
            ResultSet result = statement.executeQuery();
            while(result.next()){
                String name = result.getString("symptom");
                String doctortype = result.getString("doctortype");
                symptoms.add(new symptom(name, doctortype));
            }
            statement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            closeConnection();
            return null;
        }
        closeConnection();
        return symptoms;
    }
}
