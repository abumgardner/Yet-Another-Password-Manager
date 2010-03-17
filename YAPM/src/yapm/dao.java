/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package yapm;

import com.bumgardner.utils.AddressRec;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Adam
 */
public class dao {

    private Connection con = null;
    private String m_sLastError = "";
    private boolean m_bHasError = false;

//    private static String m_sAddressBookCreate = "create table AddressBook (" +
//        "Website varchar(250), " +
//        "Username varchar(100), " +
//        "Password varchar(100), " +
//        "Comment varchar(250), " +
//        "Address varchar(400), " +
//        "Name varchar(50) NOT NULL," +
//        "PRIMARY KEY (Name))";

    private static String m_sAddressBookCreate = "create table AddressBook (" +
        "Website nvarchar(250), " +
        "Username nvarchar(100), " +
        "Password nvarchar(100), " +
        "Comment nvarchar(250), " +
        "Address nvarchar(400), " +
        "Name nvarchar(50) NOT NULL PRIMARY KEY)";
    
    public boolean HasError() {
        return m_bHasError;
    }

    public String LastError() {
        return m_sLastError;
    }

    public dao() {
        con = getConnection("addr2.yapm");
        if(!tableExists()) {
            System.out.println("Creating table for new database");
            executeNonQuery(m_sAddressBookCreate);
            if(!tableExists()) {
                m_bHasError = true;
                m_sLastError = "Unable to create table in database!";
            }
        }
    }

    public void destroy() {
        try {
            if(con!=null)
                con.close();
        } catch (Exception e) {}
    }

    private int executeNonQuery(String sSql) {
        //con = getConnection("addr2.yapm");
        Statement stmt = null;
        try {
            stmt = con.createStatement();
            return stmt.executeUpdate(sSql);
        } catch (Exception e) {
            System.out.println("Exception in executeNonQuery(): " + e);
            return -1;
        }
    }

    private Connection getConnection(String sDatabase) {
        Connection connection = null;
        try {
            Class.forName("org.sqlite.JDBC").newInstance();
            connection = DriverManager.getConnection("jdbc:sqlite:" + sDatabase);
            connection.setAutoCommit(true);
            return connection;
        } catch (java.lang.ClassNotFoundException ce) {
            System.out.println("Class not found!");
            return connection;
        } catch (Exception e) {
            System.out.println("Exception in getConnection: " + e);
            e.printStackTrace();
            return connection;
        }
    }

    private AddressRec getSingleRecord(String sSql) {
        Statement stmt = null;
        ResultSet rs = null;
        try {
            //con = getConnection("addr2.yapm");
            stmt = con.createStatement();
            return ResultSetToRecordObj(stmt.executeQuery(sSql));
        } catch (Exception e) {
            System.out.println("Exception in getSingleRecord: " + e);
            return null;
        }
    }

    private ResultSet getManyRecords(String sSql) {
        Statement stmt = null;
        ResultSet rs = null;
        try {
            //con = getConnection("addr2.yapm");
            stmt = con.createStatement();
            rs = stmt.executeQuery(sSql);
            return rs;
        } catch (Exception e) {
            System.out.println("Exception in getManyRecords(): " + e);
            return null;
        }
//        finally {
//            try {
//                if(rs!=null)
//                    rs.close();
//                if(stmt!=null)
//                    stmt.close();
//                if(con!=null)
//                    con.close();
//            } catch (Exception e2) {}
//        }
    }

    private AddressRec ResultSetToRecordObj(ResultSet oResultSet)
    {
        AddressRec Rec = new AddressRec();

        try {
            if (oResultSet.next()) {
                Rec.setAddress(oResultSet.getString("Address"));
                Rec.setComment(oResultSet.getString("Comment"));
                Rec.setName(oResultSet.getString("Name"));
                Rec.setPassword(oResultSet.getString("Password"));
                Rec.setUsername(oResultSet.getString("Username"));
                Rec.setWebsite(oResultSet.getString("Username"));
            }
            else
                Rec = null;
        }
        catch (Exception e) {
            System.out.println("An exception ocurred during result set conversion to a AddressRec. Exception: " + e);
            Rec = null;
        }

        return Rec;
    }

    private ArrayList<AddressRec> ResultSetToArrayList(ResultSet oResultSet)
    {
        ArrayList alRet = new ArrayList();

        try {
            AddressRec oRec = null;

            while (oResultSet.next()) {
                oRec = new AddressRec();

                oRec.setAddress(oResultSet.getString("Address"));
                oRec.setComment(oResultSet.getString("Comment"));
                oRec.setName(oResultSet.getString("Name"));
                oRec.setPassword(oResultSet.getString("Password"));
                oRec.setUsername(oResultSet.getString("Username"));
                oRec.setWebsite(oResultSet.getString("Username"));

                alRet.add(oRec);
            }
        }
        catch (Exception e) {
            System.out.println("An exception occurred during result set conversion to an ArrayList. Exception: " + e);
            alRet = null;
        }

        return alRet;
    }

    private boolean tableExists() {
        try {
            //con = getConnection("addr2.yapm");
            DatabaseMetaData md = con.getMetaData();
            ResultSet rs = md.getTables(null, null, null, new String[] { "TABLE" });
            if(rs.next())
                return true;
            else
                return false;
        } catch (SQLException ex) {
            Logger.getLogger(dao.class.getName()).log(Level.SEVERE, null, ex);
        }
       

        return false;
    }

    public boolean DeleteAddressBookEntry(String sKey) {
        String sSql  = "DELETE FROM ADDRESSBOOK WHERE ident=" + sKey;
        return executeNonQuery(sSql) == 1;
    }

    public ArrayList<AddressRec> GetAddressBookEntries() {
        String sSql  = "SELECT * FROM ADDRESSBOOK";
        return ResultSetToArrayList(getManyRecords(sSql));
    }

    public ArrayList<AddressRec> GetAddressBookEntries(String sName) {
        String sSql = "SELECT * FROM ADDRESSBOOK WHERE Name like '%" + sName + "%'";
        return ResultSetToArrayList(getManyRecords(sSql));
    }

    public AddressRec GetAddressBookEntry(String sName) {
        return getSingleRecord("SELECT * FROM ADDRESSBOOK WHERE Name='" + sName + "'");
    }

    public boolean DoesAddressBookNameExist(String sName) {
        String sSql = "SELECT Name FROM AddressBook WHERE Name='" + sName + "'";
        AddressRec roRec = getSingleRecord(sSql);
        if(roRec==null) 
            return false;
        else
            return true;
    }
    
   public boolean UpdateAddressBookEntry(String sName, String sWebsite, String sUsername, String sPassword, String sComment, String sAddress) {
        String sSql  = "UPDATE AddressBook set Website='" + sWebsite + "', ";
        sSql += "Username='" + sUsername + "', Password='" + sPassword + "', Comment='" + sComment + "', Address='" + sAddress + "' WHERE name='" + sName + "'";
        return executeNonQuery(sSql) == 1;
   }

    public boolean InsertAddressBookEntry(String sName, String sWebsite, String sUsername, String sPassword, String sComment, String sAddress) {
        String sSql = "INSERT INTO AddressBook (Website, Username, Password, Comment, Address, Name) VALUES ";
        sSql += "('" + sWebsite + "', '" + sUsername + "', '" + sPassword + "', '" + sComment + "', '" + sAddress + "', '" + sName + "')";

        return executeNonQuery(sSql) == 1;
    }
}
