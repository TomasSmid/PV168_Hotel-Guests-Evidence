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
public class GeneralButtonsAndTitlesNamesManager {
    
    public static String getAppTitleName(){
        return getResourceBundle().getString("Nazev_aplikace");
    }
    
    public static String getMenuBarMenuName(){
        return getResourceBundle().getString("Horni_lista_nabidka");
    }
    
    public static String getMenuBarMenuExitName(){
        return getResourceBundle().getString("Horni_lista_nabidka_ukoncit");
    }
    
    public static String getMenuBarHelpName(){
        return getResourceBundle().getString("Horni_lista_napoveda");
    }
    
    private static ResourceBundle getResourceBundle(){
        Locale locale = Locale.getDefault();
        ResourceBundle rb = ResourceBundle.getBundle("texty", locale);
        return rb;
    }
}
