/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv168.hotelmanager.backend;

/**
 *
 * @author Tomáš Šmíd
 */
public class DuplicateRoomException extends RuntimeException{
    
    public DuplicateRoomException(String msg) {
        super(msg);
    }

    public DuplicateRoomException(Throwable cause) {
        super(cause);
    }

    public DuplicateRoomException(String message, Throwable cause) {
        super(message, cause);
    }
}
