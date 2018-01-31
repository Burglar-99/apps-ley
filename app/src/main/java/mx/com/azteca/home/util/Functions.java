package mx.com.azteca.home.util;

import android.util.Log;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Functions {


    public enum Format {
        INTEGER("###,##0"),
        DECIMAL("###,##0.00"),
        DATE("yyyy-MM-dd"),
        DATE_TIME("yyyy-MM-dd HH:mm"),
        DATE_TIME_TEXT("d MMM yyyy"),
        WINDOWS_DATE_TIME("yyyy-MM-dd'T'HH:mm:ss"),
        TIME("HH:mm.ss"),
        TIME_FOLIO("HHmmss");

        public String FORMAT;

        Format(String format) {
            this.FORMAT = format;
        }
    }

    private static Functions FUNCTIONS;

    private DecimalFormat decimalFormat;
    private SimpleDateFormat dateFormat;


    private Functions(Format format) {
        this.decimalFormat = new DecimalFormat("###,##0.00", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
        this.dateFormat = new SimpleDateFormat(format.FORMAT, new Locale("es", "MXN"));
    }

    public static synchronized Functions getInstance(Format format) {
        if (FUNCTIONS == null) {
            FUNCTIONS = new Functions(format);
        }
        else {
            FUNCTIONS.dateFormat.applyPattern(format.FORMAT);
        }
        return FUNCTIONS;
    }

    public String applyTime(long time) {
        return dateFormat.format(time);
    }

    public String applyNumber(long time) {
        return decimalFormat.format(time);
    }

    public String applyNumber(double time) {
        return decimalFormat.format(time);
    }

    public long getTime(String date) {
        long time = 0;
        try {
            Date dateTime = dateFormat.parse(date);
            time = dateTime.getTime();
        }
        catch (ParseException ex) {
            Log.e(getClass().getSimpleName(), ex.toString());
        }
        return time;
    }

}
