/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.muni.fi.pv168.hotelmanager.backend;

import cz.muni.fi.pv168.common.DBUtils;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;



/**
 *
 * @author Ondrej Smelko, Tomas Smid
 */

public class ReservationManagerImplTest {
    
    @Rule public ExpectedException exception = ExpectedException.none();
    
    //private static DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.US);
    private static DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    
    private static DataSource prepareDataSource(){
        BasicDataSource bds = new BasicDataSource();
        bds.setUrl("jdbc:derby:memory:SSHotelGuestEvidenceDB;create=true");
        return bds;
    }
    
    private ReservationManagerImpl manager;    
    private DataSource dataSource;
    private TimeManager tmMock;
    
    @Before
    public void setUp() throws SQLException {
        tmMock = mock(TimeManager.class);
        dataSource = prepareDataSource();
        DBUtils.tryCreateTables(dataSource,ReservationManager.class.getResourceAsStream("createTables.sql"));
        manager = new ReservationManagerImpl(dataSource,tmMock);
    }
    
    @After
    public void tearDown() throws SQLException{
        DBUtils.executeSqlScript(dataSource,ReservationManager.class.getResourceAsStream("dropTables.sql"));
    }

@Test
    public void createReservation() {
        Reservation res = new ResBuilder().build();
        storeGuest(res.getGuest());
        storeRoom(res.getRoom());
        manager.createReservation(res);
        
        Long resId = res.getId();
        assertNotNull(resId);
        Reservation result = manager.getReservationById(resId);
        assertEquals(res, result);
        assertNotSame(res, result);
        assertDeepReservationEquals(res, result);
    }

    @Test
    public void getReservation() {

        assertNull(manager.getReservationById(1l));        

        Reservation res = new ResBuilder().build();
        storeGuest(res.getGuest());
        storeRoom(res.getRoom());
        manager.createReservation(res);
        Long resId = res.getId();

        Reservation result = manager.getReservationById(resId);
        assertEquals(res, result);
        assertDeepReservationEquals(res, result);
    }

    @Test
    public void getAllReservations() {

        assertTrue(manager.findAllReservations().isEmpty());

        Guest guest2 = newGuest("John Dragos", null, "151515151", new Date(0l));
        Room room2 = newRoom(null,1, new BigDecimal("500.00"), 2, RoomType.STANDARD, "216");
        Reservation r1 = new ResBuilder().build();
        Reservation r2 = new ResBuilder().startTime(new Date(63_000_000_000_158l))
                                         .expectedEndTime(new Date(63_000_001_800_000l))
                                         .realEndTime(new Date(63_000_001_800_000l))
                                         .guest(guest2)
                                         .room(room2)
                                         .servicesSpendings(new BigDecimal("5420.00")).build();
        storeGuest(r1.getGuest());
        storeGuest(guest2);
        storeRoom(r1.getRoom());
        storeRoom(room2);
        manager.createReservation(r1);
        manager.createReservation(r2);

        List<Reservation> expected = Arrays.asList(r1, r2);
        List<Reservation> actual = manager.findAllReservations();

        Collections.sort(actual, reservationIdComparator);
        Collections.sort(expected, reservationIdComparator);

        assertEquals(expected, actual);
        assertDeepReservationEquals(expected, actual);
    }

    @Test
    public void createReservationWithNullAttribute() {

        exception.expect(IllegalArgumentException.class);
        manager.createReservation(null);

    }

    @Test
    public void addReservationWithInvalidId() {
        Reservation res = new ResBuilder().build();
        res.setId(1l);

        exception.expect(IllegalArgumentException.class);
        manager.createReservation(res);

    }

    @Test
    public void addReservationWithNullRoom() {

        Reservation res = new ResBuilder().build();
        res.setRoom(null);

        exception.expect(IllegalArgumentException.class);
        manager.createReservation(res);

    }

    @Test
    public void addReservationWithNullStartTime() {
        Reservation res = new ResBuilder().build();
        res.setStartTime(null);

        exception.expect(IllegalArgumentException.class);
        manager.createReservation(res);

    }

    @Test
    public void addReservationWithNullExpectedEndTime() {
        Reservation res = new ResBuilder().build();
        res.setExpectedEndTime(null);

        exception.expect(IllegalArgumentException.class);
        manager.createReservation(res);

    }
    
    @Test
    public void addReservationWithStartTimeBiggerThanExpectedEndTime() {
        Reservation res = new ResBuilder().build();
        // biger than new Date(65_664_000_000_000l)
        res.setStartTime(new Date(66_664_000_000_000l));

        exception.expect(IllegalArgumentException.class);
        manager.createReservation(res);
    }

    @Test
    public void addReservationWithNullGuest() {
        Reservation res = new ResBuilder().build();
        res.setGuest(null);

        exception.expect(IllegalArgumentException.class);
        manager.createReservation(res);

    }

    @Test
    public void addReservationWithNullServicesSpendings() {
        Reservation res = new ResBuilder().build();
        res.setServicesSpendings(null);

        exception.expect(IllegalArgumentException.class);
        manager.createReservation(res);

    }
    
    @Test
    public void addReservationWithNegativeServicesSpendings() {
        Reservation res = new ResBuilder().build();
        res.setServicesSpendings(new BigDecimal("-1.00"));

        exception.expect(IllegalArgumentException.class);
        manager.createReservation(res);

    }

    @Test
    public void updateReservationRoom() {
        Guest guest = newGuest("Pepa Korek", "(+420) 777 888 999", "123456789", dateFromString("24/04/1989"));
        Guest guest2 = newGuest("John Dragos", null, "151515151", new Date(0l));
        Room room2 = newRoom(null, 1, new BigDecimal("500.00"), 2, RoomType.STANDARD, "216");
        Reservation r1 = new ResBuilder().build();
        Reservation r2 = new ResBuilder().startTime(new Date(63_000_000_000_158l))
                                         .expectedEndTime(new Date(63_000_001_800_000l))
                                         .realEndTime(new Date(63_000_001_800_000l))
                                         .guest(guest2)
                                         .room(room2)
                                         .servicesSpendings(new BigDecimal("5420.00")).build();
        BigDecimal bd = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_EVEN);
        storeGuest(r1.getGuest());
        storeGuest(r2.getGuest());
        storeRoom(r1.getRoom());
        storeRoom(r2.getRoom());
        manager.createReservation(r1);
        manager.createReservation(r2);

        Long resId1 = r1.getId();

        r1 = manager.getReservationById(resId1);
        Room room3 = newRoom(null, 4, new BigDecimal("2500.00"), 3, RoomType.FAMILY, "300");
        storeRoom(room3);
        r1.setRoom(room3);
        manager.updateReservation(r1);
        
        assertDeepRoomEquals(room3, r1.getRoom());
        assertEquals(dateFromString("01/01/2015").getTime(), r1.getStartTime().getTime());
        assertNull(r1.getRealEndTime());
        assertEquals(dateFromString("10/01/2015").getTime(), r1.getExpectedEndTime().getTime());
        guest.setId(r1.getGuest().getId());
        assertDeepGuestEquals(guest, r1.getGuest());
        assertEquals(bd, r1.getServicesSpendings());
        
        // Check if updates didn't affected other records
        assertDeepReservationEquals(r2, manager.getReservationById(r2.getId()));
    }
    
    @Test
    public void updateReservationStartTime() {
        Guest guest = newGuest("Pepa Korek", "(+420) 777 888 999", "123456789", dateFromString("24/04/1989"));
        Guest guest2 = newGuest("John Dragos", null, "151515151", new Date(0l));
        Room room2 = newRoom(null, 1, new BigDecimal("500.00"), 2, RoomType.STANDARD, "216");
        Room room3 = newRoom(null, 2, new BigDecimal("1500.00"), 5, RoomType.STANDARD, "501");
        Reservation r1 = new ResBuilder().build();
        Reservation r2 = new ResBuilder().startTime(new Date(63_000_000_000_158l))
                                         .expectedEndTime(new Date(63_000_001_800_000l))
                                         .realEndTime(new Date(63_000_001_800_000l))
                                         .guest(guest2)
                                         .room(room2)
                                         .servicesSpendings(new BigDecimal("5420.00")).build();
        BigDecimal bd = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_EVEN);
        storeGuest(r1.getGuest());
        storeGuest(guest2);
        storeRoom(room2);        
        storeRoom(r1.getRoom());
        manager.createReservation(r1);
        manager.createReservation(r2);

        Long resId1 = r1.getId();

        r1 = manager.getReservationById(resId1);
        r1.setStartTime(dateFromString("03/01/2015"));
        manager.updateReservation(r1);
        
        room3.setId(r1.getRoom().getId());
        assertDeepRoomEquals(room3, r1.getRoom());
        assertEquals(dateFromString("03/01/2015").getTime(), r1.getStartTime().getTime());
        assertNull(r1.getRealEndTime());
        assertEquals(dateFromString("10/01/2015").getTime(), r1.getExpectedEndTime().getTime());
        guest.setId(r1.getGuest().getId());
        assertDeepGuestEquals(guest, r1.getGuest());
        assertEquals(bd, r1.getServicesSpendings());
        
        // Check if updates didn't affected other records
        assertDeepReservationEquals(r2, manager.getReservationById(r2.getId()));
    }
    
    @Test
    public void updateReservationRealEndTime() {
        Guest guest = newGuest("Pepa Korek", "(+420) 777 888 999", "123456789", dateFromString("24/04/1989"));
        Guest guest2 = newGuest("John Dragos", null, "151515151", new Date(0l));
        Room room2 = newRoom(null, 1, new BigDecimal("500.00"), 2, RoomType.STANDARD, "216");
        Room room3 = newRoom(null, 2, new BigDecimal("1500.00"), 5, RoomType.STANDARD, "501");
        Reservation r1 = new ResBuilder().build();
        Reservation r2 = new ResBuilder().startTime(new Date(63_000_000_000_158l))
                                         .expectedEndTime(new Date(63_000_001_800_000l))
                                         .realEndTime(new Date(63_000_001_800_000l))
                                         .guest(guest2)
                                         .room(room2)
                                         .servicesSpendings(new BigDecimal("5420.00")).build();
        BigDecimal bd = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_EVEN);
        storeGuest(r1.getGuest());
        storeGuest(guest2);
        storeRoom(r1.getRoom());
        storeRoom(room2);
        manager.createReservation(r1);
        manager.createReservation(r2);

        Long resId1 = r1.getId();
        
        r1 = manager.getReservationById(resId1);
        r1.setRealEndTime(dateFromString("08/01/2015"));
        manager.updateReservation(r1);
        
        room3.setId(r1.getRoom().getId());
        assertDeepRoomEquals(room3, r1.getRoom());
        assertEquals(dateFromString("01/01/2015").getTime(), r1.getStartTime().getTime());
        assertEquals(dateFromString("08/01/2015").getTime(), r1.getRealEndTime().getTime());
        assertEquals(dateFromString("10/01/2015").getTime(), r1.getExpectedEndTime().getTime());
        guest.setId(r1.getGuest().getId());
        assertDeepGuestEquals(guest, r1.getGuest());
        assertEquals(bd, r1.getServicesSpendings());
        
        // Check if updates didn't affected other records
        assertDeepReservationEquals(r2, manager.getReservationById(r2.getId()));
    }
    
    @Test
    public void updateReservationExpectedEndTime() {
        Guest guest = newGuest("Pepa Korek", "(+420) 777 888 999", "123456789", dateFromString("24/04/1989"));
        Guest guest2 = newGuest("John Dragos", null, "151515151", new Date(0l));
        Room room2 = newRoom(null, 1, new BigDecimal("500.00"), 2, RoomType.STANDARD, "216");
        Room room3 = newRoom(null, 2, new BigDecimal("1500.00"), 5, RoomType.STANDARD, "501");
        Reservation r1 = new ResBuilder().build();
        Reservation r2 = new ResBuilder().startTime(new Date(63_000_000_000_158l))
                                         .expectedEndTime(new Date(63_000_001_800_000l))
                                         .realEndTime(new Date(63_000_001_800_000l))
                                         .guest(guest2)
                                         .room(room2)
                                         .servicesSpendings(new BigDecimal("5420.00")).build();
        BigDecimal bd = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_EVEN);
        storeGuest(r1.getGuest());
        storeGuest(guest2);
        storeRoom(r1.getRoom());
        storeRoom(room2);
        manager.createReservation(r1);
        manager.createReservation(r2);

        Long resId1 = r1.getId();
        
        r1 = manager.getReservationById(resId1);
        r1.setExpectedEndTime(dateFromString("15/01/2015"));
        manager.updateReservation(r1);
        
        room3.setId(r1.getRoom().getId());
        assertDeepRoomEquals(room3, r1.getRoom());
        assertEquals(dateFromString("01/01/2015").getTime(), r1.getStartTime().getTime());
        assertNull(r1.getRealEndTime());
        assertEquals(dateFromString("15/01/2015").getTime(), r1.getExpectedEndTime().getTime());
        guest.setId(r1.getGuest().getId());
        assertDeepGuestEquals(guest, r1.getGuest());
        assertEquals(bd, r1.getServicesSpendings());
        
        // Check if updates didn't affected other records
        assertDeepReservationEquals(r2, manager.getReservationById(r2.getId()));
    }
    
    @Test
    public void updateReservationGuest() {
        Guest guest2 = newGuest("John Dragos", null, "151515151", new Date(0l));
        Room room2 = newRoom(null, 1, new BigDecimal("500.00"), 2, RoomType.STANDARD, "216");
        Room room3 = newRoom(null, 2, new BigDecimal("1500.00"), 5, RoomType.STANDARD, "501");
        Reservation r1 = new ResBuilder().build();
        Reservation r2 = new ResBuilder().startTime(new Date(63_000_000_000_158l))
                                         .expectedEndTime(new Date(63_000_001_800_000l))
                                         .realEndTime(new Date(63_000_001_800_000l))
                                         .guest(guest2)
                                         .room(room2)
                                         .servicesSpendings(new BigDecimal("5420.00")).build();
        BigDecimal bd = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_EVEN);
        storeGuest(r1.getGuest());
        storeGuest(guest2);
        storeRoom(r1.getRoom());
        storeRoom(room2);
        manager.createReservation(r1);
        manager.createReservation(r2);

        Long resId1 = r1.getId();
        
        r1 = manager.getReservationById(resId1);
        Guest guest3 = newGuest("Franco Bernardi", null, "102030405", dateFromString("20/09/1992"));
        storeGuest(guest3);
        r1.setGuest(guest3);
        manager.updateReservation(r1);
        
        room3.setId(r1.getRoom().getId());
        assertDeepRoomEquals(room3, r1.getRoom());
        assertEquals(dateFromString("01/01/2015").getTime(), r1.getStartTime().getTime());
        assertNull(r1.getRealEndTime());
        assertEquals(dateFromString("10/01/2015").getTime(), r1.getExpectedEndTime().getTime());
        assertDeepGuestEquals(guest3, r1.getGuest());        
        assertEquals(bd, r1.getServicesSpendings());
        
        // Check if updates didn't affected other records
        assertDeepReservationEquals(r2, manager.getReservationById(r2.getId()));
    }    

    @Test
    public void updateReservationServicesSpendings() {
        Guest guest = newGuest("Pepa Korek", "(+420) 777 888 999", "123456789", dateFromString("24/04/1989"));
        Guest guest2 = newGuest("John Dragos", null, "151515151", new Date(0l));
        Room room2 = newRoom(null, 1, new BigDecimal("500.00"), 2, RoomType.STANDARD, "216");
        Room room3 = newRoom(null, 2, new BigDecimal("1500.00"), 5, RoomType.STANDARD, "501");
        Reservation r1 = new ResBuilder().build();
        Reservation r2 = new ResBuilder().startTime(new Date(63_000_000_000_158l))
                                         .expectedEndTime(new Date(63_000_001_800_000l))
                                         .realEndTime(new Date(63_000_001_800_000l))
                                         .guest(guest2)
                                         .room(room2)
                                         .servicesSpendings(new BigDecimal("5420.00")).build();
        storeGuest(r1.getGuest());
        storeGuest(guest2);
        storeRoom(r1.getRoom());
        storeRoom(room2);
        manager.createReservation(r1);
        manager.createReservation(r2);

        Long resId1 = r1.getId();    
        

        r1 = manager.getReservationById(resId1);
        r1.setServicesSpendings(new BigDecimal("100.00"));
        manager.updateReservation(r1);
        
        room3.setId(r1.getRoom().getId());
        assertDeepRoomEquals(room3, r1.getRoom());
        assertEquals(dateFromString("01/01/2015").getTime(), r1.getStartTime().getTime());
        assertNull(r1.getRealEndTime());
        assertEquals(dateFromString("10/01/2015").getTime(), r1.getExpectedEndTime().getTime());
        guest.setId(r1.getGuest().getId());
        assertDeepGuestEquals(guest, r1.getGuest());
        assertEquals(new BigDecimal("100.00"), r1.getServicesSpendings());

        // Check if updates didn't affected other records
        assertDeepReservationEquals(r2, manager.getReservationById(r2.getId()));
    }

    @Test
    public void updateReservationWithNullAttribute() {
        exception.expect(IllegalArgumentException.class);
        manager.updateReservation(null);

    }

    @Test
    public void updateReservationWithWrongId() {

        Reservation res = new ResBuilder().build();
        storeRoom(res.getRoom());
        storeGuest(res.getGuest());
        manager.createReservation(res);
        Long resId = res.getId();

        res = manager.getReservationById(resId);
        res.setId(null);
        exception.expect(IllegalArgumentException.class);
        manager.updateReservation(res);

    }

    @Test
    public void updateReservationWithDecrementedId() {
        Reservation res = new ResBuilder().build();
        storeRoom(res.getRoom());
        storeGuest(res.getGuest());
        manager.createReservation(res);
        Long resId = res.getId();

        res = manager.getReservationById(resId);
        res.setId(resId - 1);
        exception.expect(IllegalArgumentException.class);
        manager.updateReservation(res);

    }

    @Test
    public void updateReservationWithIncrementedId() {
        Reservation res = new ResBuilder().build();
        storeRoom(res.getRoom());
        storeGuest(res.getGuest());
        manager.createReservation(res);
        Long resId = res.getId();

        res = manager.getReservationById(resId);
        res.setId(resId + 1);
        exception.expect(IllegalArgumentException.class);
        manager.updateReservation(res);

    }

    @Test
    public void updateReservationWithNullRoom() {
        Reservation res = new ResBuilder().build();
        storeRoom(res.getRoom());
        storeGuest(res.getGuest());
        manager.createReservation(res);
        Long resId = res.getId();

        res = manager.getReservationById(resId);
        res.setRoom(null);
        exception.expect(IllegalArgumentException.class);
        manager.updateReservation(res);

    }

    @Test
    public void updateReservationWithNullStartTime() {
        Reservation res = new ResBuilder().build();
        storeRoom(res.getRoom());
        storeGuest(res.getGuest());
        manager.createReservation(res);
        Long resId = res.getId();

        res = manager.getReservationById(resId);
        res.setStartTime(null);
        exception.expect(IllegalArgumentException.class);
        manager.updateReservation(res);

    }


    @Test
    public void updateReservationWithNullExpectedEndTime() {
        Reservation res = new ResBuilder().build();
        storeRoom(res.getRoom());
        storeGuest(res.getGuest());
        manager.createReservation(res);
        Long resId = res.getId();

        res = manager.getReservationById(resId);
        res.setExpectedEndTime(null);
        exception.expect(IllegalArgumentException.class);
        manager.updateReservation(res);

    }
    
    @Test
    public void updateReservationWithStartTimeBiggerThanExpectedEndTime() {
        Reservation res = new ResBuilder().build();
        storeRoom(res.getRoom());
        storeGuest(res.getGuest());
        manager.createReservation(res);
        Long resId = res.getId();
        
        res = manager.getReservationById(resId);
        // biger than new Date(65_664_000_000_000l)
        res.setStartTime(new Date(66_664_000_000_000l));
        exception.expect(IllegalArgumentException.class);
        manager.createReservation(res);
    }

    @Test
    public void updateReservationWithNullGuest() {
        Reservation res = new ResBuilder().build();
        storeRoom(res.getRoom());
        storeGuest(res.getGuest());
        manager.createReservation(res);
        Long resId = res.getId();

        res = manager.getReservationById(resId);
        res.setGuest(null);
        exception.expect(IllegalArgumentException.class);
        manager.updateReservation(res);

    }

    @Test
    public void updateReservationWithNullServiceSpendings() {
        Reservation res = new ResBuilder().build();
        storeRoom(res.getRoom());
        storeGuest(res.getGuest());
        manager.createReservation(res);
        Long resId = res.getId();

        res = manager.getReservationById(resId);
        res.setServicesSpendings(null);
        exception.expect(IllegalArgumentException.class);
        manager.updateReservation(res);

    }
    
    @Test
    public void updateReservationWithNegativeServiceSpendings() {
        Reservation res = new ResBuilder().build();
        storeRoom(res.getRoom());
        storeGuest(res.getGuest());
        manager.createReservation(res);
        Long resId = res.getId();

        res = manager.getReservationById(resId);
        res.setServicesSpendings(new BigDecimal("-1.00"));
        exception.expect(IllegalArgumentException.class);
        manager.updateReservation(res);

    }

    @Test
    public void deleteReservation() {
        Guest guest2 = newGuest("John Dragos", null, "151515151", new Date(0l));
        Room room2 = newRoom(null, 1, new BigDecimal("500.00"), 2, RoomType.STANDARD, "216");
        Reservation r1 = new ResBuilder().build();
        Reservation r2 = new ResBuilder().startTime(new Date(63_000_000_000_158l))
                                         .expectedEndTime(new Date(63_000_001_800_000l))
                                         .realEndTime(new Date(63_000_001_800_000l))
                                         .guest(guest2)
                                         .room(room2)
                                         .servicesSpendings(new BigDecimal("5420.00")).build();
        storeGuest(r1.getGuest());
        storeGuest(guest2);
        storeRoom(r1.getRoom());
        storeRoom(room2);
        manager.createReservation(r1);
        manager.createReservation(r2);

        assertNotNull(manager.getReservationById(r1.getId()));
        assertNotNull(manager.getReservationById(r2.getId()));

        manager.deleteReservation(r1);

        assertNull(manager.getReservationById(r1.getId()));
        assertNotNull(manager.getReservationById(r2.getId()));

    }

    @Test
    public void deleteReservationWithNullAttribute() {
        exception.expect(IllegalArgumentException.class);
        manager.deleteReservation(null);

    }

    @Test
    public void deleteReservationWithNullId() {
        Reservation res = new ResBuilder().build();

        res.setId(null);
        exception.expect(IllegalArgumentException.class);
        manager.deleteReservation(res);

    }

    @Test
    public void deleteReservationWithWrongId() {
        Reservation res = new ResBuilder().build();
        res.setId(1l);
        
        exception.expect(ServiceFailureException.class);
        manager.deleteReservation(res);

    }

    @Test
    public void findReservationForGuestWithNullAttribute() {
        exception.expect(IllegalArgumentException.class);
        manager.findReservationsForGuest(null);
    }

    @Test
    public void findReservationForRoomWithNullAttribute() {
        exception.expect(IllegalArgumentException.class);
        manager.findReservationsForRoom(null); 
    }    
    
    @Test
    public void getReservationPriceWithValidExistingReservation() throws Exception{
        when(tmMock.getCurrentDate()).thenReturn(dateFromString("09/01/2015"));
        
        Reservation res = new ResBuilder().build();
        BigDecimal expPrice = new BigDecimal("13500.00");
        
        storeGuest(res.getGuest());
        storeRoom(res.getRoom());        
        manager.createReservation(res);    
        
        BigDecimal actPrice = manager.getReservationPrice(res);
        
        assertEquals("Reservation price should be 13500",expPrice,actPrice);
    }
    
    @Test
    public void getReservationPriceWithValidAbsentReservation(){
        Guest guest = newGuest("John Dragos",null,"151515151",dateFromString("01/01/1970"));
        Room room = newRoom(null,1,new BigDecimal("500.00"),2,RoomType.STANDARD,"216");
        Reservation res = new ResBuilder().build();        
        Reservation res2 = new ResBuilder().startTime(dateFromString("30/04/2015")) //  format: dd/mm/yyyy
                                           .expectedEndTime(dateFromString("05/05/2015"))
                                           .guest(guest).room(room).build();
        
        storeGuest(res.getGuest());
        storeRoom(res.getRoom());  
        manager.createReservation(res);
        
        storeGuest(guest);
        storeRoom(room);                
        res2.setId(2L);
        
        BigDecimal actPrice = manager.getReservationPrice(res2);
        
        assertNull("Reservation price should be null",actPrice);
    }
    
    @Test
    public void getReservationPriceWithNullReservation(){        
        Reservation res = new ResBuilder().build();        
        
        storeGuest(res.getGuest());
        storeRoom(res.getRoom());  
        manager.createReservation(res);        
        
        exception.expect(IllegalArgumentException.class);
        manager.getReservationPrice(null);
    }
    
    @Test
    public void findAllUnoccupiedRoomsWithValidArgumentsSomeMatch(){
        Room room2 = newRoom(null,1,new BigDecimal("500.00"),2,RoomType.STANDARD,"216");
        Room room3 = newRoom(null,4,new BigDecimal("2500.00"),3,RoomType.FAMILY,"300");
        Room room4 = newRoom(null,2,new BigDecimal("1000.00"),5,RoomType.SUITE,"505");
        Guest guest2 = newGuest("Silvio Pavi Run","222 474 222","544000444",dateFromString("01/01/1970"));
        Guest guest3 = newGuest("Franco Bernardi",null,"020304059",dateFromString("01/01/1968"));
        Guest guest4 = newGuest("Low Dubruis","300-410-100","111111245",dateFromString("02/01/1970"));
        
        Reservation res1 = new ResBuilder().build();
        Reservation res2 = new ResBuilder().startTime(dateFromString("07/01/2015"))
                                           .expectedEndTime(dateFromString("09/01/2015"))
                                           .realEndTime(dateFromString("09/01/2015"))
                                           .room(room2).guest(guest2).servicesSpendings(new BigDecimal("5420.50")).build();
        Reservation res3 = new ResBuilder().startTime(dateFromString("18/01/2015"))
                                           .expectedEndTime(dateFromString("21/01/2015"))
                                           .realEndTime(dateFromString("21/01/2015"))
                                           .room(room3).guest(guest3).build();
        Reservation res4 = new ResBuilder().startTime(dateFromString("24/01/2015"))
                                           .expectedEndTime(dateFromString("01/02/2015"))
                                           .room(room4).guest(guest4).build();
        
        storeGuest(res1.getGuest());
        storeRoom(res1.getRoom());  
        manager.createReservation(res1);
        storeGuest(guest2);
        storeRoom(room2);  
        manager.createReservation(res2);
        storeGuest(guest3);
        storeRoom(room3);
        manager.createReservation(res3);
        storeGuest(guest4);
        storeRoom(room4);
        manager.createReservation(res4);
        
        Date from = dateFromString("22/01/2015");
        Date to = dateFromString("28/01/2015");
        List<Room> expRooms = Arrays.asList(room2,room3);
        List<Room> actRooms = manager.findAllUnoccupiedRooms(from, to);
        
        Collections.sort(expRooms, roomIdComparator);
        Collections.sort(actRooms, roomIdComparator);
        
        assertEquals("Unoccupied rooms should be 2",2,actRooms.size());
        assertDeepRoomEquals(expRooms,actRooms);
    }
    
    @Test
    public void findAllUnoccupiedRoomsWithCloseTimeComparison(){
        Room room2 = newRoom(null,1,new BigDecimal("500.00"),2,RoomType.STANDARD,"216");
        Room room3 = newRoom(null,4,new BigDecimal("2500.00"),3,RoomType.FAMILY,"300");
        Room room4 = newRoom(null,2,new BigDecimal("1000.00"),5,RoomType.SUITE,"505");
        Guest guest2 = newGuest("Silvio Pavi Run","222 474 222","544000444",dateFromString("01/01/1970"));
        Guest guest3 = newGuest("Franco Bernardi",null,"020304059",dateFromString("01/01/1968"));
        Guest guest4 = newGuest("Low Dubruis","300-410-100","111111245",dateFromString("02/01/1970"));
        
        Reservation res1 = new ResBuilder().realEndTime(dateFromString("10/01/2015")).build();
        Reservation res2 = new ResBuilder().startTime(dateFromString("11/01/2015"))
                                           .expectedEndTime(dateFromString("17/01/2015"))
                                           .realEndTime(dateFromString("17/01/2015"))
                                           .room(room2).guest(guest2).servicesSpendings(new BigDecimal("5420.50")).build();
        Reservation res3 = new ResBuilder().startTime(dateFromString("28/12/2014"))
                                           .expectedEndTime(dateFromString("31/12/2014"))
                                           .realEndTime(dateFromString("31/12/2014"))
                                           .room(room3).guest(guest3).build();
        Reservation res4 = new ResBuilder().startTime(dateFromString("10/01/2015"))
                                           .expectedEndTime(dateFromString("20/01/2015")) 
                                           .room(room4).guest(guest4).build();
        
        storeGuest(res1.getGuest());
        storeRoom(res1.getRoom());  
        manager.createReservation(res1);
        storeGuest(guest2);
        storeRoom(room2);  
        manager.createReservation(res2);
        storeGuest(guest3);
        storeRoom(room3);
        manager.createReservation(res3);
        storeGuest(guest4);
        storeRoom(room4);
        manager.createReservation(res4);
        
        Date from = new Date(res3.getRealEndTime().getTime()+1);
        Date to = new Date(res2.getStartTime().getTime()-1);
        List<Room> expRooms = Arrays.asList(room3,room2);
        List<Room> actRooms = manager.findAllUnoccupiedRooms(from, to);
        
        Collections.sort(expRooms, roomIdComparator);
        Collections.sort(actRooms, roomIdComparator);
        
        assertEquals("Unoccupied rooms should be 2",2,actRooms.size());
        assertDeepRoomEquals(expRooms,actRooms);
    }
    
    @Test
    public void findAllUnoccupiedRoomsWithValidArgumentsNoMatch(){
        Room room2 = newRoom(null,1,new BigDecimal("500.00"),2,RoomType.STANDARD,"216");
        Room room3 = newRoom(null,4,new BigDecimal("2500.00"),3,RoomType.FAMILY,"300");
        Room room4 = newRoom(null,2,new BigDecimal("1000.00"),5,RoomType.SUITE,"505");
        Guest guest2 = newGuest("Silvio Pavi Run","222 474 222","544000444",dateFromString("01/01/1970"));
        Guest guest3 = newGuest("Franco Bernardi",null,"020304059",dateFromString("01/01/1968"));
        Guest guest4 = newGuest("Low Dubruis","300-410-100","111111245",dateFromString("02/01/1970"));
        
        Reservation res1 = new ResBuilder().build();
        Reservation res2 = new ResBuilder().startTime(dateFromString("17/01/2015"))
                                           .expectedEndTime(dateFromString("24/01/2015"))
                                           .room(room2).guest(guest2).servicesSpendings(new BigDecimal("5420.50")).build();
        Reservation res3 = new ResBuilder().startTime(dateFromString("15/01/2015"))
                                           .expectedEndTime(dateFromString("23/01/2015"))
                                           .room(room3).guest(guest3).build();
        Reservation res4 = new ResBuilder().startTime(dateFromString("09/01/2015"))
                                           .expectedEndTime(dateFromString("14/01/2015"))
                                           .realEndTime(dateFromString("15/01/2015"))
                                           .guest(guest4).room(room4).build();
        
        storeGuest(res1.getGuest());
        storeRoom(res1.getRoom());  
        manager.createReservation(res1);
        storeGuest(guest2);
        storeRoom(room2);  
        manager.createReservation(res2);
        storeGuest(guest3);
        storeRoom(room3);
        manager.createReservation(res3);
        storeGuest(guest4);
        storeRoom(room4);
        manager.createReservation(res4);
        
        Date from = dateFromString("09/01/2015");
        Date to = dateFromString("19/01/2015");
        List<Room> actRooms = manager.findAllUnoccupiedRooms(from, to);
        
        assertTrue("There should not be any unoccupied room",actRooms.isEmpty());
    }
    
    @Test
    public void findAllUnoccupiedRoomsWithArgumentFromAfterTo(){
        Reservation res = new ResBuilder().build();
        storeGuest(res.getGuest());
        storeRoom(res.getRoom());
        manager.createReservation(res);
        
        Date from = dateFromString("17/01/2015");
        Date to = dateFromString("16/01/2015");
        
        exception.expect(IllegalArgumentException.class);
        List<Room> actRooms = manager.findAllUnoccupiedRooms(from, to);
    }
    
    @Test
    public void findAllUnoccupiedRoomsWithNullArgumentFrom(){
        Reservation res = new ResBuilder().build();
        storeGuest(res.getGuest());
        storeRoom(res.getRoom());
        manager.createReservation(res);
        
        Date from = null;
        Date to = dateFromString("17/01/2015");
        
        exception.expect(IllegalArgumentException.class);
        List<Room> actRooms = manager.findAllUnoccupiedRooms(from, to);
    }
    
    @Test
    public void findAllUnoccupiedRoomsWithNullArgumentTo(){
        Reservation res = new ResBuilder().build();
        storeGuest(res.getGuest());
        storeRoom(res.getRoom());
        manager.createReservation(res);
        
        Date from = dateFromString("20/01/2015");
        Date to = null;
        
        exception.expect(IllegalArgumentException.class);
        List<Room> actRooms = manager.findAllUnoccupiedRooms(from, to);
    }
    
    @Test
    public void findAllUnoccupiedRoomsWithBothNullArgument(){
        Reservation res = new ResBuilder().build();
        storeGuest(res.getGuest());
        storeRoom(res.getRoom());
        manager.createReservation(res);
        
        Date from = null;
        Date to = null;
        
        exception.expect(IllegalArgumentException.class);
        List<Room> actRooms = manager.findAllUnoccupiedRooms(from, to);
    }
    
    @Test
    public void findTopFiveSpendersWithRecordsInDB(){
        when(tmMock.getCurrentDate()).thenReturn(dateFromString("13/01/2015"));
        Guest guest2 = newGuest("Silvio Pavi Run","222 474 222","544000444",dateFromString("01/01/1970"));
        Guest guest3 = newGuest("Franco Bernardi",null,"020304059",dateFromString("01/01/1968"));
        Guest guest4 = newGuest("Low Dubruis","300-410-100","111111245",dateFromString("02/01/1970"));
        Guest guest5 = newGuest("Jan Marek",null,"234561231",dateFromString("03/01/1980"));
        Guest guest6 = newGuest("Jakub Prak","(+480)-410-410-140","400450211",dateFromString("01/01/1970"));
        Guest guest7 = newGuest("Robert Koala",null,"894414470", dateFromString("03/01/1970"));
        Guest guest8 = newGuest("Karel Malíček", null, "199888777", dateFromString("29/11/1991"));
        Room room2 = newRoom(null,1,new BigDecimal("500.00"),2,RoomType.STANDARD,"216");
        Room room3 = newRoom(null,4,new BigDecimal("2500.00"),3,RoomType.FAMILY,"300");
        Room room4 = newRoom(null,2,new BigDecimal("1000.00"),5,RoomType.SUITE,"505");
        Room room5 = newRoom(null,3,new BigDecimal("2750.00"),2,RoomType.FAMILY,"217");
        Room room6 = newRoom(null,3,new BigDecimal("2100.00"),2,RoomType.SUITE,"219");
        Room room7 = newRoom(null,6,new BigDecimal("4800.00"),5,RoomType.APARTMENT,"535");
        Room room8 = newRoom(null,4,new BigDecimal("1200.00"),3,RoomType.FAMILY,"314");
        Reservation res1 = new ResBuilder().servicesSpendings(new BigDecimal("1159.48")).build();
        Reservation res2 = new ResBuilder().guest(guest2).realEndTime(dateFromString("12/01/2015"))
                                           .room(room2).servicesSpendings(new BigDecimal("5420.50")).build();
        Reservation res3 = new ResBuilder().guest(guest3).room(room3).servicesSpendings(new BigDecimal("10410.21")).build();
        Reservation res4 = new ResBuilder().guest(guest4).room(room4).servicesSpendings(new BigDecimal("6146.20")).build();
        Reservation res5 = new ResBuilder().realEndTime(dateFromString("11/01/2015")).room(room5).guest(guest5)
                                           .servicesSpendings(new BigDecimal("15500.00")).build();
        Reservation res6 = new ResBuilder().guest(guest6).room(room6).servicesSpendings(new BigDecimal("9874.00")).build();
        Reservation res7 = new ResBuilder().guest(guest7).room(room7).servicesSpendings(new BigDecimal("10000.00")).build();
        Reservation res8 = new ResBuilder().guest(guest8).room(room8).servicesSpendings(new BigDecimal("6147.48")).build();
        
        //storeGuest(res5.getGuest());
        storeGuest(res1.getGuest());
        storeRoom(res1.getRoom());  
        manager.createReservation(res1);
        storeGuest(guest2);
        storeRoom(room2);  
        manager.createReservation(res2);
        storeGuest(guest3);
        storeRoom(room3);
        manager.createReservation(res3);
        storeGuest(guest4);
        storeRoom(room4);
        manager.createReservation(res4);
        storeGuest(guest5);
        storeRoom(room5);
        manager.createReservation(res5);
        storeGuest(guest6);
        storeRoom(room6);
        manager.createReservation(res6);
        storeGuest(guest7);
        storeRoom(room7);
        manager.createReservation(res7);
        storeGuest(guest8);
        storeRoom(room8);
        manager.createReservation(res8);
        
        /*Map<BigDecimal,Guest> expTopSpenders = new HashMap<>();
        expTopSpenders.put(res3.getServicesSpendings(), res3.getGuest());
        expTopSpenders.put(res7.getServicesSpendings(), res7.getGuest());
        expTopSpenders.put(res6.getServicesSpendings(), res6.getGuest());
        expTopSpenders.put(res8.getServicesSpendings(), res8.getGuest());
        expTopSpenders.put(res4.getServicesSpendings(), res4.getGuest());
        Map<BigDecimal,Guest> actTopSpenders = manager.findTopFiveSpenders();
        
        assertNotNull("There should be top 5 spenders",actTopSpenders);
        assertSpendersEquals(expTopSpenders,actTopSpenders);  */
        
        List<Reservation> expTopSpenders = new ArrayList<>();
        expTopSpenders.add(res3);
        expTopSpenders.add(res7);
        expTopSpenders.add(res6);
        expTopSpenders.add(res8);
        expTopSpenders.add(res4);
        List<Reservation> actTopSpenders = manager.findTopFiveSpenders();
        
        assertFalse("There should be top 5 spenders", actTopSpenders.isEmpty());
        assertSpendersEquals(expTopSpenders, actTopSpenders);
    }
    
    @Test
    public void findTopFiveSpendersWithNoActualReservetion(){
        Guest guest2 = newGuest("Silvio Pavi Run","222 474 222","454400044",new Date(0l));
        Room room2 = newRoom(null,1,new BigDecimal("500.00"),2,RoomType.STANDARD,"216");
        Reservation res1 = new ResBuilder().realEndTime(dateFromString("11/01/2015")).build();
        Reservation res2 = new ResBuilder().guest(guest2).realEndTime(dateFromString("12/01/2015"))
                                           .room(room2).servicesSpendings(new BigDecimal("5420.50")).build();
        storeGuest(res1.getGuest());
        storeGuest(guest2);
        storeRoom(res1.getRoom());
        storeRoom(res2.getRoom());
        manager.createReservation(res1);
        manager.createReservation(res2);
        
        //assertNull("There should not be any top spender",manager.findTopFiveSpenders());
        assertTrue("There should not be any top spender", manager.findTopFiveSpenders().isEmpty());
    }
    
    @Test
    public void findTopFiveSpendersWithEmptyDB(){
        //assertNull("There should not be any top spender",manager.findTopFiveSpenders());
        assertTrue("There should not be any top spender",manager.findTopFiveSpenders().isEmpty());
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
    
    private void assertDeepReservationEquals(Reservation expected, Reservation actual) {
        assertEquals(expected.getId(), actual.getId());
        assertDeepRoomEquals(expected.getRoom(), actual.getRoom());
        assertDeepGuestEquals(expected.getGuest(), actual.getGuest());
        assertEquals(expected.getServicesSpendings(), actual.getServicesSpendings());
        assertEquals(expected.getStartTime().getTime(), actual.getStartTime().getTime());
        assertEquals(expected.getExpectedEndTime().getTime(), actual.getExpectedEndTime().getTime());
        if(expected.getRealEndTime() == null || actual.getRealEndTime() == null){
            assertNull(expected.getRealEndTime());
            assertNull(actual.getRealEndTime());
        }else{
            assertEquals(expected.getRealEndTime().getTime(), actual.getRealEndTime().getTime());
        }
    }

    private void assertDeepReservationEquals(List<Reservation> expReservations, List<Reservation> actReservations){
        for (int i = 0; i < expReservations.size(); ++i) {
            Reservation expReservation = expReservations.get(i);
            Reservation actReservation = actReservations.get(i);
            assertDeepReservationEquals(expReservation, actReservation);
        }
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
    
    /*private void assertSpendersEquals(Map<BigDecimal,Guest> expTopSpenders, Map<BigDecimal,Guest> actTopSpenders){
        Set<BigDecimal> expKeys = expTopSpenders.keySet();
        Set<BigDecimal> actKeys = actTopSpenders.keySet();
        assertEquals("Sets of keys should be same",expKeys,actKeys);
        
        for(BigDecimal bd : expKeys){
            assertDeepGuestEquals(expTopSpenders.get(bd),actTopSpenders.get(bd));
        }
    }*/
    private void assertSpendersEquals(List<Reservation> expTopSpenders, List<Reservation> actTopSpenders){
        assertEquals("Sizes of lists of topSpeders should be same.",expTopSpenders.size(),actTopSpenders.size());
        for(int i = 0; i < expTopSpenders.size(); i++){
            assertEquals("Prices should be same", expTopSpenders.get(i).getServicesSpendings(), actTopSpenders.get(i).getServicesSpendings());
            assertEquals("Guests should be same", expTopSpenders.get(i).getGuest(), actTopSpenders.get(i).getGuest());
        }
    }
    
    private void assertDeepGuestEquals(Guest expGuest, Guest actGuest){
        assertEquals(expGuest.getId(), actGuest.getId());
        assertEquals(expGuest.getName(), actGuest.getName());
        assertEquals(expGuest.getIdCardNum(), actGuest.getIdCardNum());
        assertEquals(expGuest.getPhone(), actGuest.getPhone());
        assertEquals(expGuest.getBorn().getTime(), actGuest.getBorn().getTime());
    }
    
    private Date dateFromString(String date){        
        try {            
            return dateFormat.parse(date);
        } catch (ParseException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    private static Comparator<Room> roomIdComparator = new Comparator<Room>() {

        @Override
        public int compare(Room o1, Room o2) {
            return Long.valueOf(o1.getId()).compareTo(Long.valueOf(o2.getId()));
        }
    };
    
    private static Comparator<Reservation> reservationIdComparator = new Comparator<Reservation>() {

        @Override
        public int compare(Reservation o1, Reservation o2) {
            return Long.valueOf(o1.getId()).compareTo(Long.valueOf(o2.getId()));
        }
    };
    
    private void storeGuest(Guest guest){
        GuestManager man = new GuestManagerImpl(dataSource);
        man.createGuest(guest);
    }
    
    private void storeRoom(Room room){
        RoomManager man = new RoomManagerImpl(dataSource);
        man.createRoom(room);
    }
    
    class ResBuilder{
        private Room room = newRoom();
        private Date startTime = dateFromString("01/01/2015");
        private Date realEndTime = null;
        private Date expectedEndTime = dateFromString("10/01/2015");
        private Guest guest = newGuest();
        private BigDecimal servicesSpendings = BigDecimal.ZERO;

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
            r.setCapacity(2);
            r.setPrice(new BigDecimal("1500.00"));
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
            g.setBorn(dateFromString("24/04/1989"));

            return g;
        }
    }
}


