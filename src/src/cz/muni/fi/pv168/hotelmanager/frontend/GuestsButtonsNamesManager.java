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
public class GuestsButtonsNamesManager {
    
    public static String getGuestsPanelName(){
        return getResourceBundle().getString("Host_menu_panel");
    }
    
    public static String getButtonAddName(){
        return getResourceBundle().getString("Host_menu_tlacitko_pridat");
    }
    
    public static String getButtonDeleteName(){
        return getResourceBundle().getString("Host_menu_tlacitko_smazat");
    }
    
    public static String getButtonEditName(){
        return getResourceBundle().getString("Host_menu_tlacitko_upravit");
    }
    
    public static String getButtonSearchName(){
        return getResourceBundle().getString("Host_menu_tlacitko_hledat");
    }
    
    public static String getButtonShowAllName(){
        return getResourceBundle().getString("Host_menu_tlacitko_zobrazit_vse");
    }
    
    public static String getNewAndEditNameName(){
        return getResourceBundle().getString("Host_pridat_upravit_popis_jmeno");
    }
    
    public static String getNewAndEditIDCardName(){
        return getResourceBundle().getString("Host_pridat_upravit_popis_cop");
    }
    
    public static String getNewAndEditBornName(){
        return getResourceBundle().getString("Host_pridat_upravit_popis_datum_nar");
    }
    
    public static String getNewAndEditPhoneName(){
        return getResourceBundle().getString("Host_pridat_upravit_popis_tel");
    }
    
    public static String getSearchConditionName(){
        return getResourceBundle().getString("Host_hledat_popis_podle");
    }
    
    private static ResourceBundle getResourceBundle(){
        Locale locale = Locale.getDefault();
        ResourceBundle rb = ResourceBundle.getBundle("texty", locale);
        return rb;
    }
}
