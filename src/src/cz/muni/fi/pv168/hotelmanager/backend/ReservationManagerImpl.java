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
import javax.sql.DataSource;

/**
 *
 * @author Tom
 */
public class ReservationManagerImpl implements ReservationManager{
    
    private DataSource dataSource;
    
    public void setDataSource(DataSource ds){
        this.dataSource = ds;
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
        GuestManager gm = new GuestManagerImpl();
        RoomManager rm = new RoomManagerImpl();
        
        res.setId(rs.getLong("id"));
        res.setRoom(rm.getRoomById(rs.getLong("room_id")));
        res.setGuest(gm.getGuestById(rs.getLong("guest_id")));
        res.setStartTime(rs.getTimestamp("start_time"));
        res.setRealEndTime(rs.getTimestamp("real_end_time"));
        res.setExpectedEndTime(rs.getTimestamp("expected_end_time"));
        
        return res;
    }
    
}
