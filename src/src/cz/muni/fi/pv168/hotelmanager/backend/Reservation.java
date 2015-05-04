/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.muni.fi.pv168.hotelmanager.backend;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

/**
 *
 * @author Ondrej Smelko, Tomas Smid
 */
public class Reservation {
    private Long id;
    private Room room;
    private Date startTime;
    private Date realEndTime;
    private Date expectedEndTime;
    private Guest guest;
    private BigDecimal servicesSpendings;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Date getStartTime() {
        return (startTime == null ? null : new Date(startTime.getTime()));
    }

    public void setStartTime(Date startTime) {
        this.startTime = (startTime == null ? null : new Date(startTime.getTime()));
    }

    public Date getRealEndTime() {        
        return (realEndTime == null ? null : new Date(realEndTime.getTime()));
    }

    public void setRealEndTime(Date realEndTime) {
        this.realEndTime = (realEndTime == null ? null : new Date(realEndTime.getTime()));
    }

    public Date getExpectedEndTime() {
        return (expectedEndTime == null ? null : new Date(expectedEndTime.getTime()));
    }

    public void setExpectedEndTime(Date expectedEndTime) {
        this.expectedEndTime = (expectedEndTime == null ? null : new Date(expectedEndTime.getTime()));
    }

    //upravit ala setter
    public Guest getGuest() {
        return guest;
    }

    //dopsat bezpecne nastaveni - A.K.A Date
    public void setGuest(Guest guest) {
        this.guest = guest;
    }

    public BigDecimal getServicesSpendings() {
        return servicesSpendings;
    }

    public void setServicesSpendings(BigDecimal servicesSpendings) {
        this.servicesSpendings = (servicesSpendings == null ? null : servicesSpendings.setScale(2, RoundingMode.HALF_EVEN));
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Reservation other = (Reservation) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }
    
    @Override
    public String toString() {
        return "Reservation[" + "id:" + id + 
                ", room:" + room +
                ", start time:" + startTime + 
                ", expected end time:" + expectedEndTime +
                ", real end time:" + realEndTime +
                ", guest:" + guest +
                ", services spendings:" + servicesSpendings + ']';
    }
    
}
