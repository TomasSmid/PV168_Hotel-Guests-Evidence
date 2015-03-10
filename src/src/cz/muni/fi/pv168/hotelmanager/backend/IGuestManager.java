/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.muni.fi.pv168.hotelmanager.backend;

import java.util.List;

/**
 * This interface represents Guest manager. This manager operates above database
 * and enables creating a new guest, updating existed guest and searching
 * of a specified guest in and deleting guest from this database.
 * 
 * @author Tomas Smid
 */
public interface IGuestManager {
    
    public void createGuest(Guest guest);
    
    public void updateGuest(Guest guest);
    
    public void deleteGuest(Guest guest);
    
    public Guest getGuestById(Long id);
    
    public List<Guest> findAllGuests();
    
    public List<Guest> findGuestByName(String name);
}
