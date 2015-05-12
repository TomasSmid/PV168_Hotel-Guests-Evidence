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
import javax.swing.DefaultComboBoxModel;

/**
 *
 * @author Tom
 */
public class ComboBoxManager {
    
    public static DefaultComboBoxModel setYearComboBox(int from, int to){
        int number = from;
        List<String> stringNumbers = new ArrayList<>();
        
        while(number <= to){
            stringNumbers.add(String.valueOf(number));
            number++;
        }
        
        String[] strNumsArray = stringNumbers.toArray(new String[stringNumbers.size()]);
        return new DefaultComboBoxModel(strNumsArray);
    }
    
    public static DefaultComboBoxModel setRoomTypeComboBox(){
        Locale locale = Locale.getDefault();
        ResourceBundle rb = ResourceBundle.getBundle("texty", locale);
        String[] types = {rb.getString("Pokoj_pridat_upravit_popis_combo_apartman"),
                          rb.getString("Pokoj_pridat_upravit_popis_combo_rodinny"),
                          rb.getString("Pokoj_pridat_upravit_popis_combo_standard"),
                          rb.getString("Pokoj_pridat_upravit_popis_combo_suite")};
        
        return new DefaultComboBoxModel(types);
    }
    
    public static DefaultComboBoxModel setRoomSearchConditionComboBox(){
        Locale locale = Locale.getDefault();
        ResourceBundle rb = ResourceBundle.getBundle("texty", locale);
        String[] conds = {rb.getString("Pokoj_hledat_popis_combo_podle_kapacita"),
                          rb.getString("Pokoj_hledat_popis_combo_podle_typ"),
                          rb.getString("Pokoj_hledat_popis_combo_podle_oboji")};
        
        return new DefaultComboBoxModel(conds);
    }
}
