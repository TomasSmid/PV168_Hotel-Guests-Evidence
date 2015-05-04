/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv168.common;

import cz.muni.fi.pv168.hotelmanager.backend.GuestManager;
import cz.muni.fi.pv168.hotelmanager.backend.RoomManager;
import cz.muni.fi.pv168.hotelmanager.backend.RoomManagerImpl;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSource;

/**
 *
 * @author Tomas Smid
 */
public class HGEConfig {
    
    public void setupDataSource(DataSource ds) throws SQLException{
        DBUtils.tryCreateTables(ds,GuestManager.class.getResourceAsStream("createTables.sql"));
    }
    
    public DataSource dataSource() throws IOException { 
        BasicDataSource bds = new BasicDataSource(); //Apache DBCP connection pooling DataSource
        bds.setDriverClassName("org.apache.derby.jdbc.ClientDriver");
        bds.setUrl("jdbc:derby://localhost:1527/SSHotelGuestEvidenceDB");
        bds.setUsername("smesmi");
        bds.setPassword("skoc2po4501");
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
