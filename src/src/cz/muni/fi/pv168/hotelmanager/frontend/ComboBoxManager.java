/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv168.hotelmanager.frontend;

import java.util.ArrayList;
import java.util.List;
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
}
