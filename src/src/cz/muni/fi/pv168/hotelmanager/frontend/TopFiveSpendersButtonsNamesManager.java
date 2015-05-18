/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv168.hotelmanager.frontend;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 *
 * @author Tomas Smid
 */
public class TopFiveSpendersButtonsNamesManager {
    
    public static String getTitleTopFiveSpendersName(){
        return getResourceBundle().getString("Top5Spenders_tab_nadpis");
    }
    
    private static ResourceBundle getResourceBundle(){
        Locale locale = Locale.getDefault();
        ResourceBundle rb = ResourceBundle.getBundle("texty", locale);
        return rb;
    }
}
