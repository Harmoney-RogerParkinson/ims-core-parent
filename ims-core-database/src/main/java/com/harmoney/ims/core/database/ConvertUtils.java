package com.harmoney.ims.core.database;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ConvertUtils
{
  
    private static final SimpleDateFormat s_dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat s_dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    public static final Date parseDate(String d)
    {
        if (d == null)
        {
            return null;
        }
        try {
            return s_dateFormat.parse(d);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
    public static final Long parseTimestamp(String d)
    {
        if (d == null)
        {
            return null;
        }
        try {
            return s_dateFormat.parse(d).getTime();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
    public static final String printDate(Date c)
    {
        if (c == null)
        {
            return null;
        }
        return s_dateFormat.format(c.getTime());
    }
    public static final String printTimestamp(Long c)
    {
        if (c == null)
        {
            return null;
        }
        return s_dateFormat.format(c);
    }
    public static final Date parseDateTime(String d)
    {
        if (d == null)
        {
            return null;
        }
         try {
            return new Date(s_dateTimeFormat.parse(d).getTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
    public static final String printDateTime(Date c)
    {
        if (c == null)
        {
            return null;
        }
        return s_dateTimeFormat.format(c.getTime());
    }
    public static final BigDecimal parseCurrency(String c) {
    	return new BigDecimal(c).setScale(2);
    }
    public static final String printCurrency(BigDecimal c) {
    	return c.setScale(2).toPlainString();
    }

}
