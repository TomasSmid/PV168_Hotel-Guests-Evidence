/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv168.hotelmanager.frontend;

import cz.muni.fi.pv168.hotelmanager.backend.Guest;
import cz.muni.fi.pv168.hotelmanager.backend.Room;
import java.math.RoundingMode;
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
public class RoomsTableModel extends AbstractTableModel{
    
    private List<Room> rooms = new ArrayList<>();

    @Override
    public int getRowCount() {
        return rooms.size();
    }

    @Override
    public int getColumnCount() {
        return 6;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Locale locale = Locale.getDefault();
        NumberFormat numFormat = NumberFormat.getCurrencyInstance(locale);
        Room room = rooms.get(rowIndex);
        switch(columnIndex){
            case 0: return rowIndex+1;
            case 1: return room.getNumber();
            case 2: return room.getCapacity();
            case 3: return room.getFloor();
            case 4: return room.getType();
            case 5: return numFormat.format(room.getPrice());
            default: throw new IllegalArgumentException("Invalid column index in table.");
        }
    }
    
    public void addRoom(Room room){
        rooms.add(room);
        int lastRow = rooms.size()-1;
        fireTableRowsInserted(lastRow, lastRow);
    }
    
    @Override
    public String getColumnName(int columnIndex){
        Locale locale = Locale.getDefault();
        ResourceBundle rb = ResourceBundle.getBundle("texty", locale);        
        switch(columnIndex){
            case 0: return rb.getString("Pokoj_tab_hlav_id");
            case 1: return rb.getString("Pokoj_tab_hlav_cislo");
            case 2: return rb.getString("Pokoj_tab_hlav_kapacita");
            case 3: return rb.getString("Pokoj_tab_hlav_podlazi");
            case 4: return rb.getString("Pokoj_tab_hlav_typ");
            case 5: return rb.getString("Pokoj_tab_hlav_cena");
            default: throw new IllegalArgumentException("Invalid column index in table.");
        }
    }
    
    public void removeRoom(int row){
        rooms.remove(row);
        fireTableRowsDeleted(row, row);
    }
    
    public void removeAllRoomsOnlyVisually(){
        int lastRow = (rooms.size()-1 >= 0 ? rooms.size()-1 : 0);
        rooms.clear();
        fireTableRowsDeleted(0, lastRow);
    }
    
    public void updateRoom(Room room, int row){
        rooms.set(row, room);
        fireTableRowsUpdated(row, row);
    }
}
