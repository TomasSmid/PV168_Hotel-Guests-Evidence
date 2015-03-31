/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.muni.fi.pv168.hotelmanager.backend;

import cz.muni.fi.pv168.common.DBUtils;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.sql.DataSource;

/**
 *
 * @author Tom
 */
public class ReservationManagerImpl implements ReservationManager{
    
    private static final Logger logger = Logger.getLogger(GuestManagerImpl.class.getName());
    private final DataSource dataSource;
    
    public ReservationManagerImpl(DataSource dataSource){
        this.dataSource = dataSource;
    }
    
    @Override
    public void createReservation(Reservation reservation) {
        checkReservationIsValid(reservation, true,"Creating reservation ");
        
        if (reservation.getServicesSpendings().compareTo(BigDecimal.ZERO) != 0) {
            throw new IllegalArgumentException("Creating reservation failure: reservation services spendings is not zero");
        }
        
        checkDataSource();
        
        try (Connection conn = dataSource.getConnection()) {
            
            conn.setAutoCommit(false); 
            try (PreparedStatement st = conn.prepareStatement(
                        "INSERT INTO RESERVATION (room_id,guest_id,start_time,real_end_time,expected_end_time,serv_spendings) VALUES (?,?,?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                st.setLong(1, reservation.getRoom().getId());
                st.setLong(2, reservation.getGuest().getId());
                st.setTimestamp(3, dateToTimestamp(reservation.getStartTime()));  
                st.setTimestamp(4, dateToTimestamp(reservation.getRealEndTime()));
                st.setTimestamp(5, dateToTimestamp(reservation.getExpectedEndTime()));
                st.setBigDecimal(6, reservation.getServicesSpendings());
                int addedRows = st.executeUpdate();
                if (addedRows != 1) {
                    throw new ServiceFailureException("Internal Error: More rows inserted when trying to insert reservation " + reservation);
                }
                ResultSet keyRS = st.getGeneratedKeys();
                reservation.setId(getKey(keyRS, reservation));
                
                conn.commit();
            }catch(SQLException ex){
                logger.log(Level.SEVERE, "Creating reservation failure: connection error when inserting reservation " + reservation, ex);
                throw new ServiceFailureException("Creating reservation failure: Error when retrieving all reservations", ex);
            }finally{
                DBUtils.doRollbackQuietly(conn);
                DBUtils.switchAutocommitBackToTrue(conn);
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Creating reservation failure: connection error when inserting reservation " + reservation, ex);
            throw new ServiceFailureException("Creating reservation failure: Error when retrieving all reservations", ex);
        }
    }

    @Override
    public Reservation getReservationById(Long id) {
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement(
                    "SELECT id,room_id, guest_id, start_time, real_end_time, expected_end_time, serv_spendings FROM RESERVATION WHERE id = ?")) {
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
            logger.log(Level.SEVERE, "Retrieving reservation failure: Error when retrieving reservation with id " + id, ex);
            throw new ServiceFailureException("Error when retrieving all reservations", ex);
        }
    }

    @Override
    public List<Reservation> findAllReservations() {
        checkDataSource();
        
        try(Connection connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT id,room_id, guest_id, start_time, real_end_time, expected_end_time, serv_spendings FROM RESERVATION")){
            
            ResultSet rs = ps.executeQuery();
            
            List<Reservation> retReservations = new ArrayList<>();
            while(rs.next()){                
                retReservations.add(resultSetToReservation(rs));
            }
            
            return retReservations;
            
        }catch(SQLException ex){
            String errMsg = "Error occured when retrieving all reservations from DB.";
            logger.log(Level.SEVERE, errMsg, ex);
            throw new ServiceFailureException(errMsg, ex);
        }
    }

    @Override
    public void updateReservation(Reservation reservation) {
        checkReservationIsValid(reservation, false,"Updating reservation ");
        
        checkDataSource();
        
        try (Connection conn = dataSource.getConnection()) {
            
            conn.setAutoCommit(false); 
            try(PreparedStatement st = conn.prepareStatement(
                    "UPDATE RESERVATION SET room_id=?,guest_id=?,start_time=?,real_end_time=?,expected_end_time=?,serv_spendings=? WHERE id=?")) {
                st.setLong(1, reservation.getRoom().getId());
                st.setLong(2, reservation.getGuest().getId());
                st.setTimestamp(3, dateToTimestamp(reservation.getStartTime()));
                st.setTimestamp(4, dateToTimestamp(reservation.getRealEndTime()));
                st.setTimestamp(5, dateToTimestamp(reservation.getExpectedEndTime()));
                st.setBigDecimal(6, reservation.getServicesSpendings());
                st.setLong(6,reservation.getId());
                if(st.executeUpdate()!=1) {
                    throw new IllegalArgumentException("cannot update reservation "+reservation);
                }                
            conn.commit();
            
            }catch(SQLException ex){
                logger.log(Level.SEVERE, "Updating reservation failure: connection error when updating reservation" + reservation, ex);
                throw new ServiceFailureException("Update reservation failure: Error when retrieving all reservations", ex);
            }finally{
                DBUtils.doRollbackQuietly(conn);
                DBUtils.switchAutocommitBackToTrue(conn);
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Updating reservation failure: connection error when updating reservation" + reservation, ex);
            throw new ServiceFailureException("Update reservation failure: Error when retrieving all reservations", ex);
        }
    }

    @Override
    public void deleteReservation(Reservation reservation) {
        if (reservation == null) {
            throw new IllegalArgumentException("Deleting reservation failure: reservation is null");
        }
        
        if (reservation.getId() == null) {
            throw new IllegalArgumentException("Deleting reservation failure: reservation id is null");
        }
        
        checkDataSource();
        
        try (Connection conn = dataSource.getConnection()) {
            
            conn.setAutoCommit(false); 
            try(PreparedStatement st = conn.prepareStatement("DELETE FROM RESERVATION WHERE id=?")) {
                st.setLong(1,reservation.getId());
                if(st.executeUpdate()!=1) {
                    throw new ServiceFailureException("did not delete reservation with id ="+reservation.getId());
                }
            conn.commit();
            
            }catch(SQLException ex){
                logger.log(Level.SEVERE, "Deleting reservation failure: connection error when deleting reservation" + reservation, ex);
                throw new ServiceFailureException("Deleting reservation failure: Error when retrieving all reservations", ex);
            }finally{
                DBUtils.doRollbackQuietly(conn);
                DBUtils.switchAutocommitBackToTrue(conn);
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Deleting reservation failure: connection error when deleting reservation" + reservation, ex);
            throw new ServiceFailureException("Deleting reservation failure: Error when retrieving all reservations", ex);
        }
    }

    @Override
    public List<Reservation> findReservationsForGuest(Guest guest) {
        checkGuestIsValid(guest,"Find reservations for guest ");
        
        checkDataSource();
        
        try(Connection connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT id,room_id, guest_id, start_time, real_end_time, expected_end_time, serv_spendings FROM RESERVATION WHERE guest_id=?")){
            ps.setLong(1,guest.getId());
                    
            ResultSet rs = ps.executeQuery();
            
            List<Reservation> retReservations = new ArrayList<>();
            while(rs.next()){                
                retReservations.add(resultSetToReservation(rs));
            }
            
            return retReservations;
            
        }catch(SQLException ex){
            String errMsg = "Error occured when retrieving all reservations from DB.";
            logger.log(Level.SEVERE, errMsg, ex);
            throw new ServiceFailureException(errMsg, ex);
        }
    }

    @Override
    public List<Reservation> findReservationsForRoom(Room room) {
        checkRoomIsValid(room,"Find reservations for room ");
        
        checkDataSource();
        
        try(Connection connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT id,room_id, guest_id, start_time, real_end_time, expected_end_time, serv_spendings FROM RESERVATION WHERE room_id=?")){
            ps.setLong(1,room.getId());
                    
            ResultSet rs = ps.executeQuery();
            
            List<Reservation> retReservations = new ArrayList<>();
            while(rs.next()){                
                retReservations.add(resultSetToReservation(rs));
            }
            
            return retReservations;
            
        }catch(SQLException ex){
            String errMsg = "Error occured when retrieving all reservations from DB.";
            logger.log(Level.SEVERE, errMsg, ex);
            throw new ServiceFailureException(errMsg, ex);
        }
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
        res.setServicesSpendings(rs.getBigDecimal("serv_spendings"));
        
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
