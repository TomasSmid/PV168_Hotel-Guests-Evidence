/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv168.hotelmanager.frontend;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Tomas Smid
 */
public class TopFiveSpendersTableModel extends AbstractTableModel{
    
    private List<String[]> topFiveSpenders = new ArrayList<>();
    
    
    @Override
    public int getRowCount() {
        return topFiveSpenders.size();
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        String[] topFivespender = topFiveSpenders.get(rowIndex);
        switch(columnIndex){
            case 0: return topFivespender[0];
            case 1: return topFivespender[1];
            case 2: return topFivespender[2];
            case 3: return topFivespender[3];
            default: throw new IllegalArgumentException("Invalid column index in table.");
        }
    }
    
    public void addTopFiveSpender(String[] topFiveSpender){
        topFiveSpenders.add(topFiveSpender);
        int lastRow = topFiveSpenders.size()-1;
        fireTableRowsInserted(lastRow, lastRow);
    }
    
    @Override
    public String getColumnName(int columnIndex){
        Locale locale = Locale.getDefault();
        ResourceBundle rb = ResourceBundle.getBundle("texty", locale);        
        switch(columnIndex){
            case 0: return rb.getString("Top5Spenders_tab_hlav_cislo_rezervace");
            case 1: return rb.getString("Top5Spenders_tab_hlav_cislo_pokoje");
            case 2: return rb.getString("Top5Spenders_tab_hlav_jmeno_hosta");
            case 3: return rb.getString("Top5Spenders_tab_hlav_utrata_za_sluzby");
            default: throw new IllegalArgumentException("Invalid column index in table.");
        }
    }
    
    public void removeAllTopFiveSpendersOnlyVisually(){
        int lastRow = (topFiveSpenders.size()-1 >= 0 ? topFiveSpenders.size()-1 : 0);
        topFiveSpenders.clear();
        fireTableRowsDeleted(0, lastRow);
    }
}
