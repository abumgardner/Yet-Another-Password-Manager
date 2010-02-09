/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package yapm;

import com.bumgardner.utils.RecordObject;
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

    private static String m_sAddressBookCreate = "create table AddressBook (" +
        "Website varchar(250), " +
        "Username varchar(100), " +
        "Password varchar(100), " +
        "Comment varchar(250), " +
        "Address varchar(400), " +
        "Name varchar(50) NOT NULL," +
        "PRIMARY KEY (Name))";

    public boolean HasError() {
        return m_bHasError;
    }

    public String LastError() {
        return m_sLastError;
    }

    public dao() {
        con = getConnection("addr.yapm");
        if(!tableExists()) {
            System.out.println("Creating table for new database");
            executeNonQuery(m_sAddressBookCreate);
            if(!tableExists()) {
                m_bHasError = true;
                m_sLastError = "Unable to create table in database!";
            }
        }
    }

    private int executeNonQuery(String sSql) {
        try {
            Statement stmt = con.createStatement();
            return stmt.executeUpdate(sSql);
        } catch (Exception e) {
            System.out.println("Exception in executeNonQuery(): " + e);
            return -1;
        }
    }

    private Connection getConnection(String sDatabase) {
        Connection connection = null;
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
            connection = DriverManager.getConnection("jdbc:derby:" + sDatabase + ";create=true");
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

    private RecordObject getSingleRecord(String sSql) {
        Statement stmt = null;
        ResultSet rs = null;
        try {
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
            stmt = con.createStatement();
            rs = stmt.executeQuery(sSql);
            return rs;
        } catch (Exception e) {
            System.out.println("Exception in getManyRecords(): " + e);
            return null;
        }
    }

    private RecordObject ResultSetToRecordObj(ResultSet oResultSet)
    {
        RecordObject Rec = new RecordObject();

        try {
            if (oResultSet.next()) {
                ResultSetMetaData rsmd = oResultSet.getMetaData();
                int iNumColumns = rsmd.getColumnCount();

                for (int i = 0; i < iNumColumns; i++)
                    Rec.setFieldValue(rsmd.getColumnName(i + 1), oResultSet.getObject(i + 1));
            }
            else
                Rec = null;
        }
        catch (Exception e) {
            System.out.println("An exception ocurred during result set conversion to a RecordObject. Exception: " + e);
            Rec = null;
        }

        return Rec;
    }

    private ArrayList<RecordObject> ResultSetToArrayList(ResultSet oResultSet)
    {
        ArrayList alRet = new ArrayList();

        try {
            RecordObject oRec = null;
            ResultSetMetaData rsmd = oResultSet.getMetaData();
            int iIdx = 0, iNumColumns = rsmd.getColumnCount();

            while (oResultSet.next()) {
                oRec = new RecordObject();

                for (iIdx = 0; iIdx < iNumColumns; iIdx++)
                    oRec.setFieldValue(rsmd.getColumnName(iIdx + 1), oResultSet.getObject(iIdx + 1));

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
//        String sSql = "Select TBL_NAME FROM sqlite_master WHERE (TBL_NAME = 'AddressBook')";
//        RecordObject roRec = getSingleRecord(sSql);
//        if(roRec==null || (roRec.getString("TBL_NAME")==null || roRec.getString("TBL_NAME").equals("")))
//            return false;
//        else
//            return true;
    }

    public boolean DeleteAddressBookEntry(String sKey) {
        String sSql  = "DELETE FROM ADDRESSBOOK WHERE ident=" + sKey;
        return executeNonQuery(sSql) == 1;
    }

    public ArrayList<RecordObject> GetAddressBookEntries() {
        String sSql  = "SELECT * FROM ADDRESSBOOK";
        return ResultSetToArrayList(getManyRecords(sSql));
    }

    public ArrayList<RecordObject> GetAddressBookEntries(String sName) {
        String sSql = "SELECT * FROM ADDRESSBOOK WHERE Name like '%" + sName + "%'";
        return ResultSetToArrayList(getManyRecords(sSql));
    }

    public RecordObject GetAddressBookEntry(String sName) {
        return getSingleRecord("SELECT * FROM ADDRESSBOOK WHERE Name='" + sName + "'");
    }

    public boolean DoesAddressBookNameExist(String sName) {
        String sSql = "SELECT Name FROM AddressBook WHERE Name='" + sName + "'";
        RecordObject roRec = getSingleRecord(sSql);
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
