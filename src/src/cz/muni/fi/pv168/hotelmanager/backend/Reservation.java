/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.muni.fi.pv168.hotelmanager.backend;

import java.math.BigDecimal;
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
        return new Date(startTime.getTime());
    }

    public void setStartTime(Date startTime) {
        this.startTime = new Date(startTime.getTime());
    }

    public Date getRealEndTime() {
        return new Date(realEndTime.getTime());
    }

    public void setRealEndTime(Date realEndTime) {
        this.realEndTime = new Date(realEndTime.getTime());
    }

    public Date getExpectedEndTime() {
        return new Date(expectedEndTime.getTime());
    }

    public void setExpectedEndTime(Date expectedEndTime) {
        this.expectedEndTime = new Date(expectedEndTime.getTime());
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
        this.servicesSpendings = servicesSpendings;
    }
    
    
}
