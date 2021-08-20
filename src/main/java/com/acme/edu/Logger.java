package com.acme.edu;

import java.util.Objects;

import static java.lang.Math.abs;

public class Logger {

    public static final String PRIMITIVE_PREFIX = "primitive: ";
    public static final String CHAR_PREFIX = "char: ";
    public static final String STRING_PREFIX = "string: ";
    public static final String OBJECT_PREFIX = "reference: ";

    private static int intBuffer;
    private static int intMaxMinValueCounter;
    private static boolean lastLogIntegerFlag;

    private static String strBuffer;
    private static int strCounter;
    private static boolean lastLogStringFlag;

    public static void log(int message) {
        processStrBuffer();
        checkIntOverFlow(message);
        lastLogIntegerFlag = true;
    }

    private static  void checkIntOverFlow(int message) {
        if (message > 0) {
            checkMaxIntOverFlow(message);
        } else {
            checkMinIntOverFlow(message);
        }
    }

    private static void checkMaxIntOverFlow(int message /*>=0*/) {
        long diff = Integer.MAX_VALUE-(long)intBuffer;
        if ( diff > message) {
            intBuffer += message;
        } else { // only if intBuffer > 0
            intBuffer = (int)(message-diff);
            if (intMaxMinValueCounter < 0) {
                --intBuffer;
            }
            ++intMaxMinValueCounter;
        }
    }

    private static void checkMinIntOverFlow(int message) {
        long diff = (long)intBuffer-Integer.MIN_VALUE;
        if ( diff > abs(message)) {
            intBuffer += message;
        } else { // only if intBuffer < 0
            intBuffer = (int)(message+diff);
            if (intMaxMinValueCounter > 0) {
                --intBuffer;
            }
            --intMaxMinValueCounter;
        }
    }
    private static void processIntBuffer() {
        if (lastLogIntegerFlag) {
            int overflowValue = intMaxMinValueCounter > 0 ? Integer.MAX_VALUE : Integer.MIN_VALUE;
            for (int i = 0; i < abs(intMaxMinValueCounter); i++) {
                write(formatMessage(PRIMITIVE_PREFIX, overflowValue));
            }
            // to avoid printing "MAX_VALUE\n0" or "MIN_VALUE\n0"
            // but allow "0" (when "MAX_VALUE" or "MIN_VALUE" is not needed)
            if (intBuffer != 0 || intMaxMinValueCounter == 0) {
                write(formatMessage(PRIMITIVE_PREFIX, intBuffer));
            }
            cleanIntBuffer();
        }
    }
    private static void cleanIntBuffer() {
        intBuffer = 0;
        intMaxMinValueCounter = 0;
        lastLogIntegerFlag = false;
    }

    //--------------------------------------------
    public static void log(String message) {
        processIntBuffer();

        if (!lastLogStringFlag) {
            initiateStrBuffer(message);
        } else {
            checkRepeatedString(message);
        }
    }

    private static void initiateStrBuffer(String message) {
        strBuffer = message;
        ++strCounter;
        lastLogStringFlag = true;
    }

    private static void checkRepeatedString(String message) {
        if (Objects.equals(strBuffer, message)) {
            ++strCounter;
        } else {
            processStrBuffer();
            initiateStrBuffer(message);
        }
    }
    private static void processStrBuffer() {
        if (lastLogStringFlag) {
            write(formatMessage(STRING_PREFIX, formatString(strBuffer, strCounter)));
            cleanStrBuffer();
        }
    }

    private static String formatString(String strBuffer, int strCounter) {
        if (strCounter == 1) return strBuffer;
        return strBuffer + " (x" + strCounter + ")";
    }

    private static void cleanStrBuffer() {
        strBuffer = null;
        strCounter = 0;
        lastLogStringFlag = false;
    }

    //--------------------------------------------
    public static void log(Object message) {
        flush();
        write(formatMessage(OBJECT_PREFIX, message));
    }

    private static String formatMessage(String prefix, Object message) {
        return prefix + message;
    }

    public static void log(byte message) {
        flush();
        write(formatMessage(PRIMITIVE_PREFIX, message));
    }

    public static void log(char message) {
        flush();
        write(formatMessage(CHAR_PREFIX, message));
    }
    public static void log(boolean message) {
        flush();
        write(formatMessage(PRIMITIVE_PREFIX, message));
    }

//    private static void save(String message) {
//        print(message);
//    }
    private static void write(String message) {
        System.out.println(message);
    }


    public static void flush() {
            processIntBuffer();
            processStrBuffer();
    }
}

