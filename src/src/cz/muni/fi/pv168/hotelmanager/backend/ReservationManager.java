/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.muni.fi.pv168.hotelmanager.backend;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Ondrej Smelko, Tomas Smid
 */
public interface ReservationManager {
    
    public void createReservation(Reservation reservation);
    
    public Reservation getReservationById(Long id);
    
    public List<Reservation> findAllReservations();
    
    public void updateReservation(Reservation reservation);
    
    public void deleteReservation(Reservation reservation);
    
    public List<Reservation> findReservationsForGuest(Guest guest);
    
    public List<Reservation> findReservationsForRoom(Room room);
    
    public BigDecimal getReservationPrice(Reservation reservation);
    
    public List<Room> findAllUnoccupiedRooms(Date from, Date to);
    
    public List<Reservation> findTopFiveSpenders();
}
