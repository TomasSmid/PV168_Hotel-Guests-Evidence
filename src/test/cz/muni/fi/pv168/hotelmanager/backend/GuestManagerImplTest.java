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
import org.junit.Rule;
import org.junit.rules.ExpectedException;

/**
 * This test class ensures testing of units of class GuestManagerImpl.
 * 
 * @author Tomas Smid
 */
public class GuestManagerImplTest {
    
    @Rule public ExpectedException exception = ExpectedException.none();
    
    private GuestManagerImpl manager;
    
    @Before
    public void setUp() {
        manager = new GuestManagerImpl();
    }
    
    
    //tests focused on storing guest in DB with valid guest's attributes
    @Test
    public void createGuestAndCheckIdGenerated(){
        Guest guest = new GuestBuilder().build();
        
        manager.createGuest(guest);
        
        Long guestId = guest.getId();
        assertNotNull("Guest's id should have a particular value, not be null", guestId);
    }    
    
    @Test
    public void createGuestWithValidAttributesAndNoSpecialChars(){
        Guest expGuest = new GuestBuilder().build();
        
        manager.createGuest(expGuest);
        Long guestId = expGuest.getId();
        
        Guest actGuest = manager.getGuestById(guestId);
        assertEquals(expGuest, actGuest);
        assertNotSame(expGuest, actGuest);
        assertAllAttributesEquals(expGuest, actGuest);        
    }
    
    @Test
    public void createGuestWithNameContainingHyphen(){
        Guest guest = new GuestBuilder().name("John Dwight-Cannady").build();
        
        manager.createGuest(guest);
        Long guestId = guest.getId();
        
        Guest actGuest = manager.getGuestById(guestId);
        assertNotNull(actGuest);
    }
    
    @Test
    public void createGuestWithPhoneNotContainingCountryCode(){
        Guest guest = new GuestBuilder().phone("777 888 999").build();
        
        manager.createGuest(guest);
        Long guestId = guest.getId();
        
        Guest actGuest = manager.getGuestById(guestId);
        assertNotNull(actGuest);
    }
    
    @Test
    public void createGuestWithPhoneContainingRightNumOfHyphens(){
        Guest guest = new GuestBuilder().phone("(+420)-777-888-999").build();
        
        manager.createGuest(guest);
        Long guestId = guest.getId();
        
        Guest actGuest = manager.getGuestById(guestId);
        assertNotNull(actGuest);
    }
    
    @Test
    public void createGuestWithPhoneContainingRightNumOfHyphensAndNoCountryCode(){
        Guest guest = new GuestBuilder().phone("777-888-999").build();
        
        manager.createGuest(guest);
        Long guestId = guest.getId();
        
        Guest actGuest = manager.getGuestById(guestId);
        assertNotNull(actGuest);
    }
    
    @Test
    public void createGuestWithoutPhone(){
        Guest guest = new GuestBuilder().phone(null).build();
        
        manager.createGuest(guest);
        Long guestId = guest.getId();
        
        Guest actGuest = manager.getGuestById(guestId);
        assertNotNull(actGuest);
    }
    
    
    //tests focused on storing guest in DB with invalid guest's attributes
    @Test
    public void createGuestWithNullGuest(){
        exception.expect(IllegalArgumentException.class);
        manager.createGuest(null);
    }
    
    @Test
    public void createGuestWithPresetId(){
        Guest guest = new GuestBuilder().build();
        guest.setId(1L);
        
        exception.expect(IllegalArgumentException.class);
        manager.createGuest(guest);
    }
    
    @Test
    public void createGuestWithEmptyName(){
        Guest guest = new GuestBuilder().name("").build();
        
        exception.expect(IllegalArgumentException.class);
        manager.createGuest(guest);
    }
    
    @Test
    public void createGuestWithNullName(){
        Guest guest = new GuestBuilder().name(null).build();
        
        exception.expect(IllegalArgumentException.class);
        manager.createGuest(guest);
    }
    
    @Test
    public void createGuestWithNameContainingNumbers(){
        Guest guest = new GuestBuilder().name("Pepa4 5Korek5").build();
        
        exception.expect(IllegalArgumentException.class);
        manager.createGuest(guest);
    }
    
    @Test
    public void createGuestWithNameContainingUnderscore(){
        Guest guest = new GuestBuilder().name("Pepa_Korek").build();
        
        exception.expect(IllegalArgumentException.class);
        manager.createGuest(guest);
    }
    
    @Test
    public void createGuestWithNameContainingDot(){
        Guest guest = new GuestBuilder().name("Pepa.Korek").build();
        
        exception.expect(IllegalArgumentException.class);
        manager.createGuest(guest);
    }
    
    @Test
    public void createGuestWithNameContainingAtSign(){
        Guest guest = new GuestBuilder().name("Pepa@Korek").build();
        
        exception.expect(IllegalArgumentException.class);
        manager.createGuest(guest);
    }
    
    @Test
    public void createGuestWithNameContainingColon(){
        Guest guest = new GuestBuilder().name("Pepa:Korek").build();
        
        exception.expect(IllegalArgumentException.class);
        manager.createGuest(guest);
    }
    
    @Test
    public void createGuestWithNameContainingMoreThanOneSpaceBetweenNames(){
        Guest guest = new GuestBuilder().name("Pepa  Korek").build();
        
        exception.expect(IllegalArgumentException.class);
        manager.createGuest(guest);
    }
    
    @Test
    public void createGuestWithPhoneContainingLetters(){
        Guest guest = new GuestBuilder().phone("(+420) 777 and 999").build();
        
        exception.expect(IllegalArgumentException.class);
        manager.createGuest(guest);
    }
    
    @Test
    public void createGuestWithPhoneContainingBadNumOfHyphen(){
        Guest guest = new GuestBuilder().phone("(+420) 777-888 999").build();
        
        exception.expect(IllegalArgumentException.class);
        manager.createGuest(guest);
    }
    
    @Test
    public void createGuestWithPhoneContainingColons(){
        Guest guest = new GuestBuilder().phone("(+420):777:888:999").build();
        
        exception.expect(IllegalArgumentException.class);
        manager.createGuest(guest);
    }
    
    @Test
    public void createGuestWithPhoneContainingUnderscores(){
        Guest guest = new GuestBuilder().phone("(+420)_777_888_999").build();
        
        exception.expect(IllegalArgumentException.class);
        manager.createGuest(guest);
    }
    
    @Test
    public void createGuestWithPhoneContainingDots(){
        Guest guest = new GuestBuilder().phone("(+420).777.888.999").build();
        
        exception.expect(IllegalArgumentException.class);
        manager.createGuest(guest);
    }
    
    @Test
    public void createGuestWithEmptyPhoneString(){
        Guest guest = new GuestBuilder().phone("").build();
        
        exception.expect(IllegalArgumentException.class);
        manager.createGuest(guest);
    }
    
    @Test
    public void createGuestWithIdCardNumContainingLetter(){
        Guest guest = new GuestBuilder().idCardNum("123c56789").build();
        
        exception.expect(IllegalArgumentException.class);
        manager.createGuest(guest);
    }
    
    @Test
    public void createGuestWithEmptyIdCardNum(){
        Guest guest = new GuestBuilder().idCardNum("").build();
        
        exception.expect(IllegalArgumentException.class);
        manager.createGuest(guest);
    }
    
    @Test
    public void createGuestWithNullIdCardNum(){
        Guest guest = new GuestBuilder().idCardNum(null).build();
        
        exception.expect(IllegalArgumentException.class);
        manager.createGuest(guest);
    }
    
    @Test
    public void createGuestWithNullDateOfBirth(){
        Guest guest = new GuestBuilder().born(null).build();
        
        exception.expect(IllegalArgumentException.class);
        manager.createGuest(guest);
    }
    
    
    //tests focused on retrieving guest(s) from DB
    @Test
    public void getGuestById(){
        assertNull(manager.getGuestById(1L));
        
        Guest expGuest = new GuestBuilder().build();
        
        manager.createGuest(expGuest);
        Long guestId = expGuest.getId();
        
        Guest actGuest = manager.getGuestById(guestId);
        assertEquals(expGuest, actGuest);
        assertAllAttributesEquals(expGuest, actGuest);
    }
    
    @Test
    public void findGuestByName(){
        assertTrue(manager.findAllGuests().isEmpty());
        
        Guest expGuest1 = new GuestBuilder().build();
        Guest expGuest2 = new GuestBuilder().phone(null).idCardNum("112233445").build();
        
        manager.createGuest(expGuest1);
        manager.createGuest(expGuest2);
        
        assertEquals("Guests should have same names",expGuest1.getName(),expGuest2.getName());
        String guestName = expGuest1.getName();
        
        List<Guest> expGuests = Arrays.asList(expGuest1,expGuest2);
        List<Guest> actGuests = manager.findGuestByName(guestName);
        
        assertEquals("Retrieved guests should be two and same as expected ones",expGuests,actGuests);
        assertAllAttributesEquals(expGuests,actGuests);
    }
    
    @Test
    public void findAllGuests(){
        assertTrue(manager.findAllGuests().isEmpty());
        
        Guest expGuest1 = new GuestBuilder().build();
        Guest expGuest2 = new GuestBuilder().name("Dion Xen Chan").phone(null)
                                            .idCardNum("144780025").born(new Date(0l)).build();
        Guest expGuest3 = new GuestBuilder().name("John Dwight-Cannady").phone("666 878 321")
                                            .idCardNum("654001548").born(new Date(-14_000l)).build();
        
        manager.createGuest(expGuest1);
        manager.createGuest(expGuest2);
        manager.createGuest(expGuest3);
        
        List<Guest> expGuests = Arrays.asList(expGuest1,expGuest2,expGuest3);
        List<Guest> actGuests = manager.findAllGuests();
        
        Collections.sort(expGuests);
        Collections.sort(actGuests);
        
        assertEquals("Retrieved guests should be 3 and same as expected ones",expGuests,actGuests);
        assertAllAttributesEquals(expGuests,actGuests);
    }
    
    
    //tests focused on updating guest with new valid attributes
    @Test
    public void updateGuestWithNameWithoutHyphen(){
        Guest guest1 = new GuestBuilder().build();
        Guest guest2 = new GuestBuilder().name("Leonard Kantor").phone("666-878-321")
                                         .idCardNum("654001548").born(new Date(0l)).build();
        Guest guest3 = new GuestBuilder().name("Karel Turek").build();
        
        manager.createGuest(guest1);
        manager.createGuest(guest2);
        Long guestId = guest1.getId();
        
        guest1.setName(guest3.getName());
        manager.updateGuest(guest1);
        
        assertAllAttributesEquals(guest3,manager.getGuestById(guestId));
        assertAllAttributesEquals(guest2,manager.getGuestById(guest2.getId()));
    }
    
    @Test
    public void updateGuestWithNameWithHyphen(){
        Guest guest1 = new GuestBuilder().build();
        Guest guest2 = new GuestBuilder().name("Leonard Kantor").phone("666-878-321")
                                         .idCardNum("654001548").born(new Date(0l)).build();
        Guest guest3 = new GuestBuilder().name("John Dwight-Cannady").build();
        
        manager.createGuest(guest1);
        manager.createGuest(guest2);
        Long guestId = guest1.getId();
        
        guest1.setName(guest3.getName());
        manager.updateGuest(guest1);
        
        assertAllAttributesEquals(guest3,manager.getGuestById(guestId));
        assertAllAttributesEquals(guest2,manager.getGuestById(guest2.getId()));
    }
    
    @Test
    public void updateGuestWithPhone(){
        Guest guest1 = new GuestBuilder().build();
        Guest guest2 = new GuestBuilder().name("Leonard Kantor").phone("666-878-321")
                                         .idCardNum("654001548").born(new Date(0l)).build();
        Guest guest3 = new GuestBuilder().phone("111 222 333").build();
        
        manager.createGuest(guest1);
        manager.createGuest(guest2);
        Long guestId = guest1.getId();
        
        guest1.setName(guest3.getName());
        manager.updateGuest(guest1);
        
        assertAllAttributesEquals(guest3,manager.getGuestById(guestId));
        assertAllAttributesEquals(guest2,manager.getGuestById(guest2.getId()));
    }
    
    @Test
    public void updateGuestWithPhoneWithHyphens(){
        Guest guest1 = new GuestBuilder().build();
        Guest guest2 = new GuestBuilder().name("Leonard Kantor").phone("666-878-321")
                                         .idCardNum("654001548").born(new Date(0l)).build();
        Guest guest3 = new GuestBuilder().phone("(+458)-111-222-333").build();
        
        manager.createGuest(guest1);
        manager.createGuest(guest2);
        Long guestId = guest1.getId();
        
        guest1.setName(guest3.getName());
        manager.updateGuest(guest1);
        
        assertAllAttributesEquals(guest3,manager.getGuestById(guestId));
        assertAllAttributesEquals(guest2,manager.getGuestById(guest2.getId()));
    }
    
    @Test
    public void updateGuestWithNullPhone(){
        Guest guest1 = new GuestBuilder().build();
        Guest guest2 = new GuestBuilder().name("Leonard Kantor").phone("666-878-321")
                                         .idCardNum("654001548").born(new Date(0l)).build();
        Guest guest3 = new GuestBuilder().phone(null).build();
        
        manager.createGuest(guest1);
        manager.createGuest(guest2);
        Long guestId = guest1.getId();
        
        guest1.setName(guest3.getName());
        manager.updateGuest(guest1);
        
        assertAllAttributesEquals(guest3,manager.getGuestById(guestId));
        assertAllAttributesEquals(guest2,manager.getGuestById(guest2.getId()));
    }
    
    @Test
    public void updateGuestWithIdCardNum(){
        Guest guest1 = new GuestBuilder().build();
        Guest guest2 = new GuestBuilder().name("Leonard Kantor").phone("666-878-321")
                                         .idCardNum("654001548").born(new Date(0l)).build();
        Guest guest3 = new GuestBuilder().idCardNum("555444123").build();
        
        manager.createGuest(guest1);
        manager.createGuest(guest2);
        Long guestId = guest1.getId();
        
        guest1.setName(guest3.getName());
        manager.updateGuest(guest1);
        
        assertAllAttributesEquals(guest3,manager.getGuestById(guestId));
        assertAllAttributesEquals(guest2,manager.getGuestById(guest2.getId()));
    }
    
    @Test
    public void updateGuestWithDateOfBirth(){
        Guest guest1 = new GuestBuilder().build();
        Guest guest2 = new GuestBuilder().name("Leonard Kantor").phone("666-878-321")
                                         .idCardNum("654001548").born(new Date(0l)).build();
        Guest guest3 = new GuestBuilder().born(new Date(-14_000l)).build();
        
        manager.createGuest(guest1);
        manager.createGuest(guest2);
        Long guestId = guest1.getId();
        
        guest1.setName(guest3.getName());
        manager.updateGuest(guest1);
        
        assertAllAttributesEquals(guest3,manager.getGuestById(guestId));
        assertAllAttributesEquals(guest2,manager.getGuestById(guest2.getId()));
    }
    
    
    //tests focused on updating guest with new invalid attributes
    @Test
    public void updateGuestWithNullGuest(){
        Guest guest = new GuestBuilder().build();
        
        manager.createGuest(guest);
        
        exception.expect(IllegalArgumentException.class);
        manager.updateGuest(null);
    }
    
    @Test
    public void updateGuestWithNullId(){
        Guest guest = new GuestBuilder().build();
        
        manager.createGuest(guest);
        Long guestId = guest.getId();
        
        guest = manager.getGuestById(guestId);
        guest.setId(null);
        
        exception.expect(IllegalArgumentException.class);
        manager.updateGuest(guest);
    }
    
    @Test
    public void updateGuestWithModifiedId(){
        Guest guest = new GuestBuilder().build();
        
        manager.createGuest(guest);
        Long guestId = guest.getId();
        
        guest = manager.getGuestById(guestId);
        guest.setId(guestId - 1);
        
        exception.expect(IllegalArgumentException.class);
        manager.updateGuest(guest);
    }
    
    @Test
    public void updateGuestWithNullName(){
        Guest guest = new GuestBuilder().build();
        
        manager.createGuest(guest);
        Long guestId = guest.getId();
        
        guest = manager.getGuestById(guestId);
        guest.setName(null);
        
        exception.expect(IllegalArgumentException.class);
        manager.updateGuest(guest);
    }
    
    @Test
    public void updateGuestWithEmptyNameString(){
        Guest guest = new GuestBuilder().build();
        
        manager.createGuest(guest);
        Long guestId = guest.getId();
        
        guest = manager.getGuestById(guestId);
        guest.setName("");
        
        exception.expect(IllegalArgumentException.class);
        manager.updateGuest(guest);
    }
    
    @Test
    public void updateGuestWithNameContainingNumbers(){
        Guest guest = new GuestBuilder().build();
        
        manager.createGuest(guest);
        Long guestId = guest.getId();
        
        guest = manager.getGuestById(guestId);
        guest.setName("Pepa4 5Korek5");
        
        exception.expect(IllegalArgumentException.class);
        manager.updateGuest(guest);
    }
    
    @Test
    public void updateGuestWithNameContainingUnderscore(){
        Guest guest = new GuestBuilder().build();
        
        manager.createGuest(guest);
        Long guestId = guest.getId();
        
        guest = manager.getGuestById(guestId);
        guest.setName("Pepa_Korek");
        
        exception.expect(IllegalArgumentException.class);
        manager.updateGuest(guest);
    }
    
    @Test
    public void updateGuestWithNameContainingDot(){
        Guest guest = new GuestBuilder().build();
        
        manager.createGuest(guest);
        Long guestId = guest.getId();
        
        guest = manager.getGuestById(guestId);
        guest.setName("Pepa.Korek");
        
        exception.expect(IllegalArgumentException.class);
        manager.updateGuest(guest);
    }
    
    @Test
    public void updateGuestWithNameContainingAtSign(){
        Guest guest = new GuestBuilder().build();
        
        manager.createGuest(guest);
        Long guestId = guest.getId();
        
        guest = manager.getGuestById(guestId);
        guest.setName("Pepa@Korek");
        
        exception.expect(IllegalArgumentException.class);
        manager.updateGuest(guest);
    }
    
    @Test
    public void updateGuestWithNameContainingColon(){
        Guest guest = new GuestBuilder().build();
        
        manager.createGuest(guest);
        Long guestId = guest.getId();
        
        guest = manager.getGuestById(guestId);
        guest.setName("Pepa:Korek");
        
        exception.expect(IllegalArgumentException.class);
        manager.updateGuest(guest);
    }
    
    @Test
    public void updateGuestWithNameContainingMoreThanOneSpaceBetweenNames(){
        Guest guest = new GuestBuilder().build();
        
        manager.createGuest(guest);
        Long guestId = guest.getId();
        
        guest = manager.getGuestById(guestId);
        guest.setName("Pepa  Korek");
        
        exception.expect(IllegalArgumentException.class);
        manager.updateGuest(guest);
    }
    
    @Test
    public void updateGuestWithEmptyPhone(){
        Guest guest = new GuestBuilder().build();
        
        manager.createGuest(guest);
        Long guestId = guest.getId();
        
        guest = manager.getGuestById(guestId);
        guest.setPhone("");
        
        exception.expect(IllegalArgumentException.class);
        manager.updateGuest(guest);
    }
    
    @Test
    public void updateGuestWithPhoneContainingWrongNumOfHyphen(){
        Guest guest = new GuestBuilder().build();
        
        manager.createGuest(guest);
        Long guestId = guest.getId();
        
        guest = manager.getGuestById(guestId);
        guest.setPhone("(+420) 777-888 999");
        
        exception.expect(IllegalArgumentException.class);
        manager.updateGuest(guest);
    }
    
    @Test
    public void updateGuestWithPhoneContainingLetters(){
        Guest guest = new GuestBuilder().build();
        
        manager.createGuest(guest);
        Long guestId = guest.getId();
        
        guest = manager.getGuestById(guestId);
        guest.setPhone("(+420) 777 and 999");
        
        exception.expect(IllegalArgumentException.class);
        manager.updateGuest(guest);
    }
    
    @Test
    public void updateGuestWithPhoneContainingUnderscores(){
        Guest guest = new GuestBuilder().build();
        
        manager.createGuest(guest);
        Long guestId = guest.getId();
        
        guest = manager.getGuestById(guestId);
        guest.setPhone("(+420)_777_888_999");
        
        exception.expect(IllegalArgumentException.class);
        manager.updateGuest(guest);
    }
    
    @Test
    public void updateGuestWithPhoneContainingDots(){
        Guest guest = new GuestBuilder().build();
        
        manager.createGuest(guest);
        Long guestId = guest.getId();
        
        guest = manager.getGuestById(guestId);
        guest.setPhone("(+420).777.888.999");
        
        exception.expect(IllegalArgumentException.class);
        manager.updateGuest(guest);
    }
    
    @Test
    public void updateGuestWithPhoneContainingColons(){
        Guest guest = new GuestBuilder().build();
        
        manager.createGuest(guest);
        Long guestId = guest.getId();
        
        guest = manager.getGuestById(guestId);
        guest.setPhone("(+420):777:888:999");
        
        exception.expect(IllegalArgumentException.class);
        manager.updateGuest(guest);
    }
    
    @Test
    public void updateGuestWithEmptyIdCardNum(){
        Guest guest = new GuestBuilder().build();
        
        manager.createGuest(guest);
        Long guestId = guest.getId();
        
        guest = manager.getGuestById(guestId);
        guest.setIdCardNum("");
        
        exception.expect(IllegalArgumentException.class);
        manager.updateGuest(guest);
    }
    
    @Test
    public void updateGuestWithNullIdCardNum(){
        Guest guest = new GuestBuilder().build();
        
        manager.createGuest(guest);
        Long guestId = guest.getId();
        
        guest = manager.getGuestById(guestId);
        guest.setIdCardNum(null);
        
        exception.expect(IllegalArgumentException.class);
        manager.updateGuest(guest);
    }
    
    @Test
    public void updateGuestWithIdCardNumContainingLetter(){
        Guest guest = new GuestBuilder().build();
        
        manager.createGuest(guest);
        Long guestId = guest.getId();
        
        guest = manager.getGuestById(guestId);
        guest.setIdCardNum("123c56789");
        
        exception.expect(IllegalArgumentException.class);
        manager.updateGuest(guest);
    }
    
    @Test
    public void updateGuestWithNullDateOfBirth(){
        Guest guest = new GuestBuilder().build();
        
        manager.createGuest(guest);
        Long guestId = guest.getId();
        
        guest = manager.getGuestById(guestId);
        guest.setBorn(null);
        
        exception.expect(IllegalArgumentException.class);
        manager.updateGuest(guest);
    }
    
    
    //test focused on deleting valid guest from DB
    @Test
    public void deleteGuest(){
        Guest guest1 = new GuestBuilder().build();
        Guest guest2 = new GuestBuilder().name("Leonard Kantor").phone("666-878-321")
                                         .idCardNum("654001548").born(new Date(0l)).build();
        
        manager.createGuest(guest1);
        manager.createGuest(guest2);
        
        assertNotNull(manager.getGuestById(guest1.getId()));
        assertNotNull(manager.getGuestById(guest2.getId()));
        
        manager.deleteGuest(guest1);
        
        assertNull(manager.getGuestById(guest1.getId()));
        assertNotNull(manager.getGuestById(guest2.getId()));
    }
    
    
    //tests focused on deleting invalid guest from DB
    @Test
    public void deleteGuestWithNullGuest(){
        Guest guest = new GuestBuilder().build();
        
        manager.createGuest(guest);
        
        exception.expect(IllegalArgumentException.class);
        manager.deleteGuest(null);
    }
    
    @Test
    public void deleteGuestWithNullId(){
        Guest guest = new GuestBuilder().build();
        
        manager.createGuest(guest);
        Long guestId = guest.getId();
        
        guest = manager.getGuestById(guestId);
        guest.setId(null);
        
        exception.expect(IllegalArgumentException.class);
        manager.deleteGuest(guest);
    }
    
    @Test
    public void deleteGuestWithModifiedId(){
        Guest guest = new GuestBuilder().build();
        
        manager.createGuest(guest);
        Long guestId = guest.getId();
        
        guest = manager.getGuestById(guestId);
        guest.setId(1L);
        
        exception.expect(IllegalArgumentException.class);
        manager.deleteGuest(guest);
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

class GuestBuilder{    
    private String name = "Pepa Korek";
    private String phone = "(+420) 777 888 999";
    private String idCardNum = "123456789";    
    private Date born = new Date(64_800_000_000l);
    
    public GuestBuilder(){
        
    }
    
    public GuestBuilder name(String value){
        this.name = value;
        return this;
    }
    
    public GuestBuilder phone(String value){
        this.phone = value;
        return this;
    }
    
    public GuestBuilder idCardNum(String value){
        this.idCardNum = value;
        return this;
    }
    
    public GuestBuilder born(Date value){
        if(value == null)
            this.born = null;
        else
            this.born = new Date(value.getTime());
        return this;
    }
    
    public Guest build(){
        Guest guest = new Guest();
        guest.setName(this.name);
        guest.setPhone(this.phone);
        guest.setIdCardNum(this.idCardNum);
        guest.setBorn(this.born);
        
        return guest;
    }
}
