/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv168.common;

import cz.muni.fi.pv168.hotelmanager.backend.GuestManager;
import cz.muni.fi.pv168.hotelmanager.backend.RoomManager;
import cz.muni.fi.pv168.hotelmanager.backend.RoomManagerImpl;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSource;

/**
 *
 * @author Tomas Smid
 */
public class HGEConfig {
    
    private static final Logger logger = Logger.getLogger("myLogger");
    
    public void setupDataSource(DataSource ds) throws SQLException{
        DBUtils.tryCreateTables(ds,GuestManager.class.getResourceAsStream("createTables.sql"));
    }
    
    public DataSource dataSource() throws IOException { 
        BasicDataSource bds = new BasicDataSource(); //Apache DBCP connection pooling DataSource
        Properties props = new Properties();
        FileInputStream fis = null;
        
        try{
            fis = new FileInputStream(getClass().getResource("/DBConfig.properties").getPath());
            props.load(fis);
        }catch(IOException ex){
            logger.log(Level.SEVERE, "Cannot open file with DB configuration: {0}", ex.getMessage());
            throw ex;
        }
        
        bds.setDriverClassName(props.getProperty("DB_DRIVER_CLASS"));
        bds.setUrl(props.getProperty("DB_URL"));
        bds.setUsername(props.getProperty("DB_USERNAME"));
        bds.setPassword(props.getProperty("DB_PASSWORD"));
        return bds;
    }
    
    public RoomManager createRoomManager() throws SQLException{
        
    
     BasicDataSource bds = new BasicDataSource();
        bds.setDriverClassName("org.apache.derby.jdbc.ClientDriver");
        bds.setUrl("jdbc:derby://localhost:1527/SSHotelGuestEvidenceDB;create=true");
        bds.setUsername("smesmi");
        bds.setPassword("skoc2po4501");

            DBUtils.tryCreateTables(bds,RoomManager.class.getResourceAsStream("createTables.sql"));

        return new RoomManagerImpl(bds);
    } 
}
