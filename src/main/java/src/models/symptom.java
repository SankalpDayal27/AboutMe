/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package src.models;

/**
 *
 * Symptom Model
 */
public class symptom {

    /**
     *  name of the symptom
     */
    private String name;

    /**

     */
    private String doctortype;


    public symptom(String name, String doctortype) {
        this.name = name;
        this.doctortype = doctortype;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getDoctortype() {
        return doctortype;
    }

    public void setDoctortype(String doctortype) {
        this.doctortype = doctortype;
    }
       
       
}
