/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv168.hotelmanager.frontend;

import cz.muni.fi.pv168.common.HGEConfig;
import cz.muni.fi.pv168.hotelmanager.backend.Guest;
import cz.muni.fi.pv168.hotelmanager.backend.GuestManagerImpl;
import cz.muni.fi.pv168.hotelmanager.backend.Reservation;
import cz.muni.fi.pv168.hotelmanager.backend.ReservationManager;
import cz.muni.fi.pv168.hotelmanager.backend.ReservationManagerImpl;
import cz.muni.fi.pv168.hotelmanager.backend.Room;
import cz.muni.fi.pv168.hotelmanager.backend.TimeManagerImpl;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Tom
 */
public class ReservationsTableModel extends AbstractTableModel{
    
    private static final Logger logger = Logger.getLogger("myLogger");
    
    private List<Reservation> reservations = new ArrayList<>();
    private List<BigDecimal> resPrices = new ArrayList<>();
    private DataSource dataSource;

    public ReservationsTableModel(){
        dataSource = setUpDataSource();
    }
    
    @Override
    public int getRowCount() {
        return reservations.size();
    }

    @Override
    public int getColumnCount() {
        return 9;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Locale locale = Locale.getDefault();
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, locale);
        NumberFormat numFormat = NumberFormat.getCurrencyInstance(locale);
        Reservation reservation = reservations.get(rowIndex);
        BigDecimal price = resPrices.get(rowIndex);
        switch(columnIndex){
            case 0: return rowIndex+1;
            case 1: return reservation.getRoom().getNumber();
            case 2: return reservation.getGuest().getName();
            case 3: return reservation.getGuest().getIdCardNum();
            case 4: return dateFormat.format(reservation.getStartTime());
            case 5: return dateFormat.format(reservation.getExpectedEndTime());
            case 6: return (reservation.getRealEndTime() == null ? null : dateFormat.format(reservation.getRealEndTime()));
            case 7: return numFormat.format(price);
            case 8: return numFormat.format(reservation.getServicesSpendings());
            default: throw new IllegalArgumentException("Invalid column index in table.");
        }
    }
    
    public void addReservation(Reservation reservation){
        ReservationManager reservationManager = new ReservationManagerImpl(dataSource, new TimeManagerImpl());
        reservations.add(reservation);
        resPrices.add(reservationManager.getReservationPrice(reservation));
        int lastRow = reservations.size()-1;
        fireTableRowsInserted(lastRow, lastRow);
    }
    
    @Override
    public String getColumnName(int columnIndex){
        Locale locale = Locale.getDefault();
        ResourceBundle rb = ResourceBundle.getBundle("texty", locale);        
        switch(columnIndex){
            case 0: return rb.getString("Rezervace_tab_hlav_id");
            case 1: return rb.getString("Rezervace_tab_hlav_pokoj");
            case 2: return rb.getString("Rezervace_tab_hlav_host_jmeno");
            case 3: return rb.getString("Rezervace_tab_hlav_host_cop");
            case 4: return rb.getString("Rezervace_tab_hlav_pocatek");
            case 5: return rb.getString("Rezervace_tab_hlav_predpokladany_konec");
            case 6: return rb.getString("Rezervace_tab_hlav_skutecny_konec");
            case 7: return rb.getString("Rezervace_tab_hlav_cena");
            case 8: return rb.getString("Rezervace_tab_hlav_sluzby_utrata");
            default: throw new IllegalArgumentException("Invalid column index in table.");
        }
    }
    
    public void removeReservation(int row){
        reservations.remove(row);
        resPrices.remove(row);
        fireTableRowsDeleted(row, row);
    }
    
    public void removeAllReservationsOnlyVisually(){
        int lastRow = (reservations.size()-1 >= 0 ? reservations.size()-1 : 0);
        reservations.clear();
        resPrices.clear();
        fireTableRowsDeleted(0, lastRow);
    }
    
    public void updateReservation(Reservation reservation, int row){
        ReservationManager reservationManager = new ReservationManagerImpl(dataSource, new TimeManagerImpl());
        reservations.set(row, reservation);
        resPrices.set(row, reservationManager.getReservationPrice(reservation));
        fireTableRowsUpdated(row, row);
    }
    
    public void updateReservationRoom(Room room, int row){
        ReservationManager reservationManager = new ReservationManagerImpl(dataSource, new TimeManagerImpl());
        Reservation reservation = reservations.get(row);
        reservation.setRoom(room);
        reservations.set(row, reservation);
        resPrices.set(row, reservationManager.getReservationPrice(reservation));
        fireTableRowsUpdated(row, row);
    }
    
    public void updateReservationGuest(Guest guest, int row){
        Reservation reservation = reservations.get(row);
        reservation.setGuest(guest);
        reservations.set(row, reservation);
        fireTableRowsUpdated(row, row);
    }
    
    public List<Integer> getIndexOf(Guest guest){
         List<Integer> rows = new ArrayList<>();
        for(Reservation reservation : reservations){
            if(reservation.getGuest().equals(guest)){
                rows.add(reservations.indexOf(reservation));
            }
        }
        return rows;
    }
    
    public List<Integer> getIndexOf(Room room){
        List<Integer> rows = new ArrayList<>();
        for(Reservation reservation : reservations){
            if(reservation.getRoom().equals(room)){
                rows.add(reservations.indexOf(reservation));
            }
        }
        return rows;
    }
    
    public int getIndexOf(Reservation reservation){
        return reservations.indexOf(reservation);
    }
    
    private DataSource setUpDataSource(){
        HGEConfig config = new HGEConfig();
        DataSource ds = null;
        try{
            ds = config.dataSource();
            config.setupDataSource(ds);
        }catch(IOException ex){
            logger.log(Level.SEVERE, "Cannot set up datasource in 'ReservationsTableModel' - IOException",ex);
        }catch(SQLException ex){
            logger.log(Level.SEVERE, "Cannot set up datasource in 'ReservationsTableModel' - SQLException",ex);
        }
        
        return ds;
    }
}
