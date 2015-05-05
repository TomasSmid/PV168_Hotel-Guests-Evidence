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
 * @author Tom
 */
public class RoomsButtonsNamesManager {
    
    public static String getRoomsPanelName(){
        return getResourceBundle().getString("Pokoj_menu_panel");
    }
    
    public static String getButtonAddName(){
        return getResourceBundle().getString("Pokoj_menu_tlacitko_pridat");
    }
    
    public static String getButtonDeleteName(){
        return getResourceBundle().getString("Pokoj_menu_tlacitko_smazat");
    }
    
    public static String getButtonEditName(){
        return getResourceBundle().getString("Pokoj_menu_tlacitko_upravit");
    }
    
    public static String getButtonSearchName(){
        return getResourceBundle().getString("Pokoj_menu_tlacitko_hledat");
    }
    
    public static String getButtonAvailabilityName(){
        return getResourceBundle().getString("Pokoj_menu_tlacitko_dostupnost");
    }
    
    public static String getButtonShowAllName(){
        return getResourceBundle().getString("Pokoj_menu_tlacitko_zobrazit_vse");
    }
    
    private static ResourceBundle getResourceBundle(){
        Locale locale = Locale.getDefault();
        ResourceBundle rb = ResourceBundle.getBundle("texty", locale);
        return rb;
    }
}
