/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv168.hotelmanager.backend;

import java.util.List;

/**
 *
 * @author Ondra
 */
public interface IRoomManager {

    void createRoom(Room room) throws ServiceFailureException;
    
    Room getRoomById(Long id) throws ServiceFailureException;
    
    void updateRoom(Room room) throws ServiceFailureException;
    
    void deleteRoom(Room room) throws ServiceFailureException;
    
    List<Room> findAllRooms() throws ServiceFailureException;
}
