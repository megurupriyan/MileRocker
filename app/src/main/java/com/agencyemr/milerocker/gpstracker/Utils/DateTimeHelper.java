package com.agencyemr.milerocker.gpstracker.Utils;

import android.os.Environment;
import android.util.Log;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;


public class DateTimeHelper {

    public final static long ONE_SECOND = 1000;
    public final static long SECONDS = 60;

    public final static long ONE_MINUTE = ONE_SECOND * 60;
    public final static long MINUTES = 60;

    public final static long ONE_HOUR = ONE_MINUTE * 60;
    public final static long HOURS = 24;

    public final static long ONE_DAY = ONE_HOUR * 24;

    public static String getDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        // System.out.println(dateFormat.format(date));
        return dateFormat.format(date);
    }

    /*public static String convertLongDateToShortDate(String longDate) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        // System.out.println(dateFormat.format(date));
        try {
            return dateFormat.parse(longDate).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }*/
    public static String convertLongDateToShortDate(String longDate) {

        String[] strings = longDate.split(",");
        //	Log.i("String1", strings[0]+"");
        Log.i("String2", strings[1] + "");

        DateFormat inputDF = new SimpleDateFormat("dd-MM-yyyy");
        DateFormat outputDF = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return outputDF.format(inputDF.parse(strings[1]));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;

    }

    public static String formatTime(String startTime) {

        //startTime = "2013-02-27 21:06:30";
        StringTokenizer tk = new StringTokenizer(startTime);
        String date = tk.nextToken();
        String time = tk.nextToken();

        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
        SimpleDateFormat sdfs = new SimpleDateFormat("hh:mm a");
        Date dt;
        try {
            dt = sdf.parse(time);
            String formattedTime = sdfs.format(dt);
            System.out.println("Time Display: " + formattedTime); // <-- I got result here
            return formattedTime;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getFormattedShortTime(String time) {

        //String time = "22:35";

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
            Date dateObj = sdf.parse(time);
            System.out.println(dateObj);
            System.out.println(new SimpleDateFormat("KK:mm aa").format(dateObj));
            return new SimpleDateFormat("KK:mm aa").format(dateObj);
        } catch (final ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String getTime() {
        DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss");
        Date date = new Date();
        // System.out.println(dateFormat.format(date));
        return dateFormat.format(date);
    }

    public static Locale getLocale(String currencyName) {
        Locale locale = null;
        if (currencyName.toLowerCase(Locale.US).contains("rufia")
                || currencyName.toLowerCase(Locale.US).contains("rufiya")
                || currencyName.toLowerCase(Locale.US).contains("ruff")) {
            locale = new Locale("en", "MDV");
        } else if (currencyName.toLowerCase(Locale.US).contains("rupee")) {
            locale = new Locale("en", "IN");

        } else if (currencyName.toLowerCase(Locale.US).contains("dollar")) {
            locale = new Locale("en", "US");
        }
        /*
         * else { return Locale.getDefault(); }
		 */
        return locale;
    }

    /*public static Bitmap compressImage(Bitmap image) {
        if (image != null) {
            Bitmap bitmap = decodeSampledBitmapFromFile(
                    image.getAbsolutePath(), 1000, 700);

            FileOutputStream out;
            try {
                out = new FileOutputStream(image);
                bitmap.compress(CompressFormat.JPEG, 95, out);
                out.flush();
                out.close();
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }*/
    public static String formatJsonDate(String jsonDate) {
        if (jsonDate != null) {
            // String jsonDate="/Date(1400309880000+0530)/";
            //yyMMddHHmmssz
            DateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy", new Locale("en-IN"));

            Timestamp timestamp = new Timestamp(Long.parseLong(jsonDate.replaceAll(".*?(\\d+).*",
                    "$1")));

            Date date = new Date(timestamp.getTime());
            Log.d("Parsed Date", date.toString());
            return dateFormat.format(date);
        }
        return "";
    }

    public static String formatJsonTime(String jsonDate) {
        if (jsonDate != null) {
            // String jsonDate="/Date(1400309880000+0530)/";
            DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss");
            Date date = new Date(Long.parseLong(jsonDate.replaceAll(".*?(\\d+).*",
                    "$1")));
            // Log.d("Parsed Date", date.toString());
            return dateFormat.format(date);
        }
        return "";
    }

    public static String formatLongDate(String jsonDate) {
        // String jsonDate="/Date(1400309880000+0530)/";
        //
        if (jsonDate != null) {
            DateFormat dateFormat = new SimpleDateFormat("EEEE,dd-MM-yyyy");
            Date date = new Date(Long.parseLong(jsonDate.replaceAll(".*?(\\d+).*",
                    "$1")));
            // Log.d("Parsed Date", date.toString());
            return convertLongDateToShortDate(dateFormat.format(date));
        }
        return "";
    }

    public static String formatDuration(String jsonDuration) {
        // String dur=DateUtils.formatElapsedTime(12*60000);
        // 2640 Hrs : 00 Mts
		/*long hours = 0;
		long minutes = 0;
		long time = 0;
		long[] mins = new long[10];
		int i = 0;
		String[] temp = jsonDuration.split(" ");
		for (String string : temp) {

			Log.d("StringSplit", string);
			if (isValidNumber(Long.class, string)) {
				Log.d("StringSplit", "Number =" + string);
				mins[i] = Long.parseLong(string);
				i++;
			}
		}

		if (mins.length > 0) {
			hours = mins[0];
			minutes = mins[1];
			
			
			
			time = ((hours * 60) + minutes) * 60000;
			Log.d("StringSplit", "TimeInMilliSec =" + time);
			
			
			DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss",Locale.getDefault());
			Date date = new Date(time);
		
			Log.d("StringSplit", "Duration =" + dateFormat.format(date));
			//return dateFormat.format(date);
			
			DateTime start=new DateTime(TimeUnit.HOURS.toMillis(hours+minutes));
			DateTime end=DateTime.now();
			Interval interval = new Interval(start, end);
			
			return interval.toPeriod().toString();
		}
		*/
		
		
		/*
		 * Pattern pattern = Pattern.compile("([0-9]*) Hrs : ([0-9]*) Mts");
		 * Matcher matcher = pattern.matcher(jsonDuration); int hours =
		 * Integer.parseInt(matcher.group(1)); int minutes =
		 * Integer.parseInt(matcher.group(2)); long time = (hours * 60) +
		 * minutes; // In minutes time *= 60000; // In milliseconds //return
		 * dateFormat.format(new Date(time));
		 * 
		 * DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss"); Date date =
		 * new Date(time);
		 */
        return jsonDuration;
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public static String formatCurrency(Locale locale, double price) {
        NumberFormat currencyFormatter = NumberFormat
                .getCurrencyInstance(locale);
        return currencyFormatter.format(price);
    }

    public static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    public static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    /**
     * Does a try catch (Exception) on parsing the given string to the given
     * type
     *
     * @param c         the number type. Valid types are (wrappers included): double,
     *                  int, float, long.
     * @param numString number to check
     * @return false if there is an exception, true otherwise
     */
    public static boolean isValidNumber(Class<?> c, String numString) {
        try {
            if (c == double.class || c == Double.class) {
                Double.parseDouble(numString);
            } else if (c == int.class || c == Integer.class) {
                Integer.parseInt(numString);
            } else if (c == float.class || c == Float.class) {
                Float.parseFloat(numString);
            } else if (c == long.class || c == Long.class) {
                Long.parseLong(numString);
            }
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    // Time helpers

    /**
     * converts time (in milliseconds) to human-readable format
     * "<w> days, <x> hours, <y> minutes and (z) seconds"
     */
    public static String millisToLongDHMS(long duration) {
        StringBuffer res = new StringBuffer();
        long temp = 0;
        if (duration >= ONE_SECOND) {
            temp = duration / ONE_DAY;
            if (temp > 0) {
                duration -= temp * ONE_DAY;
                res.append(temp).append(" day").append(temp > 1 ? "s" : "")
                        .append(duration >= ONE_MINUTE ? ", " : "");
            }

            temp = duration / ONE_HOUR;
            if (temp > 0) {
                duration -= temp * ONE_HOUR;
                res.append(temp).append(" hour").append(temp > 1 ? "s" : "")
                        .append(duration >= ONE_MINUTE ? ", " : "");
            }

            temp = duration / ONE_MINUTE;
            if (temp > 0) {
                duration -= temp * ONE_MINUTE;
                res.append(temp).append(" minute").append(temp > 1 ? "s" : "");
            }

            if (!res.toString().equals("") && duration >= ONE_SECOND) {
                res.append(" and ");
            }

            temp = duration / ONE_SECOND;
            if (temp > 0) {
                res.append(temp).append(" second").append(temp > 1 ? "s" : "");
            }
            return res.toString();
        } else {
            return "0 second";
        }
    }

    /**
     * converts time (in milliseconds) to human-readable format "<dd:>hh:mm:ss"
     */
    public static String millisToShortDHMS(long duration) {
        String res = "";
        duration /= ONE_SECOND;
        int seconds = (int) (duration % SECONDS);
        duration /= SECONDS;
        int minutes = (int) (duration % MINUTES);
        duration /= MINUTES;
        int hours = (int) (duration % HOURS);
        int days = (int) (duration / HOURS);
        if (days == 0) {
            res = String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            res = String.format(Locale.US, "%dd%02d:%02d:%02d", days, hours, minutes,
                    seconds);
        }
        return res;
    }

    /**
     * converts time (in milliseconds) to human-readable format "<dd:>hh:mm:ss"
     */
    public static String millisToShortDHMSTU(long duration) {
        String res = "";
        long days = TimeUnit.MILLISECONDS.toDays(duration);
        long hours = TimeUnit.MILLISECONDS.toHours(duration)
                - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(duration));
        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration)
                - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
                .toHours(duration));
        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration)
                - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
                .toMinutes(duration));
        if (days == 0) {
            res = String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            res = String.format(Locale.US, "%dd%02d:%02d:%02d", days, hours, minutes,
                    seconds);
        }
        return res;
    }

    public static final String[] COUNTRIES = new String[]{"Afghanistan",
            "Albania", "Algeria", "American Samoa", "Andorra", "Angola",
            "Anguilla", "Antarctica", "Antigua and Barbuda", "Argentina",
            "Armenia", "Aruba", "Australia", "Austria", "Azerbaijan",
            "Bahrain", "Bangladesh", "Barbados", "Belarus", "Belgium",
            "Belize", "Benin", "Bermuda", "Bhutan", "Bolivia",
            "Bosnia and Herzegovina", "Botswana", "Bouvet Island", "Brazil",
            "British Indian Ocean Territory", "British Virgin Islands",
            "Brunei", "Bulgaria", "Burkina Faso", "Burundi", "Cote d'Ivoire",
            "Cambodia", "Cameroon", "Canada", "Cape Verde", "Cayman Islands",
            "Central African Republic", "Chad", "Chile", "China",
            "Christmas Island", "Cocos (Keeling) Islands", "Colombia",
            "Comoros", "Congo", "Cook Islands", "Costa Rica", "Croatia",
            "Cuba", "Cyprus", "Czech Republic",
            "Democratic Republic of the Congo", "Denmark", "Djibouti",
            "Dominica", "Dominican Republic", "East Timor", "Ecuador", "Egypt",
            "El Salvador", "Equatorial Guinea", "Eritrea", "Estonia",
            "Ethiopia", "Faeroe Islands", "Falkland Islands", "Fiji",
            "Finland", "Former Yugoslav Republic of Macedonia", "France",
            "French Guiana", "French Polynesia", "French Southern Territories",
            "Gabon", "Georgia", "Germany", "Ghana", "Gibraltar", "Greece",
            "Greenland", "Grenada", "Guadeloupe", "Guam", "Guatemala",
            "Guinea", "Guinea-Bissau", "Guyana", "Haiti",
            "Heard Island and McDonald Islands", "Honduras", "Hong Kong",
            "Hungary", "Iceland", "India", "Indonesia", "Iran", "Iraq",
            "Ireland", "Israel", "Italy", "Jamaica", "Japan", "Jordan",
            "Kazakhstan", "Kenya", "Kiribati", "Kuwait", "Kyrgyzstan", "Laos",
            "Latvia", "Lebanon", "Lesotho", "Liberia", "Libya",
            "Liechtenstein", "Lithuania", "Luxembourg", "Macau", "Madagascar",
            "Malawi", "Malaysia", "Maldives", "Mali", "Malta",
            "Marshall Islands", "Martinique", "Mauritania", "Mauritius",
            "Mayotte", "Mexico", "Micronesia", "Moldova", "Monaco", "Mongolia",
            "Montserrat", "Morocco", "Mozambique", "Myanmar", "Namibia",
            "Nauru", "Nepal", "Netherlands", "Netherlands Antilles",
            "New Caledonia", "New Zealand", "Nicaragua", "Niger", "Nigeria",
            "Niue", "Norfolk Island", "North Korea", "Northern Marianas",
            "Norway", "Oman", "Pakistan", "Palau", "Panama",
            "Papua New Guinea", "Paraguay", "Peru", "Philippines",
            "Pitcairn Islands", "Poland", "Portugal", "Puerto Rico", "Qatar",
            "Reunion", "Romania", "Russia", "Rwanda", "Sqo Tome and Principe",
            "Saint Helena", "Saint Kitts and Nevis", "Saint Lucia",
            "Saint Pierre and Miquelon", "Saint Vincent and the Grenadines",
            "Samoa", "San Marino", "Saudi Arabia", "Senegal", "Seychelles",
            "Sierra Leone", "Singapore", "Slovakia", "Slovenia",
            "Solomon Islands", "Somalia", "South Africa",
            "South Georgia and the South Sandwich Islands", "South Korea",
            "Spain", "Sri Lanka", "Sudan", "Suriname",
            "Svalbard and Jan Mayen", "Swaziland", "Sweden", "Switzerland",
            "Syria", "Taiwan", "Tajikistan", "Tanzania", "Thailand",
            "The Bahamas", "The Gambia", "Togo", "Tokelau", "Tonga",
            "Trinidad and Tobago", "Tunisia", "Turkey", "Turkmenistan",
            "Turks and Caicos Islands", "Tuvalu", "Virgin Islands", "Uganda",
            "Ukraine", "United Arab Emirates", "United Kingdom",
            "United States", "United States Minor Outlying Islands", "Uruguay",
            "Uzbekistan", "Vanuatu", "Vatican City", "Venezuela", "Vietnam",
            "Wallis and Futuna", "Western Sahara", "Yemen", "Yugoslavia",
            "Zambia", "Zimbabwe"};

}
