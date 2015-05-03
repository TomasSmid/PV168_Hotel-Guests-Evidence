/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv168.hotelmanager.backend;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 *
 * @author Tomas Smid
 */
public class TimeManagerImpl implements TimeManager{
    
    public TimeManagerImpl(){
        
    }
    
    @Override
    public Date getCurrentDate(){
        LocalDate currentDate = LocalDate.now(ZoneId.systemDefault());
        Instant instant = currentDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
        Date date = Date.from(instant);
        
        return date;
    }
}
