/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv168.hotelmanager.frontend;

import cz.muni.fi.pv168.hotelmanager.backend.Guest;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Tomas Smid
 */
public class GuestsTableModel extends AbstractTableModel {
    
    private List<Guest> guests = new ArrayList<>();

    @Override
    public int getRowCount() {
        return guests.size();
    }

    @Override
    public int getColumnCount() {
        return 5;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Locale locale = Locale.getDefault();
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, locale);
        Guest guest = guests.get(rowIndex);
        switch(columnIndex){
            case 0: return rowIndex+1;
            case 1: return guest.getName();
            case 2: return guest.getIdCardNum();
            case 3: return dateFormat.format(guest.getBorn());
            case 4: return guest.getPhone();
            default: throw new IllegalArgumentException("Invalid column index in table.");
        }
    }
    
    public void addGuest(Guest guest){
        guests.add(guest);
        int lastRow = guests.size()-1;
        fireTableRowsInserted(lastRow, lastRow);
    }
    
    @Override
    public String getColumnName(int columnIndex){
        Locale locale = Locale.getDefault();
        ResourceBundle rb = ResourceBundle.getBundle("texty", locale);        
        switch(columnIndex){
            case 0: return rb.getString("Host_tab_hlav_id");
            case 1: return rb.getString("Host_tab_hlav_jmeno");
            case 2: return rb.getString("Host_tab_hlav_COP");
            case 3: return rb.getString("Host_tab_hlav_narozen");
            case 4: return rb.getString("Host_tab_hlav_tel");
            default: throw new IllegalArgumentException("Invalid column index in table.");
        }
    }
    
    public void removeGuest(int row){
        guests.remove(row);
        fireTableRowsDeleted(row, row);
    }
    
    public void removeAllGuestsOnlyVisually(){
        int lastRow = (guests.size()-1 >= 0 ? guests.size()-1 : 0);
        guests.clear();
        fireTableRowsDeleted(0, lastRow);
    }
    
    public void updateGuest(Guest guest, int row){
        guests.set(row, guest);
        fireTableRowsUpdated(row, row);
    }
    
    public int getIndexOf(Guest guest){
        return guests.indexOf(guest);
    }
}
