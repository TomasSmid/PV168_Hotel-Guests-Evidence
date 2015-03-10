/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.muni.fi.pv168.hotelmanager.backend;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Tomas Smid
 */
public class GuestManagerImplTest {
    
    private GuestManagerImpl manager;
    
    @Before
    public void setUp() {
        manager = new GuestManagerImpl();
    }
    
    @Test
    public void createGuest(){
        Date born = newBornDate(64_800_000_000l);
        Guest expGuest = newGuest("Pepa Korek", "(+420) 754 865 000", "154879833", born);
        manager.createGuest(expGuest);
        
        Long guestId = expGuest.getId();
        assertNotNull(guestId);
        
        Guest actGuest = manager.getGuestById(guestId);
        assertEquals(expGuest, actGuest);
        assertNotSame(expGuest, actGuest);
        assertAllAttributesEquals(expGuest, actGuest);        
    }
    
    @Test
    public void createGuestWithInvalidAttributes(){
        Date born = newBornDate(64_800_000_000l);
        
        //no guest specified
        Guest wrongGuest = null;        
        try{
            manager.createGuest(wrongGuest);
            fail();
        }catch(IllegalArgumentException ex){ }        
        
        //incorrect id set
        wrongGuest = newGuest("Pepa Korek", "(+420) 754 865 000", "154879833", born);
        wrongGuest.setId(1L);
        try{
            manager.createGuest(wrongGuest);
            fail();
        }catch(IllegalArgumentException ex){ }
        
        //guest with an empty name
        wrongGuest = newGuest("", "(+420) 754 865 000", "154879833", born);
        try{
            manager.createGuest(wrongGuest);
            fail();
        }catch(IllegalArgumentException ex){ }
        
        //guest without a name
        wrongGuest = newGuest(null, "(+420) 754 865 000", "154879833", born);
        try{
            manager.createGuest(wrongGuest);
            fail();
        }catch(IllegalArgumentException ex){ }
        
        //guest's name wrong format - name contains numbers
        wrongGuest = newGuest("Pepa4 5Korek5", "(+420) 754 865 000", "154879833", born);
        try{
            manager.createGuest(wrongGuest);
            fail();
        }catch(IllegalArgumentException ex){ }
        
        //guest's name wrong format - name contains underscore
        wrongGuest = newGuest("Pepa_Korek", "(+420) 754 865 000", "154879833", born);
        try{
            manager.createGuest(wrongGuest);
            fail();
        }catch(IllegalArgumentException ex){ }
        
        //guest's name wrong format - name contains dot
        wrongGuest = newGuest("Pepa.Korek", "(+420) 754 865 000", "154879833", born);
        try{
            manager.createGuest(wrongGuest);
            fail();
        }catch(IllegalArgumentException ex){ }
        
        //guest's name wrong format - name contains at sign
        wrongGuest = newGuest("Pepa@Korek", "(+420) 754 865 000", "154879833", born);
        try{
            manager.createGuest(wrongGuest);
            fail();
        }catch(IllegalArgumentException ex){ }
        
        //guest's name wrong format - name contains colon
        wrongGuest = newGuest("Pepa:Korek", "(+420) 754 865 000", "154879833", born);
        try{
            manager.createGuest(wrongGuest);
            fail();
        }catch(IllegalArgumentException ex){ }
        
        //incorrect phone number format - phone number contains letters
        wrongGuest = newGuest("Pepa Korek", "(+420) 754 and 000", "154879833", born);
        try{
            manager.createGuest(wrongGuest);
            fail();
        }catch(IllegalArgumentException ex){ }
        
        //incorrect phone number format - only one '-'
        wrongGuest = newGuest("Pepa Korek", "(+420) 754-865 000", "154879833", born);
        try{
            manager.createGuest(wrongGuest);
            fail();
        }catch(IllegalArgumentException ex){ }
        
        //incorrect phone number format - empty string
        wrongGuest = newGuest("Pepa Korek", "", "154879833", born);
        try{
            manager.createGuest(wrongGuest);
            fail();
        }catch(IllegalArgumentException ex){ }
        
        //guest with an empty idCardNumber
        wrongGuest = newGuest("Pepa Korek", "(+420) 754 865 000", "", born);
        try{
            manager.createGuest(wrongGuest);
            fail();
        }catch(IllegalArgumentException ex){ }
        
        //guest without an idCardNumber
        wrongGuest = newGuest("Pepa Korek", "(+420) 754 865 000", null, born);
        try{
            manager.createGuest(wrongGuest);
            fail();
        }catch(IllegalArgumentException ex){ }
        
        //guest with no specified date-of-birth
        born = null;
        wrongGuest = newGuest("Pepa Korek", "(+420) 754 865 000", "154879833", born);
        try{
            manager.createGuest(wrongGuest);
            fail();
        }catch(IllegalArgumentException ex){ }
    }
    
    @Test
    public void createGuestWithAnotherValidAttributes(){
        Date born = newBornDate(64_800_000_000l);
        
        //name with hyphen
        Guest expGuest = newGuest("John Dwight-Cannady", "754 865 000", "154879833", born);
        manager.createGuest(expGuest);
        Guest actGuest = manager.getGuestById(expGuest.getId());
        assertNotNull(actGuest);
        
        //phone number without country calling code
        expGuest = newGuest("Pepa Korek", "754 865 000", "154879833", born);
        manager.createGuest(expGuest);
        actGuest = manager.getGuestById(expGuest.getId());
        assertNotNull(actGuest);
        
        //phone number format containing '-'
        expGuest = newGuest("Pepa Korek", "(+420)-754-865-000", "154879833", born);
        manager.createGuest(expGuest);
        actGuest = manager.getGuestById(expGuest.getId());
        assertNotNull(actGuest);
        
        //phone number format containing '-' without county calling code
        expGuest = newGuest("Pepa Korek", "754-865-000", "154879833", born);
        manager.createGuest(expGuest);
        actGuest = manager.getGuestById(expGuest.getId());
        assertNotNull(actGuest);
        
        //no phone number specified
        expGuest = newGuest("Pepa Korek", null, "154879833", born);
        manager.createGuest(actGuest);
        actGuest = manager.getGuestById(expGuest.getId());
        assertNotNull(actGuest);
    }
    
    @Test
    public void getGuestById(){
        assertNull(manager.getGuestById(1L));
        
        Date born = newBornDate(64_800_000_000l);
        Guest expGuest = newGuest("Pepa Korek", "(+420) 754 865 000", "154879833", born);
        manager.createGuest(expGuest);
        Long guestId = expGuest.getId();
        
        Guest actGuest = manager.getGuestById(guestId);
        assertEquals(expGuest, actGuest);
        assertAllAttributesEquals(expGuest, actGuest);
    }
    
    @Test
    public void findGuestByName(){
        assertTrue(manager.findAllGuests().isEmpty());
        
        Date born = newBornDate(64_800_000_000l);
        Guest expGuest1 = newGuest("Pepa Korek", "(+420) 754 865 000", "154879833", born);
        manager.createGuest(expGuest1);
        born = newBornDate(0l);
        Guest expGuest2 = newGuest("Pepa Korek", null, "258879123", born);
        manager.createGuest(expGuest2);
        
        assertEquals(expGuest1,expGuest2);
        String guestName = expGuest1.getName();
        
        List<Guest> expGuests = Arrays.asList(expGuest1,expGuest2);
        List<Guest> actGuests = manager.findGuestByName(guestName);
        
        assertEquals(expGuests,actGuests);
        assertAllAttributesEquals(expGuests,actGuests);
    }
    
    @Test
    public void findAllGuests(){
        assertTrue(manager.findAllGuests().isEmpty());
        
        Date born = newBornDate(64_800_000_000l);
        Guest expGuest1 = newGuest("Pepa Korek", "(+420) 754 865 000", "154879833", born);
        born = newBornDate(0l);
        Guest expGuest2 = newGuest("Dion Xen Chan", null, "144780025", born);
        born = newBornDate(-14_000l);
        Guest expGuest3 = newGuest("Leonard Kantor", "666 878 321", "654001548", born);
        
        manager.createGuest(expGuest1);
        manager.createGuest(expGuest2);
        manager.createGuest(expGuest3);
        
        List<Guest> expGuests = Arrays.asList(expGuest1,expGuest2,expGuest3);
        List<Guest> actGuests = manager.findAllGuests();
        
        Collections.sort(expGuests);
        Collections.sort(actGuests);
        
        assertEquals(expGuests,actGuests);
        assertAllAttributesEquals(expGuests,actGuests);
    }
    
    @Test
    public void updateGuest(){
        Date born = newBornDate(64_800_000_000l);
        Guest guest1 = newGuest("Pepa Korek", "(+420) 754 865 000", "154879833", born);
        born = newBornDate(0l);
        Guest guest2 = newGuest("Leonard Kantor", "666-878-321", "654001548", born);        
        
        manager.createGuest(guest1);
        manager.createGuest(guest2);
        Long guestId = guest1.getId();
        born = newBornDate(64_800_000_000l);
        
        //update guest's name test1
        guest1 = manager.getGuestById(guestId);
        guest1.setName("John Dwight-Cannady");
        manager.updateGuest(guest1);        
        assertAllAttributesEquals(newGuest("John Dwight-Cannady","(+420) 754 865 000", "154879833",born),
                         manager.getGuestById(guestId));
        
        //update guest's name test2
        guest1 = manager.getGuestById(guestId);
        guest1.setName("Karel Turek");
        manager.updateGuest(guest1);        
        assertAllAttributesEquals(newGuest("Karel Turek","(+420) 754 865 000", "154879833",born),
                         manager.getGuestById(guestId));
        
        //update guest's phone number test1
        guest1 = manager.getGuestById(guestId);
        guest1.setPhone("989 487 544");
        manager.updateGuest(guest1);
        assertAllAttributesEquals(newGuest("Karel Turek","989 487 544","154879833",born),
                         manager.getGuestById(guestId));
        
        //update guest's phone number test2
        guest1 = manager.getGuestById(guestId);
        guest1.setPhone(null);
        manager.updateGuest(guest1);
        assertAllAttributesEquals(newGuest("Karel Turek",null,"154879833",born),
                         manager.getGuestById(guestId));
        
        //update guest's identification card number test
        guest1 = manager.getGuestById(guestId);
        guest1.setIdCardNum("555000555");
        manager.updateGuest(guest1);
        assertAllAttributesEquals(newGuest("Karel Turek",null,"555000555",born),
                         manager.getGuestById(guestId));
        
        //update guest's date-of-birth test        
        guest1 = manager.getGuestById(guestId);
        guest1.setBorn(newBornDate(-14_000l));
        born = newBornDate(-14_000l);
        manager.updateGuest(guest1);
        assertAllAttributesEquals(newGuest("Karel Turek",null,"555000555",born),
                         manager.getGuestById(guestId));
        
        //test whether or not guest2 has been modified
        assertAllAttributesEquals(guest2,manager.getGuestById(guest2.getId()));
    }
    
    @Test
    public void updateGuestWithInvalidAttributes(){
        Date born = newBornDate(64_800_000_000l);
        Guest guest = newGuest("Pepa Korek", "(+420) 754 865 000", "154879833", born);
        Long guestId = guest.getId();
        manager.createGuest(guest);
        
        //no guest (object) to update
        try{
            manager.updateGuest(null);
            fail();
        }catch(IllegalArgumentException ex){ }
        
        //guest with no id to update
        try{
            guest = manager.getGuestById(guestId);
            guest.setId(null);
            manager.updateGuest(guest);
            fail();
        }catch(IllegalArgumentException ex){ }
        
        //guest with wrong (modified) id
        try{
            guest = manager.getGuestById(guestId);
            guest.setId(guestId - 1);
            manager.updateGuest(guest);
            fail();
        }catch(IllegalArgumentException ex){ }
        
        //guest with no specified name
        try{
            guest = manager.getGuestById(guestId);
            guest.setName(null);
            manager.updateGuest(guest);
            fail();
        }catch(IllegalArgumentException ex){ }
        
        //guest with wrong name format - empty name
        try{
            guest = manager.getGuestById(guestId);
            guest.setName("");
            manager.updateGuest(guest);
            fail();
        }catch(IllegalArgumentException ex){ }
        
        //guest with wrong name format - illegal characters -> test1 - numbers
        try{
            guest = manager.getGuestById(guestId);
            guest.setName("Pepa4 5Korek5");
            manager.updateGuest(guest);
            fail();
        }catch(IllegalArgumentException ex){ }
        
        //guest with wrong name format - illegal characters -> test2 - underscore
        try{
            guest = manager.getGuestById(guestId);
            guest.setName("Pepa_Korek");
            manager.updateGuest(guest);
            fail();
        }catch(IllegalArgumentException ex){ }
        
        //guest with wrong name format - illegal characters -> test3 - dot
        try{
            guest = manager.getGuestById(guestId);
            guest.setName("Pepa.Korek");
            manager.updateGuest(guest);
            fail();
        }catch(IllegalArgumentException ex){ }
        
        //guest with wrong name format - illegal characters -> test4 - at sign
        try{
            guest = manager.getGuestById(guestId);
            guest.setName("Pepa@Korek");
            manager.updateGuest(guest);
            fail();
        }catch(IllegalArgumentException ex){ }
        
        //guest with wrong name format - illegal characters -> test5 - colon
        try{
            guest = manager.getGuestById(guestId);
            guest.setName("Pepa:Korek");
            manager.updateGuest(guest);
            fail();
        }catch(IllegalArgumentException ex){ }
        
        //guest with wrong phone format - empty
        try{
            guest = manager.getGuestById(guestId);
            guest.setPhone("");
            manager.updateGuest(guest);
            fail();
        }catch(IllegalArgumentException ex){ }
        
        //guest with wrong phone format - wrong usage of hyphen
        try{
            guest = manager.getGuestById(guestId);
            guest.setPhone("(+420) 754-865 000");
            manager.updateGuest(guest);
            fail();
        }catch(IllegalArgumentException ex){ }
        
        //guest with wrong phone format - illegal characters - letters
        try{
            guest = manager.getGuestById(guestId);
            guest.setPhone("(+420) 754 and 000");
            manager.updateGuest(guest);
            fail();
        }catch(IllegalArgumentException ex){ }
        
        //guest with wrong idCardNum format - empty
        try{
            guest = manager.getGuestById(guestId);
            guest.setIdCardNum("");
            manager.updateGuest(guest);
            fail();
        }catch(IllegalArgumentException ex){ }
        
        //guest with no specified idCardNum
        try{
            guest = manager.getGuestById(guestId);
            guest.setIdCardNum(null);
            manager.updateGuest(guest);
            fail();
        }catch(IllegalArgumentException ex){ }
        
        //guest with no specified date-of-birth
        try{
            guest = manager.getGuestById(guestId);
            guest.setBorn(null);
            manager.updateGuest(guest);
            fail();
        }catch(IllegalArgumentException ex){ }
    }
    
    @Test
    public void deleteGuest(){
        Date born = newBornDate(64_800_000_000l);
        Guest guest1 = newGuest("Pepa Korek", "(+420) 754 865 000", "154879833", born);
        born = newBornDate(0l);
        Guest guest2 = newGuest("Leonard Kantor", "666-878-321", "654001548", born);
        
        manager.createGuest(guest1);
        manager.createGuest(guest2);
        
        assertNotNull(manager.getGuestById(guest1.getId()));
        assertNotNull(manager.getGuestById(guest2.getId()));
        
        manager.deleteGuest(guest1);
        
        assertNull(manager.getGuestById(guest1.getId()));
        assertNotNull(manager.getGuestById(guest2.getId()));
    }
    
    @Test
    public void deleteGuestWithInvalidAttributes(){
        Date born = newBornDate(64_800_000_000l);
        Guest guest = newGuest("Pepa Korek", "(+420) 754 865 000", "154879833", born);
        Long guestId = guest.getId();
        manager.createGuest(guest);
        
        try{
            manager.deleteGuest(null);
            fail();
        }catch(IllegalArgumentException ex) { }
        
        try{
            guest.setId(null);
            manager.deleteGuest(guest);
            fail();
        }catch(IllegalArgumentException ex) { }
        
        try{
            guest.setId(1L);
            manager.deleteGuest(guest);
            fail();
        }catch(IllegalArgumentException ex) { }
    }
    
    
    
    private static Guest newGuest(String name, String phone, String idCardNum, Date born){
        Guest guest = new Guest();
        guest.setName(name);
        guest.setPhone(phone);
        guest.setIdCardNum(idCardNum);
        guest.setBorn(born);
        
        return guest;
    }
    
    private static Date newBornDate(long timeMillis){
        return new Date(timeMillis);
    }
    
    private void assertAllAttributesEquals(List<Guest> expGuests, List<Guest> actGuests){
        for(int i = 0; i < expGuests.size(); ++i){
            Guest expGuest = expGuests.get(i);
            Guest actGuest = actGuests.get(i);
            assertAllAttributesEquals(expGuest,actGuest);
        }
    }
    
    private void assertAllAttributesEquals(Guest expGuest, Guest actGuest) {
        assertEquals(expGuest.getId(), actGuest.getId());
        assertEquals(expGuest.getName(), actGuest.getName());
        assertEquals(expGuest.getIdCardNum(), actGuest.getIdCardNum());
        assertEquals(expGuest.getPhone(), actGuest.getPhone());
        assertEquals(expGuest.getBorn().getTime(), actGuest.getBorn().getTime());
    }
}
