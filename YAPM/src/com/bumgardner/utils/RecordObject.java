/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bumgardner.utils;

import java.math.BigDecimal;
import java.util.*;

/**
 *
 * @author adam
 */
public class RecordObject implements java.io.Serializable
{

    private HashMap m_hmFields = new HashMap();
    private String m_sError = "";

    public RecordObject() { }

    /** Error Handling */
    protected void error(String sErr)
    {
        m_sError = sErr;
    }

    public String clearErrors()
    {
        String sErrRet = m_sError;
        m_sError = "";
        return sErrRet;
    }

    public String getLastError()
    {
        return m_sError;
    }

    public boolean hasErrors()
    {
        return !m_sError.equals("");
    }

    /** Set Methods */
    public Object setFieldValue(String sFieldName, Object oFieldValue)
    {
        return m_hmFields.put(sFieldName.toUpperCase(), oFieldValue);
    }

    public String setFieldValue(String sFieldName, String sFieldValue)
    {
        return (String)m_hmFields.put(sFieldName.toUpperCase(), sFieldValue);
    }

    /** Get Methods */
    public Object getObject(String sFieldName)
    {
        Object oRet = null;

        if (m_hmFields.containsKey(sFieldName.toUpperCase()))
            oRet = m_hmFields.get(sFieldName.toUpperCase());
        else
            error("Field '" + sFieldName + "' not found.");

        return oRet;
    }

    public byte[] getBytes(String sFieldName)
    {
        byte[] baRet = null;
        Object o = getObject(sFieldName);

        if (o != null) {
            if (byte[].class.equals(o.getClass()))
                baRet = (byte[])o;
            else
                error("Field '" + sFieldName + "' cannot be converted to a byte array.");
        }

        return baRet;
    }

    public String getString(String sFieldName)
    {
        Object o = getObject(sFieldName);
        String sRet = "";

        if (o != null)
            sRet = o.toString().trim();

        return sRet;
    }

    public boolean getBoolean(String sFieldName)
    {
        boolean bRet = false;
        Object o = getObject(sFieldName);

        if (o != null) {
            if (Integer.class.equals(o.getClass()))
                bRet = (((Integer)o).intValue() == 1);
            else if (String.class.equals(o.getClass())) {
                String s = o.toString().toUpperCase();

                if (s.length() > 0) {
                    switch (s.charAt(0)) {
                        case 'Y':
                        case 'T':
                        case '1':
                            bRet = true;
                            break;
                        case 'O':
                            bRet = s.equals("ON");
                            break;
                        default:
                            bRet = false;
                            break;
                    }
                }
            }
            else if (Long.class.equals(o.getClass()))
                bRet = (((Long)o).longValue() == 1);
            else if (Double.class.equals(o.getClass()))
                bRet = (((Double)o).doubleValue() == 1.0f);
            else if (Byte.class.equals(o.getClass()))
                bRet = (((Byte)o).intValue() == 1);
            else if (Boolean.class.equals(o.getClass()))
                bRet = ((Boolean)o);
            else
                error("Field '" + sFieldName + "' cannot be converted to a boolean.");
        }

        return bRet;
    }

    public int getInt(String sFieldName)
    {
        int iRet = -1;
        Object o = getObject(sFieldName);

        if (o != null) {
            if (Integer.class.equals(o.getClass()))
                iRet = ((Integer)o).intValue();
            else if (String.class.equals(o.getClass())) {
                try {
                    iRet = Integer.parseInt(o.toString());
                }
                catch (Exception e) {
                    error("An exception occurred attempting to convert the string value of field '" + sFieldName + "' to an int.");
                    iRet = -1;
                }
            }
            else if (Long.class.equals(o.getClass()))
                iRet = ((Long)o).intValue();
            else if (Double.class.equals(o.getClass()))
                iRet = ((Double)o).intValue();
            else if (BigDecimal.class.equals(o.getClass()))
                iRet = ((BigDecimal)o).intValue();
            else
                error("Field '" + sFieldName + "' cannot be converted to an int.");
        }

        return iRet;
    }

    public long getLong(String sFieldName)
    {
        long lRet = -1;
        Object o = getObject(sFieldName);

        if (o != null) {
            if (Integer.class.equals(o.getClass()))
                lRet = ((Integer)o).longValue();
            else if (String.class.equals(o.getClass())) {
                try {
                    lRet = Long.parseLong(o.toString());
                }
                catch (Exception e) {
                    error("An exception occurred attempting to convert the string value of field '" + sFieldName + "' to a long.");
                    lRet = -1;
                }
            }
            else if (Long.class.equals(o.getClass()))
                lRet = ((Long)o).longValue();
            else if (Double.class.equals(o.getClass()))
                lRet = ((Double)o).longValue();
            else if (BigDecimal.class.equals(o.getClass()))
                lRet = ((BigDecimal)o).longValue();
            else
                error("Field '" + sFieldName + "' cannot be converted to a long.");
        }

        return lRet;
    }

    public double getDouble(String sFieldName)
    {
        double dRet = -1;
        Object o = getObject(sFieldName);

        if (o != null) {
            if (Integer.class.equals(o.getClass()))
                dRet = ((Integer)o).doubleValue();
            else if (String.class.equals(o.getClass())) {
                try {
                    dRet = Double.parseDouble(o.toString());
                }
                catch (Exception e) {
                    error("An exception occurred attempting to convert the string value of field '" + sFieldName + "' to a double.");
                    dRet = -1;
                }
            }
            else if (Long.class.equals(o.getClass()))
                dRet = ((Long)o).doubleValue();
            else if (Double.class.equals(o.getClass()))
                dRet = ((Double)o).doubleValue();
            else if (BigDecimal.class.equals(o.getClass()))
                dRet = ((BigDecimal)o).doubleValue();
            else
                error("Field '" + sFieldName + "' cannot be converted to a double.");
        }

        return dRet;
    }

    public BigDecimal getDecimal(String sFieldName)
    {
        BigDecimal DRet = BigDecimal.valueOf(-1);
        Object o = getObject(sFieldName);

        if (o != null) {
            if (BigDecimal.class.equals(o.getClass()))
                DRet = (BigDecimal)o;
            else if (Integer.class.equals(o.getClass()))
                DRet = BigDecimal.valueOf(((Integer)o).longValue());
            else if (String.class.equals(o.getClass())) {
                try {
                    DRet = BigDecimal.valueOf(Double.parseDouble(o.toString()));
                }
                catch (Exception e) {
                    error("An exception occurred attempting to convert the string value of field '" + sFieldName + "' to a double.");
                    DRet = BigDecimal.valueOf(-1);
                }
            }
            else if (Long.class.equals(o.getClass()))
                DRet = BigDecimal.valueOf(((Long)o).longValue());
            else if (Double.class.equals(o.getClass()))
                DRet = BigDecimal.valueOf(((Double)o).doubleValue());
            else
                error("Field '" + sFieldName + "' cannot be converted to a double.");
        }

        return DRet;
    }

    public Set<String> GetFieldNames()
    {
        return (Set<String>)m_hmFields.keySet();
    }

}
