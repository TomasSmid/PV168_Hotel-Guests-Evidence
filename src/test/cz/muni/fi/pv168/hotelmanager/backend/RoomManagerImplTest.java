/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv168.hotelmanager.backend;


import static cz.muni.fi.pv168.hotelmanager.backend.RoomType.*;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Ondra
 */
public class RoomManagerImplTest {
    
    private RoomManagerImpl manager;
    
    @Before
    public void setUp() {
        manager = new RoomManagerImpl();
    }

    @Test
    public void createRoom() {
        BigDecimal bd = new BigDecimal(1500.00);
        Room room = newRoom(1,bd,5,STANDARD,"501");
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
        
        BigDecimal bd = new BigDecimal(1500.00);
        Room room = newRoom(1,bd,5,STANDARD,"501");
        manager.createRoom(room);
        Long roomId = room.getId();

        Room result = manager.getRoomById(roomId);
        assertEquals(room, result);
        assertDeepEquals(room, result);
    }
    
    @Test
    public void getAllRooms() {

        assertTrue(manager.findAllRooms().isEmpty());

        BigDecimal bd1 = new BigDecimal(1500.00);
        BigDecimal bd2 = new BigDecimal(3000.00);
        Room r1 = newRoom(1,bd1,5,STANDARD,"501");
        Room r2 = newRoom(2,bd2,3,FAMILY,"301");
        manager.createRoom(r1);
        manager.createRoom(r2);

        List<Room> expected = Arrays.asList(r1,r2);
        List<Room> actual = manager.findAllRooms();

        Collections.sort(actual,idComparator);
        Collections.sort(expected,idComparator);

        assertEquals(expected, actual);
        assertDeepEquals(expected, actual);
    }
    
    @Test
    public void addRoomWithWrongAttributes() {

        try {
            manager.createRoom(null);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }
        
        BigDecimal bd = new BigDecimal(1500.00);
        Room room = newRoom(1,bd,5,STANDARD,"501");
        room.setId(1l);
        try {
            manager.createRoom(room);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }
        
        // capacity test
        room = newRoom(0,bd,5,STANDARD,"501"); 
        try {
            manager.createRoom(room);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }
        
        // capacity test
        room = newRoom(-1,bd,5,STANDARD,"501"); 
        try {
            manager.createRoom(room);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }
        
        // price test
        BigDecimal bd2 = new BigDecimal(0.00);
        room = newRoom(1,bd2,5,STANDARD,"501"); 
        try {
            manager.createRoom(room);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }
        
        // price test
        bd2 = new BigDecimal(-0.01);
        room = newRoom(1,bd2,5,STANDARD,"501"); 
        try {
            manager.createRoom(room);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }
        
        // price test
        bd2 = new BigDecimal(-1.00);
        room = newRoom(1,bd2,5,STANDARD,"501"); 
        try {
            manager.createRoom(room);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }
        
        // price test
        room = newRoom(1,null,5,STANDARD,"501"); 
        try {
            manager.createRoom(room);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }

        // floor test
        room = newRoom(1,bd,0,STANDARD,"501"); 
        try {
            manager.createRoom(room);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }
        
        // floor test
        room = newRoom(1,bd,-1,STANDARD,"501"); 
        try {
            manager.createRoom(room);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }
        
        // type test
        room = newRoom(1,bd,5,null,"501"); 
        try {
            manager.createRoom(room);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }
        
        // number(string) test
        room = newRoom(1,bd,5,STANDARD,null); 
        try {
            manager.createRoom(room);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }
        
        // number(string) test
        room = newRoom(1,bd,5,STANDARD,"Pokoj"); 
        try {
            manager.createRoom(room);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }
        
        // number(string) test
        room = newRoom(1,bd,5,STANDARD,""); 
        try {
            manager.createRoom(room);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }
        
        // number(string) test
        room = newRoom(1,bd,5,STANDARD,"+,.;@:*-"); 
        try {
            manager.createRoom(room);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }

        // these variants should be ok
        room = newRoom(1,bd,5,STANDARD,"501");
        manager.createRoom(room);
        Room result = manager.getRoomById(room.getId()); 
        assertNotNull(result);

        room = newRoom(4,bd,3,FAMILY,"420");
        manager.createRoom(room);
        result = manager.getRoomById(room.getId()); 
        assertNotNull(result);

    }
    
    @Test
    public void updateRoom() {
        BigDecimal bd1 = new BigDecimal(1500.00);
        BigDecimal bd2 = new BigDecimal(3000.00);
        Room r1 = newRoom(1,bd1,5,STANDARD,"501");
        Room r2 = newRoom(2,bd2,3,FAMILY,"301");
        manager.createRoom(r1);
        manager.createRoom(r2);
        
        Long roomId1 = r1.getId();

        r1 = manager.getRoomById(roomId1);
        r1.setCapacity(3);
        manager.updateRoom(r1);        
        assertEquals(3, r1.getCapacity());
        assertEquals(bd1, r1.getPrice());
        assertEquals(5, r1.getFloor());
        assertEquals(STANDARD, r1.getType());
        assertEquals("501", r1.getNumber());
        
        r1 = manager.getRoomById(roomId1);
        BigDecimal bd3 = new BigDecimal(6000.00);
        r1.setPrice(bd3);
        manager.updateRoom(r1);        
        assertEquals(3, r1.getCapacity());
        assertEquals(bd3, r1.getPrice());
        assertEquals(5, r1.getFloor());
        assertEquals(STANDARD, r1.getType());
        assertEquals("501", r1.getNumber());

        r1 = manager.getRoomById(roomId1);
        r1.setFloor(1);
        manager.updateRoom(r1);        
        assertEquals(3, r1.getCapacity());
        assertEquals(bd3, r1.getPrice());
        assertEquals(1, r1.getFloor());
        assertEquals(STANDARD, r1.getType());
        assertEquals("501", r1.getNumber());

        r1 = manager.getRoomById(roomId1);
        r1.setType(SUITE);
        manager.updateRoom(r1);        
        assertEquals(3, r1.getCapacity());
        assertEquals(bd3, r1.getPrice());
        assertEquals(1, r1.getFloor());
        assertEquals(SUITE, r1.getType());
        assertEquals("501", r1.getNumber());

        r1 = manager.getRoomById(roomId1);
        r1.setNumber("101");
        manager.updateRoom(r1);        
        assertEquals(3, r1.getCapacity());
        assertEquals(bd3, r1.getPrice());
        assertEquals(1, r1.getFloor());
        assertEquals(SUITE, r1.getType());
        assertEquals("101", r1.getNumber());
        
        r1 = manager.getRoomById(roomId1);
        r1.setNumber(null);
        manager.updateRoom(r1);        
        assertEquals(3, r1.getCapacity());
        assertEquals(bd3, r1.getPrice());
        assertEquals(1, r1.getFloor());
        assertEquals(SUITE, r1.getType());
        assertNull(r1.getNumber());

        // Check if updates didn't affected other records
        assertDeepEquals(r2, manager.getRoomById(r2.getId()));
    }
    
    @Test
    public void updateRoomWithWrongAttributes() {

        BigDecimal bd = new BigDecimal(1500.00);
        Room room = newRoom(1,bd,5,STANDARD,"501");
        manager.createRoom(room);
        Long roomId = room.getId();
        
        try {
            manager.updateRoom(null);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }
        
        try {
            room = manager.getRoomById(roomId);
            room.setId(null);
            manager.updateRoom(room);        
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }

        try {
            room = manager.getRoomById(roomId);
            room.setId(roomId - 1);
            manager.updateRoom(room);        
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }
        
        try {
            room = manager.getRoomById(roomId);
            room.setId(roomId + 1);
            manager.updateRoom(room);        
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }
        
        // capacity test
        try {
            room = manager.getRoomById(roomId);
            room.setCapacity(0);
            manager.updateRoom(room);        
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }
        
        // capacity test
        try {
            room = manager.getRoomById(roomId);
            room.setCapacity(-1);
            manager.updateRoom(room);        
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }
        
        // price test
        try {
            room = manager.getRoomById(roomId);
            BigDecimal bd1 = new BigDecimal(0.00);
            room.setPrice(bd1);
            manager.updateRoom(room);        
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }
             
        // price test
        try {
            room = manager.getRoomById(roomId);
            BigDecimal bd1 = new BigDecimal(-0.01);
            room.setPrice(bd1);
            manager.updateRoom(room);        
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }
        
        // price test
        try {
            room = manager.getRoomById(roomId);
            BigDecimal bd1 = new BigDecimal(-1.00);
            room.setPrice(bd1);
            manager.updateRoom(room);        
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }
        
        // price test
        try {
            room = manager.getRoomById(roomId);
            room.setPrice(null);
            manager.updateRoom(room);        
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }
        
        
        // floor test
        try {
            room = manager.getRoomById(roomId);
            room.setFloor(0);
            manager.updateRoom(room);        
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }
        
        // floor test
        try {
            room = manager.getRoomById(roomId);
            room.setFloor(-1);
            manager.updateRoom(room);        
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }
        
        // type test
        try {
            room = manager.getRoomById(roomId);
            room.setType(null);
            manager.updateRoom(room);        
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }
        
        // number(string) test
        try {
            room = manager.getRoomById(roomId);
            room.setNumber("Pokoj");
            manager.updateRoom(room);        
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }
        
        // number(string) test
        try {
            room = manager.getRoomById(roomId);
            room.setNumber("");
            manager.updateRoom(room);        
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }
        
        // number(string) test
        try {
            room = manager.getRoomById(roomId);
            room.setNumber(":+;@*-,.");
            manager.updateRoom(room);        
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }
        
        // number(string) test
        try {
            room = manager.getRoomById(roomId);
            room.setNumber(null);
            manager.updateRoom(room);        
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }

    }
    
    @Test
    public void deleteRoom() {

        BigDecimal bd1 = new BigDecimal(1500.00);
        BigDecimal bd2 = new BigDecimal(3000.00);
        Room r1 = newRoom(1,bd1,5,STANDARD,"501");
        Room r2 = newRoom(2,bd2,3,STANDARD,"301");
        manager.createRoom(r1);
        manager.createRoom(r2);
        
        assertNotNull(manager.getRoomById(r1.getId()));
        assertNotNull(manager.getRoomById(r2.getId()));

        manager.deleteRoom(r1);
        
        assertNull(manager.getRoomById(r1.getId()));
        assertNotNull(manager.getRoomById(r2.getId()));
                
    }

    @Test
    public void deleteRoomWithWrongAttributes() {

        BigDecimal bd = new BigDecimal(1500.00);
        Room room = newRoom(1,bd,5,STANDARD,"501");
        
        try {
            manager.deleteRoom(null);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }

        try {
            room.setId(null);
            manager.deleteRoom(room);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }

        try {
            room.setId(1l);
            manager.deleteRoom(room);
            fail();
        } catch (IllegalArgumentException ex) {
            //OK
        }        

    }
    
    
    private static Room newRoom(int capacity, BigDecimal price, int floor, RoomType type,String number) {
        Room room = new Room();
        room.setCapacity(capacity);
        room.setPrice(price);
        room.setFloor(floor);
        room.setType(type);
        room.setNumber(number);
        return room;
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
}
