/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.muni.fi.pv168.hotelmanager.backend;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

/**
 *
 * @author Ondrej Smelko, Tomas Smid
 */
public class ReservationManagerImplTest {
    
    @Rule public ExpectedException exception = ExpectedException.none();
    
    private ReservationManagerImpl manager;
    
    @Before
    public void setUp() {
        manager = new ReservationManagerImpl();
    }

    @Test
    public void isRoomAvailableWithValidAvailableRoom(){
        Guest guest2 = newGuest("John Dragos",null,"151515151",new Date(0l));
        Guest guest3 = newGuest("Marek Mrak","505 050 654","548478789",new Date(-500_000l));
        Room room2 = newRoom(474748512L,1,new BigDecimal(500.00),2,RoomType.STANDARD,"216");
        Room room3 = newRoom(165400004L,4,new BigDecimal(2500.00),3,RoomType.FAMILY,"300");
        Reservation res1 = new ResBuilder().build();
        Reservation res2 = new ResBuilder().startTime(new Date(63_000_000_000_158l))
                                           .expectedEndTime(new Date(63_000_001_800_000l))
                                           .realEndTime(new Date(63_000_001_800_000l))
                                           .guest(guest2).room(room2).servicesSpendings(new BigDecimal(5420.50)).build();
        Reservation res3 = new ResBuilder().guest(guest3).room(room3).build();
        
        manager.createReservation(res1);
        manager.createReservation(res2);
        manager.createReservation(res3);
        
        assertTrue("Room " + room2.toString() + " should be available",manager.isRoomAvailable(room2));
    }
    
    @Test
    public void isRoomAvailableWithValidUnavailableRoom(){
        Guest guest2 = newGuest("John Dragos",null,"151515151",new Date(0l));
        Guest guest3 = newGuest("Marek Mrak","505 050 654","548478789",new Date(-500_000l));
        Room room2 = newRoom(474748512L,1,new BigDecimal(500.00),2,RoomType.STANDARD,"216");
        Room room3 = newRoom(165400004L,4,new BigDecimal(2500.00),3,RoomType.FAMILY,"300");
        Reservation res1 = new ResBuilder().build();
        Reservation res2 = new ResBuilder().startTime(new Date(63_000_000_000_158l))
                                           .expectedEndTime(new Date(63_000_001_800_000l))
                                           .realEndTime(new Date(63_000_001_800_000l))
                                           .guest(guest2).room(room2).servicesSpendings(new BigDecimal(5420.50)).build();
        Reservation res3 = new ResBuilder().guest(guest3).room(room3).build();
        
        manager.createReservation(res1);
        manager.createReservation(res2);
        manager.createReservation(res3);        
        
        assertFalse("Room " + room3.toString() + " should not be available",manager.isRoomAvailable(room3));
    }
    
    @Test
    public void isRoomAvailableWithNullRoom(){
        Reservation res1 = new ResBuilder().build();
        
        manager.createReservation(res1);       
        
        exception.expect(IllegalArgumentException.class);
        manager.isRoomAvailable(null);
    }
    
    @Test
    public void getReservationPriceWithValidExistingReservation(){
        Reservation res = new ResBuilder().build();
        BigDecimal expPrice = new BigDecimal(15_000.00);
        
        manager.createReservation(res);        
        BigDecimal actPrice = manager.getReservationPrice(res);
        
        assertEquals("Reservation price should be 15000",expPrice,actPrice);
    }
    
    @Test
    public void getReservationPriceWithValidAbsentReservation(){
        Guest guest = newGuest("John Dragos",null,"151515151",new Date(0l));
        Room room = newRoom(474748512L,1,new BigDecimal(500.00),2,RoomType.STANDARD,"216");
        Reservation res = new ResBuilder().build();        
        Reservation res2 = new ResBuilder().startTime(new Date(66_000_000_000l))
                                           .expectedEndTime(new Date(67_000_000_000l))
                                           .guest(guest).room(room).build();
        
        manager.createReservation(res);        
        BigDecimal actPrice = manager.getReservationPrice(res2);
        
        assertNull("Reservation price should be null",actPrice);
    }
    
    @Test
    public void getReservationPriceWithNullReservation(){        
        Reservation res = new ResBuilder().build();        
        
        manager.createReservation(res);        
        
        exception.expect(IllegalArgumentException.class);
        manager.getReservationPrice(null);
    }
    
    @Test
    public void findAllUnoccupiedRoomsWithValidArgumentsSomeMatch(){
        Room room2 = newRoom(474748512L,1,new BigDecimal(500.00),2,RoomType.STANDARD,"216");
        Room room3 = newRoom(165400004L,4,new BigDecimal(2500.00),3,RoomType.FAMILY,"300");
        Room room4 = newRoom(252525252L,2,new BigDecimal(1000.00),5,RoomType.SUITE,"505");
        Reservation res1 = new ResBuilder().build();
        Reservation res2 = new ResBuilder().startTime(new Date(63_000_000_000_158l))
                                           .expectedEndTime(new Date(63_000_001_800_000l))
                                           .realEndTime(new Date(63_000_001_800_000l))
                                           .room(room2).servicesSpendings(new BigDecimal(5420.50)).build();
        Reservation res3 = new ResBuilder().startTime(new Date(64_000_000_000l))
                                           .expectedEndTime(new Date(64_750_000_000l))
                                           .realEndTime(new Date(64_750_000_000l))
                                           .room(room3).build();
        Reservation res4 = new ResBuilder().startTime(new Date(65_100_000_000l))
                                           .expectedEndTime(new Date(66_000_000_000l))
                                           .room(room4).build();
        
        manager.createReservation(res1);
        manager.createReservation(res2);
        manager.createReservation(res3);
        manager.createReservation(res4);
        
        Date from = new Date(64_900_000_000l);
        Date to = new Date(65_300_000_000l);
        List<Room> expRooms = Arrays.asList(room2,room3);
        List<Room> actRooms = manager.findAllUnoccupiedRooms(from, to);
        
        Collections.sort(expRooms, roomIdComparator);
        Collections.sort(actRooms, roomIdComparator);
        
        assertEquals("Unoccupied rooms should be 2",2,actRooms.size());
        assertDeepRoomEquals(expRooms,actRooms);
    }
    
    @Test
    public void findAllUnoccupiedRoomsWithCloseTimeComparison(){
        Room room2 = newRoom(474748512L,1,new BigDecimal(500.00),2,RoomType.STANDARD,"216");
        Room room3 = newRoom(165400004L,4,new BigDecimal(2500.00),3,RoomType.FAMILY,"300");
        Room room4 = newRoom(252525252L,2,new BigDecimal(1000.00),5,RoomType.SUITE,"505");
        Reservation res1 = new ResBuilder().build();
        Reservation res2 = new ResBuilder().startTime(new Date(64_800_000_001l))
                                           .expectedEndTime(new Date(65_000_001_800_000l))
                                           .realEndTime(new Date(65_000_001_800_000l))
                                           .room(room2).servicesSpendings(new BigDecimal(5420.50)).build();
        Reservation res3 = new ResBuilder().startTime(new Date(64_000_000_000l))
                                           .expectedEndTime(new Date(64_750_000_000l))
                                           .realEndTime(new Date(64_750_000_000l))
                                           .room(room3).build();
        Reservation res4 = new ResBuilder().startTime(new Date(65_299_999_999l))
                                           .expectedEndTime(new Date(66_000_000_000l))
                                           .room(room4).build();
        
        manager.createReservation(res1);
        manager.createReservation(res2);
        manager.createReservation(res3);
        manager.createReservation(res4);
        
        Date from = new Date(64_800_000_001l);
        Date to = new Date(65_300_000_000l);
        List<Room> expRooms = Arrays.asList(room3,res1.getRoom());
        List<Room> actRooms = manager.findAllUnoccupiedRooms(from, to);
        
        Collections.sort(expRooms, roomIdComparator);
        Collections.sort(actRooms, roomIdComparator);
        
        assertEquals("Unoccupied rooms should be 2",2,actRooms.size());
        assertDeepRoomEquals(expRooms,actRooms);
    }
    
    @Test
    public void findAllUnoccupiedRoomsWithValidArgumentsNoMatch(){
        Room room2 = newRoom(474748512L,1,new BigDecimal(500.00),2,RoomType.STANDARD,"216");
        Room room3 = newRoom(165400004L,4,new BigDecimal(2500.00),3,RoomType.FAMILY,"300");
        Reservation res1 = new ResBuilder().build();
        Reservation res2 = new ResBuilder().startTime(new Date(64_800_000_001l))
                                           .expectedEndTime(new Date(65_000_001_800_000l))
                                           .realEndTime(new Date(65_000_001_800_000l))
                                           .room(room2).servicesSpendings(new BigDecimal(5420.50)).build();
        Reservation res3 = new ResBuilder().startTime(new Date(64_000_000_000l))
                                           .expectedEndTime(new Date(64_750_000_000l))
                                           .realEndTime(new Date(64_750_000_000l))
                                           .room(room3).build();
        
        manager.createReservation(res1);
        manager.createReservation(res2);
        manager.createReservation(res3);
        
        Date from = new Date(64_600_000_001l);
        Date to = new Date(65_300_000_000l);
        List<Room> actRooms = manager.findAllUnoccupiedRooms(from, to);
        
        assertEquals("There should not be any unoccupied room",0,actRooms.size());
    }
    
    @Test
    public void findAllUnoccupiedRoomsWithArgumentFromAfterTo(){
        Reservation res = new ResBuilder().build();
        
        manager.createReservation(res);
        
        Date from = new Date(64_800_000_001l);
        Date to = new Date(64_300_000_000l);
        
        exception.expect(IllegalArgumentException.class);
        List<Room> actRooms = manager.findAllUnoccupiedRooms(from, to);
    }
    
    @Test
    public void findAllUnoccupiedRoomsWithNullArgumentFrom(){
        Reservation res = new ResBuilder().build();
        
        manager.createReservation(res);
        
        Date from = null;
        Date to = new Date(64_300_000_000l);
        
        exception.expect(IllegalArgumentException.class);
        List<Room> actRooms = manager.findAllUnoccupiedRooms(from, to);
    }
    
    @Test
    public void findAllUnoccupiedRoomsWithNullArgumentTo(){
        Reservation res = new ResBuilder().build();
        
        manager.createReservation(res);
        
        Date from = new Date(64_800_000_001l);
        Date to = null;
        
        exception.expect(IllegalArgumentException.class);
        List<Room> actRooms = manager.findAllUnoccupiedRooms(from, to);
    }
    
    @Test
    public void findAllUnoccupiedRoomsWithBothNullArgument(){
        Reservation res = new ResBuilder().build();
        
        manager.createReservation(res);
        
        Date from = null;
        Date to = null;
        
        exception.expect(IllegalArgumentException.class);
        List<Room> actRooms = manager.findAllUnoccupiedRooms(from, to);
    }
    
    @Test
    public void findTopFiveSpendersWithRecordsInDB(){
        Guest guest2 = newGuest("Silvio Pavi Run","222 474 222","4544000444",new Date(0l));
        Guest guest3 = newGuest("Franco Bernardi",null,"1020304059",new Date(-60_054_147l));
        Guest guest4 = newGuest("Low Dubruis","300-410-100","1111111245",new Date(3_100l));
        Guest guest5 = newGuest("Jan Marek",null,"1234561231",new Date(150_300_450l));
        Guest guest6 = newGuest("Jakub Prak","(+480)-410-410-140","1400450211",new Date(0l));
        Guest guest7 = newGuest("Robert Koala",null,"7894414470", new Date(10_150l));
        Room room2 = newRoom(474748512L,1,new BigDecimal(500.00),2,RoomType.STANDARD,"216");
        Room room3 = newRoom(165400004L,4,new BigDecimal(2500.00),3,RoomType.FAMILY,"300");
        Room room4 = newRoom(252525252L,2,new BigDecimal(1000.00),5,RoomType.SUITE,"505");
        Room room5 = newRoom(125463897L,3,new BigDecimal(2750.00),2,RoomType.FAMILY,"217");
        Room room6 = newRoom(100463897L,3,new BigDecimal(2100.00),2,RoomType.SUITE,"219");
        Room room7 = newRoom(666663897L,6,new BigDecimal(4800.00),5,RoomType.APARTMENT,"535");
        Reservation res1 = new ResBuilder().servicesSpendings(new BigDecimal(1159.48)).build();
        Reservation res2 = new ResBuilder().guest(guest2).realEndTime(new Date(65_300_000_000l))
                                           .room(room2).servicesSpendings(new BigDecimal(5420.50)).build();
        Reservation res3 = new ResBuilder().guest(guest3).room(room3).servicesSpendings(new BigDecimal(10_410.21)).build();
        Reservation res4 = new ResBuilder().guest(guest4).room(room4).servicesSpendings(new BigDecimal(1211.00)).build();
        Reservation res5 = new ResBuilder().realEndTime(new Date(65_000_000_000l)).room(room2)
                                           .servicesSpendings(new BigDecimal(15_500.00)).build();
        Reservation res6 = new ResBuilder().guest(guest5).room(room5).servicesSpendings(new BigDecimal(9874.00)).build();
        Reservation res7 = new ResBuilder().guest(guest6).room(room6).servicesSpendings(new BigDecimal(10000.00)).build();
        Reservation res8 = new ResBuilder().guest(guest7).room(room7).servicesSpendings(new BigDecimal(6147.80)).build();
        
        manager.createReservation(res1);
        manager.createReservation(res2);
        manager.createReservation(res3);
        manager.createReservation(res4);
        manager.createReservation(res5);
        manager.createReservation(res6);
        manager.createReservation(res7);
        manager.createReservation(res8);
        
        Map<BigDecimal,Guest> expTopSpenders = new HashMap<>();
        expTopSpenders.put(res3.getServicesSpendings(), res3.getGuest());
        expTopSpenders.put(res7.getServicesSpendings(), res7.getGuest());
        expTopSpenders.put(res6.getServicesSpendings(), res6.getGuest());
        expTopSpenders.put(res8.getServicesSpendings(), res8.getGuest());
        expTopSpenders.put(res4.getServicesSpendings(), res4.getGuest());
        Map<BigDecimal,Guest> actTopSpenders = manager.findTopFiveSpenders();
        
        assertNotNull("There should be top 5 spenders",actTopSpenders);
        assertSpendersEquals(expTopSpenders,actTopSpenders);        
    }
    
    @Test
    public void findTopFiveSpendersWithNoActualReservetion(){
        Guest guest2 = newGuest("Silvio Pavi Run","222 474 222","4544000444",new Date(0l));
        Room room2 = newRoom(474748512L,1,new BigDecimal(500.00),2,RoomType.STANDARD,"216");
        Reservation res1 = new ResBuilder().realEndTime(new Date(65_000_000_000l)).build();
        Reservation res2 = new ResBuilder().guest(guest2).realEndTime(new Date(65_300_000_000l))
                                           .room(room2).servicesSpendings(new BigDecimal(5420.50)).build();
        
        manager.createReservation(res1);
        manager.createReservation(res2);
        
        assertNull("There should not be any top spender",manager.findTopFiveSpenders());
    }
    
    @Test
    public void findTopFiveSpendersWithEmptyDB(){
        assertNull("There should not be any top spender",manager.findTopFiveSpenders());
    }
    
    private static Guest newGuest(String name, String phone, String idCardNum, Date born){
        Guest guest = new Guest();
        guest.setName(name);
        guest.setPhone(phone);
        guest.setIdCardNum(idCardNum);
        guest.setBorn(new Date(born.getTime()));
        
        return guest;
    }
    
    private static Room newRoom(Long id, int cap, BigDecimal price, int floor, RoomType type, String num){
        Room room = new Room();
        room.setId(id);
        room.setCapacity(cap);
        room.setPrice(price);
        room.setFloor(floor);
        room.setType(type);
        room.setNumber(num);
        
        return room;
    }
    
    private void assertDeepRoomEquals(List<Room> expRooms, List<Room> actRooms){
        for(int i = 0; i < expRooms.size(); ++i){
            Room expRoom = expRooms.get(i);
            Room actRoom = actRooms.get(i);
            assertDeepRoomEquals(expRoom,actRoom);
        }
    }
    
    private void assertDeepRoomEquals(Room expRoom, Room actRoom){
        assertEquals(expRoom.getId(), actRoom.getId());
        assertEquals(expRoom.getCapacity(), actRoom.getCapacity());
        assertEquals(expRoom.getPrice(), actRoom.getPrice());
        assertEquals(expRoom.getFloor(), actRoom.getFloor());
        assertEquals(expRoom.getType(), actRoom.getType());
        assertEquals(expRoom.getNumber(), actRoom.getNumber());
    }
    
    private void assertSpendersEquals(Map<BigDecimal,Guest> expTopSpenders, Map<BigDecimal,Guest> actTopSpenders){
        Set<BigDecimal> expKeys = expTopSpenders.keySet();
        Set<BigDecimal> actKeys = actTopSpenders.keySet();
        assertEquals("Sets of keys should be same",expKeys,actKeys);
        
        for(BigDecimal bd : expKeys){
            assertDeepGuestEquals(expTopSpenders.get(bd),actTopSpenders.get(bd));
        }
    }
    
    private void assertDeepGuestEquals(Guest expGuest, Guest actGuest){
        assertEquals(expGuest.getId(), actGuest.getId());
        assertEquals(expGuest.getName(), actGuest.getName());
        assertEquals(expGuest.getIdCardNum(), actGuest.getIdCardNum());
        assertEquals(expGuest.getPhone(), actGuest.getPhone());
        assertEquals(expGuest.getBorn().getTime(), actGuest.getBorn().getTime());
    }
    
    private static Comparator<Room> roomIdComparator = new Comparator<Room>() {

        @Override
        public int compare(Room o1, Room o2) {
            return Long.valueOf(o1.getId()).compareTo(Long.valueOf(o2.getId()));
        }
    };
}

class ResBuilder{
    private Room room = newRoom();
    private Date startTime = new Date(64_800_000_000_000l);
    private Date realEndTime = null;
    private Date expectedEndTime = new Date(65_664_000_000_000l);
    private Guest guest = newGuest();
    private BigDecimal servicesSpendings = new BigDecimal(0.00);
    
    public ResBuilder(){
        
    }
    
    public ResBuilder room(Room value){
        this.room = value;
        return this;
    }
    
    public ResBuilder startTime(Date value){
        if(value == null)
            this.startTime = null;
        else
            this.startTime = new Date(value.getTime());
        return this;
    }
    
    public ResBuilder realEndTime(Date value){
        if(value == null)
            this.realEndTime = null;
        else
            this.realEndTime = new Date(value.getTime());
        return this;
    }
    
    public ResBuilder expectedEndTime(Date value){
        if(value == null)
            this.expectedEndTime = null;
        else
            this.expectedEndTime = new Date(value.getTime());
        return this;
    }
    
    public ResBuilder guest(Guest value){
        this.guest = value;
        return this;
    }
    
    public ResBuilder servicesSpendings(BigDecimal value){
        this.servicesSpendings = value;
        return this;
    }
    
    public Reservation build(){
        Reservation res = new Reservation();
        res.setRoom(this.room);
        res.setStartTime(this.startTime);
        res.setRealEndTime(this.realEndTime);
        res.setExpectedEndTime(this.expectedEndTime);
        res.setGuest(this.guest);
        res.setServicesSpendings(this.servicesSpendings);
        
        return res;
    }
    
    private Room newRoom(){
        Room r = new Room();
        r.setId(3656351487L);
        r.setCapacity(2);
        r.setPrice(new BigDecimal(1500.00));
        r.setFloor(5);
        r.setType(RoomType.STANDARD);
        r.setNumber("501");
        
        return r;
    }
    
    private Guest newGuest(){
        Guest g = new Guest();
        g.setName("Pepa Korek");
        g.setPhone("(+420) 777 888 999");
        g.setIdCardNum("123456789");
        g.setBorn(new Date(14_000l));
        
        return g;
    }
}
