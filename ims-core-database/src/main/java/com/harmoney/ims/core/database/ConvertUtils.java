package com.harmoney.ims.core.database;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


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
            return s_dateFormat.parse(d.substring(0, 10));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
    public static final Calendar parseCalendar(String d)
    {
        if (d == null)
        {
            return null;
        }
        try {
        	GregorianCalendar ret = new GregorianCalendar();
        	Timestamp ts = new Timestamp(s_dateTimeFormat.parse(d).getTime());
        	ret.setTimeInMillis(ts.getTime());
        	return ret;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
    public static final Timestamp parseTimestamp(String d)
    {
        if (d == null)
        {
            return null;
        }
        try {
            return new Timestamp(s_dateTimeFormat.parse(d).getTime());
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
    public static final String printTimestamp(Timestamp c)
    {
        if (c == null)
        {
            return null;
        }
        return s_dateTimeFormat.format(c);
    }
    public static final String printCalendar(Calendar c)
    {
        if (c == null)
        {
            return null;
        }
        return s_dateTimeFormat.format(c.getTime());
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
    public static final LocalDate parseLocalDate(String d)
    {
        if (d == null)
        {
            return null;
        }
        return LocalDate.parse(d);
    }
    public static final LocalDateTime parseLocalDateTime(String d)
    {
        if (d == null)
        {
            return null;
        }
        return LocalDateTime.parse(d);
    }
    public static final OffsetDateTime parseOffsetDateTime(String d)
    {
        if (d == null)
        {
            return null;
        }
        return OffsetDateTime.parse(d);
     }
    public static final String printLocalDate(LocalDate c)
    {
        if (c == null)
        {
            return null;
        }
        return c.toString();
    }
    public static final String printLocalDateTime(LocalDateTime c)
    {
        if (c == null)
        {
            return null;
        }
        return c.toString();
    }
    public static final String printOffsetDateTime(OffsetDateTime c)
    {
        if (c == null)
        {
            return null;
        }
        return c.toString();
    }
    public static final Calendar convertToCalendar(LocalDateTime localDateTime) {
		GregorianCalendar calendar = new GregorianCalendar();
		Timestamp ts = Timestamp.valueOf(localDateTime);
		calendar.setTimeInMillis(ts.getTime());
		return calendar;

    }
    public static final LocalDateTime convertTolocalDateTime(Calendar calendar) {
    	return LocalDateTime.ofInstant(calendar.toInstant(), ZoneId.systemDefault());
    }
}
