/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv168.hotelmanager.backend;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;



/**
 * 
 * 
 * @author Ondrej Smelko
 */
public class RoomManagerImpl implements RoomManager {

    private DataSource dataSource;
    
    public void setDataSource(DataSource ds){
        this.dataSource = ds;
    }
    
    
    @Override
    public void createRoom(Room room) throws ServiceFailureException {
        if (room == null) {
            throw new IllegalArgumentException("Creating room failure: room is null");
        }
        
        if (room.getId() != null) {
            throw new IllegalArgumentException("Creating room failure: room id is already set");
        }
        
        if (room.getCapacity() <= 0) {
            throw new IllegalArgumentException("Creating room failure: room has 0 or negative capacity");
        }
        
        if (room.getPrice() == null) {
            throw new IllegalArgumentException("Creating room failure: room price is null");
        }
        
        if (room.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Creating room failure: room price is 0 or negative number");
        }
        
        if (room.getFloor() <= 0) {
            throw new IllegalArgumentException("Creating room failure: room floor is 0  or negative number");
        }
        
        if (room.getNumber() == null) {
            throw new IllegalArgumentException("Creating room failure: room number is null");
        }
        
        if (!room.getNumber().matches("[1-9][0-9][0-9]") ) {
            throw new IllegalArgumentException("Creating room failure: room number is in wrong format");
        }
        
        if (room.getType() == null) {
            throw new IllegalArgumentException("Creating room failure: room type is null");
        }
        
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement(
                        "INSERT INTO ROOM (capacity,price,floor,number,room_type) VALUES (?,?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                st.setInt(1, room.getCapacity());
                st.setBigDecimal(2, room.getPrice());
                st.setInt(3, room.getFloor());
                st.setString(4, room.getNumber());
                st.setString(5, room.getType().name());
                int addedRows = st.executeUpdate();
                if (addedRows != 1) {
                    throw new ServiceFailureException("Internal Error: More rows inserted when trying to insert room " + room);
                }
                ResultSet keyRS = st.getGeneratedKeys();
                room.setId(getKey(keyRS, room));
            }
        } catch (SQLException ex) {
            //log.error("db connection problem", ex);
            throw new ServiceFailureException("Creating room failure: Error when retrieving all room", ex);
        }
    }
    
    private Long getKey(ResultSet keyRS, Room room) throws ServiceFailureException, SQLException {
        if (keyRS.next()) {
            if (keyRS.getMetaData().getColumnCount() != 1) {
                throw new ServiceFailureException("Internal Error: Generated key"
                        + "retriving failed when trying to insert room " + room
                        + " - wrong key fields count: " + keyRS.getMetaData().getColumnCount());
            }
            Long result = keyRS.getLong(1);
            if (keyRS.next()) {
                throw new ServiceFailureException("Internal Error: Generated key"
                        + "retriving failed when trying to insert room " + room
                        + " - more keys found");
            }
            return result;
        } else {
            throw new ServiceFailureException("Internal Error: Generated key"
                    + "retriving failed when trying to insert room " + room
                    + " - no key found");
        }
    }

    @Override
    public Room getRoomById(Long id) throws ServiceFailureException {
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement(
                    "SELECT id,capacity,price,floor,number,room_type FROM ROOM WHERE id = ?")) {
                st.setLong(1, id);
                ResultSet rs = st.executeQuery();
                if (rs.next()) {
                    Room room = resultSetToRoom(rs);
                    if (rs.next()) {
                        throw new ServiceFailureException(
                                "Internal error: More entities with the same id found "
                                        + "(source id: " + id + ", found " + room + " and " + resultSetToRoom(rs));
                    }
                    return room;
                } else {
                    return null;
                }
            }
        } catch (SQLException ex) {
            //log.error("db connection problem", ex);
            throw new ServiceFailureException("Error when retrieving all rooms", ex);
        }
    }
    
    private Room resultSetToRoom(ResultSet rs) throws SQLException {
        Room room = new Room();
        room.setId(rs.getLong("id"));
        room.setCapacity(rs.getInt("capacity"));
        room.setPrice(rs.getBigDecimal("price"));
        room.setFloor(rs.getInt("floor"));
        room.setNumber(rs.getString("number"));
        room.setType(RoomType.valueOf(rs.getString("room_type").toUpperCase()) ); // problem s case?
        return room;
    }

    @Override
    public void updateRoom(Room room) throws ServiceFailureException {
        if (room == null) {
            throw new IllegalArgumentException("Updating room failure: room is null");
        }
        
        if (room.getId() == null) {
            throw new IllegalArgumentException("Updating room failure: room id is NULL");
        }
        
        if (room.getCapacity() <= 0) {
            throw new IllegalArgumentException("Updating room failure: room has 0 or negative capacity");
        }
        
        if (room.getPrice() == null) {
            throw new IllegalArgumentException("Updating room failure: room price is null");
        }
        
        if (room.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Updating room failure: room price is 0 or negative number");
        }
        
        if (room.getFloor() <= 0) {
            throw new IllegalArgumentException("Updating room failure: room floor is 0  or negative number");
        }
        
        if (room.getNumber() == null) {
            throw new IllegalArgumentException("Updating room failure: room number is null");
        }
        
        if (!room.getNumber().matches("[1-9][0-9][0-9]") ) {
            throw new IllegalArgumentException("Updating room failure: room number is in wrong format");
        }
        
        if (room.getType() == null) {
            throw new IllegalArgumentException("Updating room failure: room type is null");
        }
        
        try (Connection conn = dataSource.getConnection()) {
            try(PreparedStatement st = conn.prepareStatement(
                    "UPDATE ROOM SET capacity=?,price=?,floor=?,number=?,room_type=? WHERE id=?")) {
                st.setInt(1, room.getCapacity());
                st.setBigDecimal(2, room.getPrice());
                st.setInt(3, room.getFloor());
                st.setString(4, room.getNumber());
                st.setString(5, room.getType().name()); 
                st.setLong(6,room.getId());
                if(st.executeUpdate()!=1) {
                    throw new IllegalArgumentException("cannot update room "+room);
                }
            }
        } catch (SQLException ex) {
           // log.error("db connection problem", ex);
            throw new ServiceFailureException("Update room: Error when retrieving all rooms", ex);
        }
    }

    @Override
    public void deleteRoom(Room room) throws ServiceFailureException {
        if (room == null) {
            throw new IllegalArgumentException("Deleting room failure: room is null");
        }
        
        if (room.getId() == null) {
            throw new IllegalArgumentException("Deleting room failure: room id is null");
        }
        
        try (Connection conn = dataSource.getConnection()) {
            try(PreparedStatement st = conn.prepareStatement("DELETE FROM ROOM WHERE id=?")) {
                st.setLong(1,room.getId());
                if(st.executeUpdate()!=1) {
                    throw new ServiceFailureException("did not delete room with id ="+room.getId());
                }
            }
        } catch (SQLException ex) {
            //log.error("db connection problem", ex);
            throw new ServiceFailureException("Error when retrieving all rooms", ex);
        }
    }

    @Override
    public List<Room> findAllRooms() throws ServiceFailureException {
       // log.debug("finding all graves");
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement(
                    "SELECT id,capacity,price,floor,number,room_type FROM ROOM")) {
                ResultSet rs = st.executeQuery();
                List<Room> result = new ArrayList<>();
                while (rs.next()) {
                    result.add(resultSetToRoom(rs));
                }
                return result;
            }
        } catch (SQLException ex) {
           // log.error("db connection problem", ex);
            throw new ServiceFailureException("Error when retrieving all rooms", ex);
        }
    }

}
