/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv168.hotelmanager.frontend;

import cz.muni.fi.pv168.hotelmanager.backend.Reservation;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Tom
 */
public class ReservationsTableModel extends AbstractTableModel{
    
    private List<Reservation> reservations = new ArrayList<>();

    @Override
    public int getRowCount() {
        return reservations.size();
    }

    @Override
    public int getColumnCount() {
        return 8;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Locale locale = Locale.getDefault();
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, locale);
        NumberFormat numFormat = NumberFormat.getCurrencyInstance(locale);
        Reservation reservation = reservations.get(rowIndex);
        switch(columnIndex){
            case 0: return rowIndex+1;
            case 1: return reservation.getRoom().getNumber();
            case 2: return reservation.getGuest().getName();
            case 3: return reservation.getGuest().getIdCardNum();
            case 4: return dateFormat.format(reservation.getStartTime());
            case 5: return dateFormat.format(reservation.getExpectedEndTime());
            case 6: return (reservation.getRealEndTime() == null ? null : dateFormat.format(reservation.getRealEndTime()));
            case 7: return numFormat.format(reservation.getServicesSpendings());
            default: throw new IllegalArgumentException("Invalid column index in table.");
        }
    }
    
    public void addReservation(Reservation reservation){
        reservations.add(reservation);
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
            case 7: return rb.getString("Rezervace_tab_hlav_sluzby_utrata");
            default: throw new IllegalArgumentException("Invalid column index in table.");
        }
    }
    
    public void removeReservation(int row){
        reservations.remove(row);
        fireTableRowsDeleted(row, row);
    }
    
    public void removeAllReservation0sOnlyVisually(){
        int lastRow = (reservations.size()-1 >= 0 ? reservations.size()-1 : 0);
        reservations.clear();
        fireTableRowsDeleted(0, lastRow);
    }
    
    public void updateReservation(Reservation reservation, int row){
        reservations.set(row, reservation);
        fireTableRowsUpdated(row, row);
    }
}
