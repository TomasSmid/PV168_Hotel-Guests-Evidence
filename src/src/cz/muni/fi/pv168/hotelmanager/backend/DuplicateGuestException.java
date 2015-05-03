/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv168.hotelmanager.backend;

/**
 *
 * @author Tom
 */
public class DuplicateGuestException extends RuntimeException{
    
    public DuplicateGuestException(String msg) {
        super(msg);
    }

    public DuplicateGuestException(Throwable cause) {
        super(cause);
    }

    public DuplicateGuestException(String message, Throwable cause) {
        super(message, cause);
    }
}
