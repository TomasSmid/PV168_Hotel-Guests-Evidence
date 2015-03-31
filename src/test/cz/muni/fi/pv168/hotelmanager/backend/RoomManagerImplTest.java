/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv168.hotelmanager.backend;

import cz.muni.fi.pv168.common.DBUtils;
import static cz.muni.fi.pv168.hotelmanager.backend.RoomType.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Resource;
import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

/**
 *
 * @author Ondrej Smelko
 */
public class RoomManagerImplTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();
    
    private static DataSource prepareDataSource(){
        BasicDataSource bds = new BasicDataSource();
        bds.setUrl("jdbc:derby:memory:SSHotelGuestEvidenceDB;create=true");
        return bds;
    }
    
    private RoomManagerImpl manager;
    private DataSource dataSource;
    
    @Before
    public void setUp() throws SQLException {
        dataSource = prepareDataSource();
        DBUtils.executeSqlScript(dataSource,RoomManager.class.getResourceAsStream("createTables.sql"));
        manager = new RoomManagerImpl(dataSource);
    }
    
    @After
    public void tearDown() throws SQLException{
        DBUtils.executeSqlScript(dataSource,RoomManager.class.getResourceAsStream("dropTables.sql"));
    }
 
    @Test
    public void createRoom() {
              
        Room room = new RoomBuilder().build();

        manager.createRoom(room);

        Long roomId = room.getId();
        assertNotNull(roomId);
        Room result = manager.getRoomById(roomId);
        assertEquals(room, result);
        assertNotSame(room, result);
        assertDeepEquals(room, result);
    }

    @Test
    public void getRoom() {

        assertNull(manager.getRoomById(1l));

        Room room = new RoomBuilder().build();

        manager.createRoom(room);
        Long roomId = room.getId();

        Room result = manager.getRoomById(roomId);
        assertEquals(room, result);
        assertDeepEquals(room, result);
    }

    @Test
    public void getAllRooms() {

        assertTrue(manager.findAllRooms().isEmpty());

        Room r1 = new RoomBuilder().build();
        Room r2 = new RoomBuilder().capacity(2).price(new BigDecimal(3000.00))
                                   .floor(3).number("301").type(FAMILY).build();

        manager.createRoom(r1);
        manager.createRoom(r2);

        List<Room> expected = Arrays.asList(r1, r2);
        List<Room> actual = manager.findAllRooms();

        Collections.sort(actual, idComparator);
        Collections.sort(expected, idComparator);

        assertEquals(expected, actual);
        assertDeepEquals(expected, actual);
    }

    @Test
    public void createRoomWithNullAttribute() {

        exception.expect(IllegalArgumentException.class);
        manager.createRoom(null);
    }

    @Test
    public void addRoomWithInvalidId() {
        Room room = new RoomBuilder().build();
        room.setId(1l);
        exception.expect(IllegalArgumentException.class);
        manager.createRoom(room);
    }

    @Test
    public void addRoomWithZeroCapacity() {
        // capacity test
        Room room = new RoomBuilder().capacity(0).build();
        exception.expect(IllegalArgumentException.class);
        manager.createRoom(room);
    }

    @Test
    public void addRoomWithNegativeCapacity() {
        // capacity test
        Room room = new RoomBuilder().capacity(-1).build();
        exception.expect(IllegalArgumentException.class);
        manager.createRoom(room);
    }

    @Test
    public void addRoomWithZeroPrice() {
        // price test
        Room room = new RoomBuilder().price(new BigDecimal(0.00)).build();
        exception.expect(IllegalArgumentException.class);
        manager.createRoom(room);
    }

    @Test
    public void addRoomWithNegativeDecimalPrice() {
        // price test
        Room room = new RoomBuilder().price(new BigDecimal(-0.01)).build();
        exception.expect(IllegalArgumentException.class);
        manager.createRoom(room);
    }

    @Test
    public void addRoomWithNegativePrice() {
        // price test
        Room room = new RoomBuilder().price(new BigDecimal(-1.00)).build();
        exception.expect(IllegalArgumentException.class);
        manager.createRoom(room);
    }

    @Test
    public void addRoomWithNullPrice() {
        // price test
        Room room = new RoomBuilder().price(null).build();
        exception.expect(IllegalArgumentException.class);
        manager.createRoom(room);
    }

    @Test
    public void addRoomWithZeroFloor() {
        // floor test
        Room room = new RoomBuilder().floor(0).build();
        exception.expect(IllegalArgumentException.class);
        manager.createRoom(room);
    }

    @Test
    public void addRoomWithNegativeFloor() {
        // floor test
        Room room = new RoomBuilder().floor(-1).build();
        exception.expect(IllegalArgumentException.class);
        manager.createRoom(room);
    }

    @Test
    public void addRoomWithNullType() {
        // type test
        Room room = new RoomBuilder().type(null).build();
        exception.expect(IllegalArgumentException.class);
        manager.createRoom(room);

    }

    @Test
    public void addRoomWithNullNumber() {
        // number(string) test
        Room room = new RoomBuilder().number(null).build();
        exception.expect(IllegalArgumentException.class);
        manager.createRoom(room);

    }

    @Test
    public void addRoomWithStartLetterInNumber() {
        // number(string) test
        Room room = new RoomBuilder().number("a501").build();
        exception.expect(IllegalArgumentException.class);
        manager.createRoom(room);

    }

    @Test
    public void addRoomWithEndLetterInNumber() {
        // number(string) test
        Room room = new RoomBuilder().number("501a").build();
        exception.expect(IllegalArgumentException.class);
        manager.createRoom(room);

    }

    @Test
    public void addRoomWithWhiteSpaceInNumber() {
        // number(string) test
        Room room = new RoomBuilder().number(" 501").build();
        exception.expect(IllegalArgumentException.class);
        manager.createRoom(room);

    }

    public void addRoomWithDotInNumber() {
        // number(string) test
        Room room = new RoomBuilder().number("5.01").build();
        exception.expect(IllegalArgumentException.class);
        manager.createRoom(room);

    }

    public void addRoomWithComaInNumber() {
        // number(string) test
        Room room = new RoomBuilder().number("5,01").build();
        exception.expect(IllegalArgumentException.class);
        manager.createRoom(room);

    }

    public void addRoomWithEmptyNumber() {
        // number(string) test
        Room room = new RoomBuilder().number("").build();
        exception.expect(IllegalArgumentException.class);
        manager.createRoom(room);

    }

    @Test
    public void updateRoomCapacity() {
        Room r1 = new RoomBuilder().build();
        Room r2 = new RoomBuilder().capacity(2).price(new BigDecimal(3000.00))
                                   .floor(3).number("301").type(FAMILY).build();
        manager.createRoom(r1);
        manager.createRoom(r2);

        Long roomId1 = r1.getId();

        r1 = manager.getRoomById(roomId1);
        r1.setCapacity(3);
        manager.updateRoom(r1);
        assertEquals(3, r1.getCapacity());
        assertEquals(new BigDecimal(1500.00), r1.getPrice());
        assertEquals(5, r1.getFloor());
        assertEquals(STANDARD, r1.getType());
        assertEquals("501", r1.getNumber());
        
        // Check if updates didn't affected other records
        assertDeepEquals(r2, manager.getRoomById(r2.getId()));
    }
    
    @Test
    public void updateRoomPrice() {
        Room r1 = new RoomBuilder().build();
        Room r2 = new RoomBuilder().capacity(2).price(new BigDecimal(3000.00))
                                   .floor(3).number("301").type(FAMILY).build();
        manager.createRoom(r1);
        manager.createRoom(r2);

        Long roomId1 = r1.getId();
        
        r1 = manager.getRoomById(roomId1);
        BigDecimal bd3 = new BigDecimal(6000.00);
        r1.setPrice(bd3);
        manager.updateRoom(r1);
        assertEquals(1, r1.getCapacity());
        assertEquals(bd3, r1.getPrice());
        assertEquals(5, r1.getFloor());
        assertEquals(STANDARD, r1.getType());
        assertEquals("501", r1.getNumber());
        
        // Check if updates didn't affected other records
        assertDeepEquals(r2, manager.getRoomById(r2.getId()));
    }
    
    @Test
    public void updateRoomFloor() {
        Room r1 = new RoomBuilder().build();
        Room r2 = new RoomBuilder().capacity(2).price(new BigDecimal(3000.00))
                                   .floor(3).number("301").type(FAMILY).build();
        manager.createRoom(r1);
        manager.createRoom(r2);

        Long roomId1 = r1.getId();
        
        r1 = manager.getRoomById(roomId1);
        r1.setFloor(1);
        manager.updateRoom(r1);
        assertEquals(1, r1.getCapacity());
        assertEquals(new BigDecimal(1500.00), r1.getPrice());
        assertEquals(1, r1.getFloor());
        assertEquals(STANDARD, r1.getType());
        assertEquals("501", r1.getNumber());
        
        // Check if updates didn't affected other records
        assertDeepEquals(r2, manager.getRoomById(r2.getId()));    
    }
    
    @Test
    public void updateRoomType() {
        Room r1 = new RoomBuilder().build();
        Room r2 = new RoomBuilder().capacity(2).price(new BigDecimal(3000.00))
                                   .floor(3).number("301").type(FAMILY).build();
        manager.createRoom(r1);
        manager.createRoom(r2);

        Long roomId1 = r1.getId();
        
        r1 = manager.getRoomById(roomId1);
        r1.setType(SUITE);
        manager.updateRoom(r1);
        assertEquals(1, r1.getCapacity());
        assertEquals(new BigDecimal(1500.00), r1.getPrice());
        assertEquals(5, r1.getFloor());
        assertEquals(SUITE, r1.getType());
        assertEquals("501", r1.getNumber());
        
        // Check if updates didn't affected other records
        assertDeepEquals(r2, manager.getRoomById(r2.getId()));  
    }
    
    @Test
    public void updateRoomNumber() {
        Room r1 = new RoomBuilder().build();
        Room r2 = new RoomBuilder().capacity(2).price(new BigDecimal(3000.00))
                                   .floor(3).number("301").type(FAMILY).build();
        manager.createRoom(r1);
        manager.createRoom(r2);

        Long roomId1 = r1.getId();
        
        r1 = manager.getRoomById(roomId1);
        r1.setNumber("101");
        manager.updateRoom(r1);
        assertEquals(1, r1.getCapacity());
        assertEquals(new BigDecimal(1500.00), r1.getPrice());
        assertEquals(5, r1.getFloor());
        assertEquals(STANDARD, r1.getType());
        assertEquals("101", r1.getNumber());
        
        // Check if updates didn't affected other records
        assertDeepEquals(r2, manager.getRoomById(r2.getId()));  
    }

    @Test
    public void updateRoomWithNullAttribute() {
        exception.expect(IllegalArgumentException.class);
        manager.updateRoom(null);

    }

    @Test
    public void updateRoomWithWrongId() {

        Room room = new RoomBuilder().build();
        manager.createRoom(room);
        Long roomId = room.getId();

        room = manager.getRoomById(roomId);
        room.setId(null);
        exception.expect(IllegalArgumentException.class);
        manager.updateRoom(room);
    }

    @Test
    public void updateRoomWithDecrementedId() {

        Room room = new RoomBuilder().build();
        manager.createRoom(room);
        Long roomId = room.getId();

        room = manager.getRoomById(roomId);
        room.setId(roomId - 1);
        exception.expect(IllegalArgumentException.class);
        manager.updateRoom(room);

    }

    @Test
    public void updateRoomWithIncrementedId() {

        Room room = new RoomBuilder().build();
        manager.createRoom(room);
        Long roomId = room.getId();

        room = manager.getRoomById(roomId);
        room.setId(roomId + 1);
        exception.expect(IllegalArgumentException.class);
        manager.updateRoom(room);

    }

    @Test
    public void updateRoomWithZeroCapacity() {

        Room room = new RoomBuilder().build();
        manager.createRoom(room);
        Long roomId = room.getId();

        // capacity test
        room = manager.getRoomById(roomId);
        room.setCapacity(0);
        exception.expect(IllegalArgumentException.class);
        manager.updateRoom(room);

    }

    @Test
    public void updateRoomWithNegativeCapacity() {

        Room room = new RoomBuilder().build();
        manager.createRoom(room);
        Long roomId = room.getId();

        // capacity test
        room = manager.getRoomById(roomId);
        room.setCapacity(-1);
        exception.expect(IllegalArgumentException.class);
        manager.updateRoom(room);

    }

    @Test
    public void updateRoomWithZeroPrice() {

        Room room = new RoomBuilder().build();
        manager.createRoom(room);
        Long roomId = room.getId();

        // price test
        room = manager.getRoomById(roomId);
        BigDecimal bd1 = new BigDecimal(0.00);
        room.setPrice(bd1);
        exception.expect(IllegalArgumentException.class);
        manager.updateRoom(room);

    }

    @Test
    public void updateRoomWithNegativeDecimalPrice() {

        Room room = new RoomBuilder().build();
        manager.createRoom(room);
        Long roomId = room.getId();

        // price test
        room = manager.getRoomById(roomId);
        BigDecimal bd1 = new BigDecimal(-0.01);
        room.setPrice(bd1);
        exception.expect(IllegalArgumentException.class);
        manager.updateRoom(room);

    }

    @Test
    public void updateRoomWithNegativePrice() {

        Room room = new RoomBuilder().build();
        manager.createRoom(room);
        Long roomId = room.getId();

        // price test
        room = manager.getRoomById(roomId);
        BigDecimal bd1 = new BigDecimal(-1.00);
        room.setPrice(bd1);
        exception.expect(IllegalArgumentException.class);
        manager.updateRoom(room);

    }

    @Test
    public void updateRoomWithNullPrice() {

        Room room = new RoomBuilder().build();
        manager.createRoom(room);
        Long roomId = room.getId();

        // price test
        room = manager.getRoomById(roomId);
        room.setPrice(null);
        exception.expect(IllegalArgumentException.class);
        manager.updateRoom(room);

    }

    @Test
    public void updateRoomWithZeroFloor() {

        Room room = new RoomBuilder().build();
        manager.createRoom(room);
        Long roomId = room.getId();

        // floor test
        room = manager.getRoomById(roomId);
        room.setFloor(0);
        exception.expect(IllegalArgumentException.class);
        manager.updateRoom(room);

    }

    @Test
    public void updateRoomWithNegativeFloor() {

        Room room = new RoomBuilder().build();
        manager.createRoom(room);
        Long roomId = room.getId();

        // floor test
        room = manager.getRoomById(roomId);
        room.setFloor(-1);
        exception.expect(IllegalArgumentException.class);
        manager.updateRoom(room);

    }

    @Test
    public void updateRoomWithNullType() {

        Room room = new RoomBuilder().build();
        manager.createRoom(room);
        Long roomId = room.getId();

        // type test
        room = manager.getRoomById(roomId);
        room.setType(null);
        exception.expect(IllegalArgumentException.class);
        manager.updateRoom(room);

    }

    @Test
    public void updateRoomWithStartLetterInNumber() {

        Room room = new RoomBuilder().build();
        manager.createRoom(room);
        Long roomId = room.getId();

        // number(string) test
        room = manager.getRoomById(roomId);
        room.setNumber("a501");
        exception.expect(IllegalArgumentException.class);
        manager.updateRoom(room);

    }

    @Test
    public void updateRoomWithEndLetterInNumber() {

        Room room = new RoomBuilder().build();
        manager.createRoom(room);
        Long roomId = room.getId();

        // number(string) test
        room = manager.getRoomById(roomId);
        room.setNumber("501a");
        exception.expect(IllegalArgumentException.class);
        manager.updateRoom(room);

    }

    @Test
    public void updateRoomWithDotInNumber() {

        Room room = new RoomBuilder().build();
        manager.createRoom(room);
        Long roomId = room.getId();

        // number(string) test
        room = manager.getRoomById(roomId);
        room.setNumber("5.01");
        exception.expect(IllegalArgumentException.class);
        manager.updateRoom(room);

    }

    @Test
    public void updateRoomWithComaInNumber() {

        Room room = new RoomBuilder().build();
        manager.createRoom(room);
        Long roomId = room.getId();

        // number(string) test
        room = manager.getRoomById(roomId);
        room.setNumber("5,01");
        exception.expect(IllegalArgumentException.class);
        manager.updateRoom(room);

    }

    @Test
    public void updateRoomWithWhiteSpaceNumber() {

        Room room = new RoomBuilder().build();
        manager.createRoom(room);
        Long roomId = room.getId();

        // number(string) test
        room = manager.getRoomById(roomId);
        room.setNumber(" 501");
        exception.expect(IllegalArgumentException.class);
        manager.updateRoom(room);

    }

    @Test
    public void updateRoomWithEmptyNumber() {

        Room room = new RoomBuilder().build();
        manager.createRoom(room);
        Long roomId = room.getId();

        // number(string) test
        room = manager.getRoomById(roomId);
        room.setNumber("");
        exception.expect(IllegalArgumentException.class);
        manager.updateRoom(room);

    }

    @Test
    public void updateRoomWithNullNumber() {

        Room room = new RoomBuilder().build();
        manager.createRoom(room);
        Long roomId = room.getId();

        // number(string) test
        room = manager.getRoomById(roomId);
        room.setNumber(null);
        exception.expect(IllegalArgumentException.class);
        manager.updateRoom(room);

    }

    @Test
    public void deleteRoom() {

        Room r1 = new RoomBuilder().build();
        Room r2 = new RoomBuilder().capacity(2).price(new BigDecimal(3000.00))
                                   .floor(3).number("301").type(FAMILY).build();
        manager.createRoom(r1);
        manager.createRoom(r2);

        assertNotNull(manager.getRoomById(r1.getId()));
        assertNotNull(manager.getRoomById(r2.getId()));

        manager.deleteRoom(r1);

        assertNull(manager.getRoomById(r1.getId()));
        assertNotNull(manager.getRoomById(r2.getId()));

    }

    @Test
    public void deleteRoomWithNullAttribute() {
        exception.expect(IllegalArgumentException.class);
        manager.deleteRoom(null);

    }

    @Test
    public void deleteRoomWithNullId() {
        Room room = new RoomBuilder().build();

        room.setId(null);
        exception.expect(IllegalArgumentException.class);
        manager.deleteRoom(room);

    }

    @Test
    public void deleteRoomWithWrongId() {
        Room room = new RoomBuilder().build();

        room.setId(1l);
        exception.expect(ServiceFailureException.class);
        manager.deleteRoom(room);

    }

    private void assertDeepEquals(Room expected, Room actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getCapacity(), actual.getCapacity());
        assertEquals(expected.getPrice(), actual.getPrice());
        assertEquals(expected.getFloor(), actual.getFloor());
        assertEquals(expected.getType(), actual.getType());
        assertEquals(expected.getNumber(), actual.getNumber());
    }

    private void assertDeepEquals(List<Room> expectedList, List<Room> actualList) {
        for (int i = 0; i < expectedList.size(); i++) {
            Room expected = expectedList.get(i);
            Room actual = actualList.get(i);
            assertDeepEquals(expected, actual);
        }
    }

    private static Comparator<Room> idComparator = new Comparator<Room>() {

        @Override
        public int compare(Room o1, Room o2) {
            return Long.valueOf(o1.getId()).compareTo(Long.valueOf(o2.getId()));
        }
    };

    class RoomBuilder {

        private int capacity = 1;
        private BigDecimal price = new BigDecimal(1500.00);
        private int floor = 5;
        private String number = "501";
        private RoomType type = STANDARD;

        public RoomBuilder() {

        }

        public RoomBuilder capacity(int capacity) {
            this.capacity = capacity;
            return this;
        }

        public RoomBuilder price(BigDecimal price) {
            this.price = price;
            return this;
        }

        public RoomBuilder floor(int floor) {
            this.floor = floor;
            return this;
        }

        public RoomBuilder number(String number) {
            this.number = number;
            return this;
        }

        public RoomBuilder type(RoomType type) {
            this.type = type;
            return this;
        }

        public Room build() {
            Room room = new Room();
            room.setCapacity(this.capacity);
            room.setPrice(this.price);
            room.setFloor(this.floor);
            room.setNumber(this.number);
            room.setType(this.type);

            return room;
        }
    }
}
