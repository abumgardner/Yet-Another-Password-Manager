/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package yapm;

import com.bumgardner.utils.RecordObject;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 *
 * @author Adam
 */
public class dao {

    private Connection con = null;
    private String m_sLastError = "";
    private boolean m_bHasError = false;

    private static String m_sAddressBookCreate = "create table AddressBook (ident INTEGER PRIMARY KEY AUTOINCREMENT," +
        "Website nvarchar(250), " +
        "Username nvarchar(100), " +
        "Password nvarchar(100), " +
        "Comment nvarchar(250), " +
        "Address nvarchar(400), " +
        "Name nvarchar(50) NOT NULL)";

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
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + sDatabase);
            return connection;
        } catch (java.lang.ClassNotFoundException ce) {
            System.out.println("Class not found!");
            return connection;
        } catch (Exception e) {
            System.out.println("Exception in getConnection: " + e);
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
            stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
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
        String sSql = "Select TBL_NAME FROM sqlite_master WHERE (TBL_NAME = 'AddressBook')";
        RecordObject roRec = getSingleRecord(sSql);
        if(roRec==null || (roRec.getString("TBL_NAME")==null || roRec.getString("TBL_NAME").equals("")))
            return false;
        else
            return true;
    }
}
