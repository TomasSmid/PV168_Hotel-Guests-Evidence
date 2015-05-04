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
public interface RoomManager {

    void createRoom(Room room) throws ServiceFailureException;
    
    Room getRoomById(Long id) throws ServiceFailureException;
    
    Room getRoomByNumber(String number) throws ServiceFailureException;
    
    void updateRoom(Room room) throws ServiceFailureException;
    
    void deleteRoom(Room room) throws ServiceFailureException;
    
    List<Room> findAllRooms() throws ServiceFailureException;
    
    List<Room> findRoomsWithType(RoomType type) throws ServiceFailureException;
    
    List<Room> findRoomsWithCapacity(int capacity) throws ServiceFailureException;
}
