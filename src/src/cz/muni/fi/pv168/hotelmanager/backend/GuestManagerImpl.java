/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.muni.fi.pv168.hotelmanager.backend;

import cz.muni.fi.pv168.common.DBUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.sql.DataSource;

/**
 * This class implements GuestManager.
 * 
 * @author Tomas Smid
 */
public class GuestManagerImpl implements GuestManager{

    private static final Logger logger = Logger.getLogger(GuestManagerImpl.class.getName());
    
    private final DataSource dataSource;
    
    public GuestManagerImpl(DataSource dataSource){
        this.dataSource = dataSource;
    }
    
    @Override
    public void createGuest(Guest guest) {
        if(guest == null){
            throw new IllegalArgumentException("Storing guest into DB failure: "
                    + "There was made an attempt to insert null guest into DB.");
        }
        
        String pem = "Storing guest into";
        checkGuestsIdIsNull(guest, pem);
        checkGuestsName(guest, pem);
        checkGuestsPhone(guest, pem);
        checkGuestsIdCardNum(guest, pem);
        checkGuestsDateOfBirth(guest, pem);
        checkGuestDuplicity(guest);
        
        checkDataSource();
        
        try(Connection connection = dataSource.getConnection()){
            
            connection.setAutoCommit(false);                    
            try(PreparedStatement ps = connection.prepareStatement(
                            "INSERT INTO Guest (name,phone,id_card_num,born) VALUES (?,?,?,?)",
                            Statement.RETURN_GENERATED_KEYS)){
                ps.setString(1, guest.getName());
                ps.setString(2, guest.getPhone());
                ps.setString(3, guest.getIdCardNum());
                ps.setTimestamp(4, dateToTimestamp(guest.getBorn()));

                int count = ps.executeUpdate();
                if(count != 1){
                    throw new ServiceFailureException("Internal error: "
                            + "More rows inserted when trying to insert guest " + guest);
                }

                ResultSet keyRS = ps.getGeneratedKeys();
                Long id = getIdFromKey(keyRS, guest);
                guest.setId(id);

                connection.commit();
            
            }catch(SQLException ex){
                String errMsg = "Error occured when inserting guest " + guest + " into DB.";
                logger.log(Level.SEVERE, errMsg, ex);
                throw new ServiceFailureException(errMsg, ex);
            }finally{
                DBUtils.doRollbackQuietly(connection);
                DBUtils.switchAutocommitBackToTrue(connection);
            }
            
        } catch (SQLException ex) {
            String errMsg = "Connection error occured when inserting guest " + guest + " into DB.";
            logger.log(Level.SEVERE, errMsg, ex);
            throw new ServiceFailureException(errMsg, ex);
        }
    }
    

    @Override
    public void updateGuest(Guest guest) {
        if(guest == null){
            throw new IllegalArgumentException("Updating guest in DB failure: "
                    + "There was made an attempt to update a null guest in DB.");
        }
        
        String pem = "Updating guest in";
        checkGuestsIdIsNotNull(guest, pem);
        checkGuestsName(guest, pem);
        checkGuestsPhone(guest, pem);
        checkGuestsIdCardNum(guest, pem);
        checkGuestsDateOfBirth(guest, pem);
        
        checkDataSource();
        
        try(Connection connection = dataSource.getConnection()){
            
            connection.setAutoCommit(false);
            try(PreparedStatement ps = connection.prepareStatement(
                            "UPDATE Guest SET name = ?, phone = ?, id_card_num = ?,"
                                + "born = ? WHERE id = ?")){
                ps.setString(1, guest.getName());
                ps.setString(2, guest.getPhone());
                ps.setString(3, guest.getIdCardNum());
                ps.setTimestamp(4, dateToTimestamp(guest.getBorn()));
                ps.setLong(5, guest.getId());

                int count = ps.executeUpdate();
                if(count != 1){
                    throw new ServiceFailureException("Internal error: "
                            + "More rows updated when trying to update guest " + guest.toString());
                }

                connection.commit();

            }catch(SQLException ex){
                String errMsg = "Error occured when updating guest " + guest.toString() + " in DB.";
                logger.log(Level.SEVERE, errMsg, ex);
                throw new ServiceFailureException(errMsg, ex);
            }finally{
                DBUtils.doRollbackQuietly(connection);
                DBUtils.switchAutocommitBackToTrue(connection);
            }
            
        } catch (SQLException ex) {
            String errMsg = "Connection error occured when updating guest " + guest.toString() + " in DB.";
            logger.log(Level.SEVERE, errMsg, ex);
            throw new ServiceFailureException(errMsg, ex);
        }
    }

    @Override
    public void deleteGuest(Guest guest) {
        if(guest == null){
            throw new IllegalArgumentException("Deleting guest from DB failure: "
                    + "There was made an attempt to delete a null guest in DB.");
        }
        
        checkGuestsIdIsNotNull(guest, "Deleting guest from");
        
        try(Connection connection = dataSource.getConnection()){
            
            connection.setAutoCommit(false);
            try(PreparedStatement ps = connection.prepareStatement("DELETE FROM Guest WHERE id = ?")){
                ps.setLong(1, guest.getId());

                int count = ps.executeUpdate();
                if(count != 1){
                    throw new ServiceFailureException("Internal error: "
                            + "More rows deleted when trying to delete guest " + guest.toString());
                }

                connection.commit();
            
            }catch(SQLException ex){
                String errMsg = "Error occured when deleting guest " + guest.toString() + " from DB -> " + ex.getMessage();
                logger.log(Level.SEVERE, errMsg, ex);
                throw new ServiceFailureException(errMsg, ex);
            }finally{
                DBUtils.doRollbackQuietly(connection);
                DBUtils.switchAutocommitBackToTrue(connection);
            }
            
        }catch(SQLException ex){
                String errMsg = "Connection error occured when deleting guest " + guest.toString() + " from DB.";
                logger.log(Level.SEVERE, errMsg, ex);
                throw new ServiceFailureException(errMsg, ex);
            }
    }

    @Override
    public Guest getGuestById(Long id) {
        if(id == null){
            throw new IllegalArgumentException("Retrieving guest from DB failure: "
                    + "There was made an attempt to retrieve a guest by a null id.");
        }
        
        checkDataSource();
        
        try(Connection connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT id,name,phone,id_card_num,born FROM Guest WHERE id = ?")){
            ps.setLong(1, id);            
            ResultSet rs = ps.executeQuery();
            Guest guest = null;
            if(rs.next()){
                guest = resultSetToGuest(rs);
                if(rs.next()){
                    throw new ServiceFailureException("Internal error: More entities with the same id found "
                            + "(source id: " + id + ", found " + guest + " and " + resultSetToGuest(rs));
                }                
            }
            
            return guest;
            
        }catch(SQLException ex){
            String errMsg = "Error occured when retrieving guest with id " + id + " from DB.";
            logger.log(Level.SEVERE, errMsg, ex);
            throw new ServiceFailureException(errMsg, ex);
        }
    }

    @Override
    public List<Guest> findAllGuests() {
        checkDataSource();
        
        try(Connection connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT id,name,phone,id_card_num,born FROM Guest")){
            
            ResultSet rs = ps.executeQuery();
            
            List<Guest> retGuests = new ArrayList<>();
            while(rs.next()){                
                retGuests.add(resultSetToGuest(rs));
            }
            
            return retGuests;
            
        }catch(SQLException ex){
            String errMsg = "Error occured when retrieving all guests from DB.";
            logger.log(Level.SEVERE, errMsg, ex);
            throw new ServiceFailureException(errMsg, ex);
        }
    }

    @Override
    public List<Guest> findGuestByName(String name) {
        if(name == null || name.equals("")){
            throw new IllegalArgumentException("Retrieving guest from DB failure: "
                    + "There was made an attempt to retrieve a guest by a null or an empty name.");
        }
        
        checkDataSource();
        
        try(Connection connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT id,name,phone,id_card_num,born FROM Guest WHERE name = ?")){
            ps.setString(1, name);            
            ResultSet rs = ps.executeQuery();
            
            List<Guest> retGuests = new ArrayList<>();
            while(rs.next()){                
                retGuests.add(resultSetToGuest(rs));
            }
            
            return retGuests;
            
        }catch(SQLException ex){
            String errMsg = "Error occured when retrieving guest with name " + name + " from DB.";
            logger.log(Level.SEVERE, errMsg, ex);
            throw new ServiceFailureException(errMsg, ex);
        }
    }
    
    
    
    private void checkDataSource() {
        if (dataSource == null) {
            throw new IllegalStateException("DataSource is not set");
        }
    }
    
    private void checkGuestsIdIsNull(Guest guest, String partOfErrMsg){
        if(guest.getId() != null){
            throw new IllegalArgumentException(partOfErrMsg + " DB failure: "
                    + "Guest has a preset id.");
        }
    }
    
    private void checkGuestsIdIsNotNull(Guest guest, String partOfErrMsg){
        if(guest.getId() == null){
            throw new IllegalArgumentException(partOfErrMsg + " DB failure: "
                    + "Guest has a null id.");
        }
    }
    
    private void checkGuestsName(Guest guest, String partOfErrMsg){
        String name = guest.getName();
        
        if(name == null){
            throw new IllegalArgumentException(partOfErrMsg + " DB failure: "
                    + "Name of guest " + guest.toString() + " is null.");
        }
        
        if (!Pattern.matches("[A-ZČĎŇŘŠŤŽĚÁÉÍÓÚÝ][a-zA-ZčČďĎňŇřŘšŠťŤžŽěĚáÁéÉíÍóÓúÚůýÝ]*([ |\\-][A-ZČĎŇŘŠŤŽĚÁÉÍÓÚÝ][a-zA-ZčČďĎňŇřŘšŠťŤžŽěĚáÁéÉíÍóÓúÚůýÝ]*)*", name)){
            throw new IllegalArgumentException(partOfErrMsg + " DB failure: "
                    + "Name of guest " + guest.toString() + " has a wrong form.");
        }
    }
    
    private void checkGuestsPhone(Guest guest, String partOfErrMsg){
        String phone = guest.getPhone();
        
        if(phone != null){
            if(phone.isEmpty()){
                throw new IllegalArgumentException(partOfErrMsg + " DB failure: "
                        + "Phone number of guest " + guest.toString() + " is empty.");
            }        
        
            if(!((Pattern.matches("([\\(][\\+][0-9]{3}[\\)][ ])?[0-9]{3}[ ][0-9]{3}[ ][0-9]{3}", phone))
                    || (Pattern.matches("([\\(][\\+][0-9]{3}[\\)][\\-])?[0-9]{3}[\\-][0-9]{3}[\\-][0-9]{3}", phone)))){
                throw new IllegalArgumentException(partOfErrMsg + " DB failure: "
                        + "Phone number of guest " + guest.toString() + " has a wrong form.");
            }
        }
    }
    
    private void checkGuestsIdCardNum(Guest guest, String partOfErrMsg){
        String icn = guest.getIdCardNum();
        
        if(icn == null || icn.equals("")){
            throw new IllegalArgumentException(partOfErrMsg + " DB failure: "
                    + "Identification card number of guest " + guest.toString() + " is null or empty.");
        }
        
        if(!Pattern.matches("[0-9]{9}", icn)){
            throw new IllegalArgumentException(partOfErrMsg + " DB failure: "
                    + " Identification card number of guest " + guest.toString() + " has a wrong form.");
        }
    }
    
    private void checkGuestsDateOfBirth(Guest guest, String partOfErrMsg){
        if(guest.getBorn() == null){
            throw new IllegalArgumentException(partOfErrMsg + " DB failure: "
                    + "Date of birth of guest " + guest.toString() + " is null.");
        }
    }
    
    private Timestamp dateToTimestamp(Date date){
        if (date == null) {
            return null;
        }
        
        return (date instanceof Timestamp ? (Timestamp) date : new Timestamp(date.getTime()));
    }
    
    private Long getIdFromKey(ResultSet keyRS, Guest guest) throws SQLException{
        if(keyRS.next()){
            if(keyRS.getMetaData().getColumnCount() != 1){
                throw new ServiceFailureException("Internal error: "
                        + "Retrieval of generated key failed when trying to insert "
                        + "guest " + guest.toString() + " - wrong key fields count: "
                        + keyRS.getMetaData().getColumnCount());
            }
            Long result = keyRS.getLong(1);
            if(keyRS.next()){
                throw new ServiceFailureException("Internal error: "
                        + "Retrieval of generated key failed when trying to insert "
                        + "guest " + guest.toString() + " - more keys found.");
            }
            return result;
        }else{
            throw new ServiceFailureException("Internal error: "
                    + "Retrieval of generated key failed when trying to insert "
                    + "guest " + guest.toString() + " - no key found.");
        }
    }
    
    private Guest resultSetToGuest(ResultSet rs) throws SQLException{
        Guest guest = new Guest();
        guest.setId(rs.getLong("id"));
        guest.setName(rs.getString("name"));
        guest.setPhone(rs.getString("phone"));
        guest.setIdCardNum(rs.getString("id_card_num"));
        guest.setBorn(rs.getTimestamp("born"));
        
        return guest;
    }
    
    private void checkGuestDuplicity(Guest guest){
        List<Guest> matchedGuests = findGuestByName(guest.getName());
        
        if(!matchedGuests.isEmpty()){
            for(Guest g : matchedGuests){
                if(g.getIdCardNum().equals(guest.getIdCardNum()) && g.getBorn().equals(guest.getBorn())){
                    throw new DuplicateGuestException("Guest " + guest + " is already stored in DB.");
                }
            }
        }
    }
    
}
