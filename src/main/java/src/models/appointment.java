/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package src.models;
import java.time.LocalDateTime;
/**
 *
 * Appointment Model
 */
public class appointment {

    /**
     * id in the database
     */
    private int id;

    /**
     * the doctor of the appointment
     */
    private doctor doctor;

    /**
     * the date and time of the appointment
     */
    private LocalDateTime dateandtime;

    /**
     * the data and time when the reminder mail should be send
     */
    private LocalDateTime reminder;

    /**
     * the healthproblem of the user
     */
    private String healthproblem;

    /**
     * true if the reminder-mail was send, otherwise false
     */
    private boolean remindercheck;
        

    public appointment(int id, doctor doctor, LocalDateTime dateandtime, LocalDateTime reminder, String healthproblem, boolean remindercheck) {
        this.id = id;
        this.doctor = doctor;
        this.dateandtime = dateandtime;
        this.reminder = reminder;
        this.healthproblem = healthproblem;
        this.remindercheck = remindercheck;
    }
    

    public appointment(){}


    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }


    public doctor getDoctor() {
        return doctor;
    }


    public void setDoctor(doctor doctor) {
        this.doctor = doctor;
    }


    public LocalDateTime getDateandtime() {
        return dateandtime;
    }


    public void setDateandtime(LocalDateTime dateandtime) {
        this.dateandtime = dateandtime;
    }


    public LocalDateTime getReminder() {
        return reminder;
    }


    public void setReminder(LocalDateTime reminder) {
        this.reminder = reminder;
    }


    public String getHealthproblem() {
        return healthproblem;
    }


    public void setHealthproblem(String healthproblem) {
        this.healthproblem = healthproblem;
    }


    public boolean isRemindercheck() {
        return remindercheck;
    }


    public void setRemindercheck(boolean remindercheck) {
        this.remindercheck = remindercheck;
    }
        
    
        
}
