/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.muni.fi.pv168.hotelmanager.backend;

import java.util.Date;
import java.util.Objects;

/**
 * This entity class represents Guest. Guest has some name, phone number,
 * identification card number and date of birth.
 * 
 * @author Tomas Smid
 */
public class Guest implements Comparable<Guest>{
    private Long id;
    private String name;
    private String phone;
    private String idCardNum;    
    private Date born;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getIdCardNum() {
        return idCardNum;
    }

    public void setIdCardNum(String idCardNum) {
        this.idCardNum = idCardNum;
    }

    public Date getBorn() {
        return new Date(born.getTime());
    }

    public void setBorn(Date born) {
        this.born = new Date(born.getTime());
    }
    
    /**
     * Returns a brief description of this guest. The exact details
     * of the representation are unspecified and subject to change,
     * but the following may be regarded as typical:
     *
     * "[Guest: id=154897985, name=Josef Trnka, idCardNumber=741478969, date-of-birth=1974-11-24]"
     * 
     * @return a brief description of this guest as string
     */
    @Override
    public String toString(){
        return "[" + "Guest: " + "id=" + id + ", name=" + name +
               ", idCardNumber=" + idCardNum + ", date-of-birth=" + born + "]"; 
    }
    
    @Override
    public boolean equals(Object obj){
        if(obj == this) return true;
        if(!(obj instanceof Guest)) return false;
        Guest guest = (Guest)obj;
        return ((this.id == guest.id) || 
                (this.id != null && this.id.equals(guest.id)));        
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public int compareTo(Guest guest) {
        return this.id.compareTo(guest.id);
    }
}
