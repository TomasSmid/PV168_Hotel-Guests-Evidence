/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.muni.fi.pv168.hotelmanager.backend;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import javax.sql.DataSource;

/**
 *
 * @author Tom
 */
public class ReservationManagerImpl implements ReservationManager{
    
    private final DataSource dataSource;
    
    public ReservationManagerImpl(DataSource dataSource){
        this.dataSource = dataSource;
    }
    
    @Override
    public void createReservation(Reservation reservation) {
        if (reservation == null) {
            throw new IllegalArgumentException("Creating reservation failure: reservation is null");
        }
        
        if (reservation.getId() != null) {
            throw new IllegalArgumentException("Creating reservation failure: reservation id is already set");
        }
        
        if (reservation.getRoom() == null) {
            throw new IllegalArgumentException("Creating reservation failure: reservation room is null");
        }
               
        if (reservation.getStartTime() == null) {
            throw new IllegalArgumentException("Creating reservation failure: reservation start time is null");
        }
        
        if (reservation.getExpectedEndTime() == null) {
            throw new IllegalArgumentException("Creating reservation failure: reservation expected end time is null");
        }
        
        if (reservation.getExpectedEndTime().before(reservation.getStartTime())) {
            throw new IllegalArgumentException("Creating reservation failure: reservation expected end time is before start time null");
        }
        
        if (reservation.getGuest() == null) {
            throw new IllegalArgumentException("Creating reservation failure: reservation guest is null");
        }
        
        if (reservation.getServicesSpendings() == null) {
            throw new IllegalArgumentException("Creating reservation failure: reservation services spendings is null");
        }
        
        if (reservation.getServicesSpendings().compareTo(BigDecimal.ZERO) != 0) {
            throw new IllegalArgumentException("Creating reservation failure: reservation services spendings is not zero");
        }
        
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement(
                        "INSERT INTO RESERVATION (room_id, guest_id, start_time, real_end_time, expected_end_time) VALUES (?,?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                st.setLong(1, reservation.getRoom().getId());
                st.setLong(2, reservation.getGuest().getId());
                st.setTimestamp(3, dateToTimestamp(reservation.getStartTime()));
                st.setTimestamp(4, dateToTimestamp(reservation.getRealEndTime()));
                st.setTimestamp(5, dateToTimestamp(reservation.getExpectedEndTime()));
                int addedRows = st.executeUpdate();
                if (addedRows != 1) {
                    throw new ServiceFailureException("Internal Error: More rows inserted when trying to insert reservation " + reservation);
                }
                ResultSet keyRS = st.getGeneratedKeys();
                reservation.setId(getKey(keyRS, reservation));
            }
        } catch (SQLException ex) {
            //log.error("db connection problem", ex);
            throw new ServiceFailureException("Creating reservation failure: Error when retrieving all reservations", ex);
        }
    }
    // UNDER CONSTRUCTION
    @Override
    public Reservation getReservationById(Long id) {
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement(
                    "SELECT id,room_id, guest_id, start_time, real_end_time, expected_end_time FROM RESERVATION WHERE id = ?")) {
                st.setLong(1, id);
                ResultSet rs = st.executeQuery();
                if (rs.next()) {
                    Reservation res = resultSetToReservation(rs);
                    if (rs.next()) {
                        throw new ServiceFailureException(
                                "Internal error: More entities with the same id found "
                                        + "(source id: " + id + ", found " + res + " and " + resultSetToReservation(rs));
                    }
                    return res;
                } else {
                    return null;
                }
            }
        } catch (SQLException ex) {
            //log.error("db connection problem", ex);
            throw new ServiceFailureException("Error when retrieving all reservations", ex);
        }
    }

    @Override
    public List<Reservation> findAllReservations() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateReservation(Reservation reservation) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void deleteReservation(Reservation reservation) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Reservation> findReservationsForGuest(Guest guest) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Reservation> findReservationsForRoom(Room room) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isRoomAvailable(Room room) {
        /*checkRoomIsValid(room,"Unquiring room for availability ");
        checkDataSource();
        
        try(Connection connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT ")){
            
        }*/
        return false;
    }

    @Override
    public BigDecimal getReservationPrice(Reservation reservation) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Room> findAllUnoccupiedRooms(Date from, Date to) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Map<BigDecimal, Guest> findTopFiveSpenders() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private Timestamp dateToTimestamp(Date date){
        if (date == null) {
            return null;
        }
        
        return (date instanceof Timestamp ? (Timestamp) date : new Timestamp(date.getTime()));
    }
    
    private Long getKey(ResultSet keyRS, Reservation reservation) throws ServiceFailureException, SQLException {
        if (keyRS.next()) {
            if (keyRS.getMetaData().getColumnCount() != 1) {
                throw new ServiceFailureException("Internal Error: Generated key"
                        + "retriving failed when trying to insert reservation " + reservation
                        + " - wrong key fields count: " + keyRS.getMetaData().getColumnCount());
            }
            Long result = keyRS.getLong(1);
            if (keyRS.next()) {
                throw new ServiceFailureException("Internal Error: Generated key"
                        + "retriving failed when trying to insert reservation " + reservation
                        + " - more keys found");
            }
            return result;
        } else {
            throw new ServiceFailureException("Internal Error: Generated key"
                    + "retriving failed when trying to insert reservation " + reservation
                    + " - no key found");
        }
    }
    
    private Reservation resultSetToReservation(ResultSet rs) throws SQLException {
        Reservation res = new Reservation();
        GuestManager gm = new GuestManagerImpl(dataSource);
        RoomManager rm = new RoomManagerImpl(dataSource);
        
        res.setId(rs.getLong("id"));
        res.setRoom(rm.getRoomById(rs.getLong("room_id")));
        res.setGuest(gm.getGuestById(rs.getLong("guest_id")));
        res.setStartTime(rs.getTimestamp("start_time"));
        res.setRealEndTime(rs.getTimestamp("real_end_time"));
        res.setExpectedEndTime(rs.getTimestamp("expected_end_time"));
        
        return res;
    }
    
    private void checkDataSource() {
        if (dataSource == null) {
            throw new IllegalStateException("DataSource is not set");
        }
    }
    
    private void checkRoomIsValid(Room room, String partOfErrMsg){
        if(room == null){
            throw new IllegalArgumentException(partOfErrMsg + "failure: Room is null.");
        }
                
        if (room.getId() == null) {
            throw new IllegalArgumentException(partOfErrMsg + "failure: ID of room " + room + " is not set (is null).");
        }        
        
        if (room.getCapacity() <= 0) {
            throw new IllegalArgumentException(partOfErrMsg + "failure: Room " + room + " has 0 or negative capacity.");
        }
        
        if (room.getPrice() == null) {
            throw new IllegalArgumentException(partOfErrMsg + "failure: Price of room " + room + " is not set (is null).");
        }
        
        if (room.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(partOfErrMsg + "failure: Price of room " + room + " is 0 or negative.");
        }
        
        if (room.getFloor() <= 0) {
            throw new IllegalArgumentException(partOfErrMsg + "failure: Floor of room " + room + " is 0 or negative.");
        }
        
        if (room.getNumber() == null) {
            throw new IllegalArgumentException(partOfErrMsg + "failure: Number of room " + room + " is not set (is null).");
        }
        
        if (!room.getNumber().matches("[1-9][0-9][0-9]") ) {
            throw new IllegalArgumentException(partOfErrMsg + "failure: Wrong form of room number.");
        }
        
        if (room.getType() == null) {
            throw new IllegalArgumentException(partOfErrMsg + "failure: Room type is not set (is null).");
        }
    }
    
    private void checkGuestIsValid(Guest guest, String partOfErrMsg){
        if(guest == null){
            throw new IllegalArgumentException(partOfErrMsg + "failure: Guest is null.");
        }
        
        if(guest.getId() == null){
            throw new IllegalArgumentException(partOfErrMsg + "failure: Guest " + guest + " has a null id.");
        }
        
        if(guest.getName() == null){
            throw new IllegalArgumentException(partOfErrMsg + "failure: Name of guest " + guest + " is null.");
        }
        
        if (!Pattern.matches("[A-Z][a-zA-Z]*([ |\\-][A-Z][a-zA-Z]*)*", guest.getName())){
            throw new IllegalArgumentException(partOfErrMsg + "failure: Name of guest " + guest + " has a wrong form.");
        }
        
        if(guest.getPhone() != null){
            if(guest.getName().isEmpty()){
                throw new IllegalArgumentException(partOfErrMsg + "failure: Phone number of guest " + guest + " is empty.");
            }        
        
            if(!((Pattern.matches("([\\(][\\+][0-9]{3}[\\)][ ])?[0-9]{3}[ ][0-9]{3}[ ][0-9]{3}", guest.getPhone()))
                    || (Pattern.matches("([\\(][\\+][0-9]{3}[\\)][\\-])?[0-9]{3}[\\-][0-9]{3}[\\-][0-9]{3}", guest.getPhone())))){
                throw new IllegalArgumentException(partOfErrMsg + "failure: "
                        + "Phone number of guest " + guest + " has a wrong form.");
            }
        }
        
        if(guest.getIdCardNum() == null || guest.getIdCardNum().isEmpty()){
            throw new IllegalArgumentException(partOfErrMsg + "failure: "
                    + "Identification card number of guest " + guest + " is null or empty.");
        }
        
        if(!Pattern.matches("[0-9]{9}", guest.getIdCardNum())){
            throw new IllegalArgumentException(partOfErrMsg + "failure: "
                    + " Identification card number of guest " + guest + " has a wrong form.");
        }
        
        if(guest.getBorn() == null){
            throw new IllegalArgumentException(partOfErrMsg + "failure: "
                    + "Date of birth of guest " + guest + " is null.");
        }
    }
    
    private void checkReservationIsValid(Reservation reservation, boolean idShouldBeNull, String partOfErrMsg){
        if (reservation == null) {
            throw new IllegalArgumentException(partOfErrMsg + "failure: Reservation is null.");
        }
        
        if(idShouldBeNull){
            if (reservation.getId() != null) {
                throw new IllegalArgumentException(partOfErrMsg + "failure: ID of reservation" + reservation + " is preset.");
            }
        }else{
            if (reservation.getId() == null) {
                throw new IllegalArgumentException(partOfErrMsg + "failure: ID of reservation" + reservation + " is not set (is null).");
            }
        }
        
        checkRoomIsValid(reservation.getRoom(),partOfErrMsg);
        checkGuestIsValid(reservation.getGuest(),partOfErrMsg);
               
        if (reservation.getStartTime() == null) {
            throw new IllegalArgumentException(partOfErrMsg + "failure: Start time of reservation " + reservation + " is null.");
        }
        
        if (reservation.getExpectedEndTime() == null) {
            throw new IllegalArgumentException(partOfErrMsg + "failure: Expected end time of reservation " + reservation + " is null.");
        }
        
        if (reservation.getExpectedEndTime().before(reservation.getStartTime())) {
            throw new IllegalArgumentException(partOfErrMsg + "failure: Expected time of reservation " + reservation 
                    + " is before its start time.");
        }
        
        if (reservation.getServicesSpendings() == null) {
            throw new IllegalArgumentException(partOfErrMsg + "failure: Services spendings of reservation " + reservation
                        + " is null.");
        }
    }
    
}
