package com.acme.edu;

import java.util.Objects;
import static com.acme.edu.Logger.Classes.*;
import static java.lang.Math.abs;

public class Logger {
    public static final String PRIMITIVE_PREFIX = "primitive: ";
    public static final String CHAR_PREFIX = "char: ";
    public static final String STRING_PREFIX = "string: ";
    public static final String OBJECT_PREFIX = "reference: ";

    enum Classes{UNDEFINED,
        BOOLEAN, CHAR, BYTE, INTEGER,
        STRING, OBJECT
    }

    private static Classes lastLogClass = UNDEFINED;

    private static int intBuffer;
    private static long intOverflowCounter;

    private static String strBuffer;
    private static int strCounter;

    private static byte byteBuffer;
    private static long byteOverflowCounter;

    public static void log(int message) {
        flushOtherClass(INTEGER);
        processIntOverflow(message);
        setLastLogClass(INTEGER);
    }

    private static  void processIntOverflow(int message) {
        if (message > 0) {
            processMaxIntOverflow(message);
        } else {
            processMinIntOverflow(message);
        }
    }

    private static void processMaxIntOverflow(int message /*>=0*/) {
        long diff = Integer.MAX_VALUE-(long)intBuffer;
        if ( diff > message) {
            intBuffer += message;
        } else { // only if intBuffer > 0
            intBuffer = (int)(message-diff);
            if (intOverflowCounter < 0) {
                --intBuffer;
            }
            ++intOverflowCounter;
        }
    }

    private static void processMinIntOverflow(int message) {
        long diff = (long)intBuffer-Integer.MIN_VALUE;
        if ( diff > abs(message)) {
            intBuffer += message;
        } else { // only if intBuffer < 0
            intBuffer = (int)(message+diff);
            if (intOverflowCounter > 0) {
                --intBuffer;
            }
            --intOverflowCounter;
        }
    }

    private static void flushIntBuffer() {
            writeInteger();
            cleanIntBuffer();
    }

    private  static void writeInteger() {
        writeIntOverflowValue();
        writeIntResidual();
    }

    private static void writeIntOverflowValue() {
        int overflowValue = intOverflowCounter > 0 ? Integer.MAX_VALUE : Integer.MIN_VALUE;
        for (int i = 0; i < abs(intOverflowCounter); i++) {
            write(formatMessage(PRIMITIVE_PREFIX, overflowValue));
        }
    }

    private static void writeIntResidual() {
        // to avoid printing "MAX_VALUE\n0" or "MIN_VALUE\n0"
        // but allow "0" (when "MAX_VALUE" or "MIN_VALUE" is not needed)
        if (intBuffer != 0 || intOverflowCounter == 0) {
            write(formatMessage(PRIMITIVE_PREFIX, intBuffer));
        }
    }

    private static void cleanIntBuffer() {
        intBuffer = 0;
        intOverflowCounter = 0;
    }

    //--------------------------------------------
    public static void log(String message) {
        flushOtherClass(STRING);
        processString(message);
        setLastLogClass(STRING);
    }

    private static void processString(String message) {
        if (lastLogClass != STRING) {
            initStrBuffer(message);
        } else {
            processStrBuffer(message);
        }
    }

    private static void initStrBuffer(String message) {
        strBuffer = message;
        ++strCounter;
    }

    private static void processStrBuffer(String message) {
        if (Objects.equals(strBuffer, message)) {
            ++strCounter;
        } else {
            flushStrBuffer();
            initStrBuffer(message);
        }
    }
    private static void flushStrBuffer() {
            write(formatMessage(STRING_PREFIX, formatString(strBuffer, strCounter)));
            cleanStrBuffer();
    }

    private static String formatString(String strBuffer, int strCounter) {
        if (strCounter == 1) return strBuffer;
        return strBuffer + " (x" + strCounter + ")";
    }

    private static void cleanStrBuffer() {
        strBuffer = null;
        strCounter = 0;
    }

    //--------------------------------------------
    public static void log(byte message) {
        flushOtherClass(BYTE);
        processByteOverflow(message);
        setLastLogClass(BYTE);
    }

    private static  void processByteOverflow(byte message) {
        if (message > 0) {
            processMaxByteOverflow(message);
        } else {
            processMinByteOverflow(message);
        }
    }

    private static void processMaxByteOverflow(Byte message /*>=0*/) {
        int diff = Byte.MAX_VALUE-byteBuffer;
        if ( diff > message) {
            byteBuffer += message;
        } else { // only if byteBuffer > 0
            byteBuffer = (byte)(message-diff);
            if (byteOverflowCounter < 0) {
                --byteBuffer;
            }
            ++byteOverflowCounter;
        }
    }

    private static void processMinByteOverflow(byte message) {
        int diff = byteBuffer-Byte.MIN_VALUE;
        if ( diff > abs(message)) {
            byteBuffer += message;
        } else { // only if byteBuffer < 0
            byteBuffer = (byte)(message+diff);
            if (byteOverflowCounter > 0) {
                --byteBuffer;
            }
            --byteOverflowCounter;
        }
    }

    private static void flushByteBuffer() {
        writeByte();
        cleanByteBuffer();
    }

    private  static void writeByte() {
        writeByteOverflowValue();
        writeByteResidual();
    }

    private static void writeByteOverflowValue() {
        byte overflowValue = byteOverflowCounter > 0 ? Byte.MAX_VALUE : Byte.MIN_VALUE;
        for (int i = 0; i < abs(byteOverflowCounter); i++) {
            write(formatMessage(PRIMITIVE_PREFIX, overflowValue));
        }
    }

    private static void writeByteResidual() {
        // to avoid printing "MAX_VALUE\n0" or "MIN_VALUE\n0"
        // but allow "0" (when "MAX_VALUE" or "MIN_VALUE" is not needed)
        if (byteBuffer != 0 || byteOverflowCounter == 0) {
            write(formatMessage(PRIMITIVE_PREFIX, byteBuffer));
        }
    }

    private static void cleanByteBuffer() {
        byteBuffer = 0;
        byteOverflowCounter = 0;
    }

    //--------------------------------------------
    public static void log(Object message) {
        flushOtherClass(OBJECT);
        write(formatMessage(OBJECT_PREFIX, message));
        setLastLogClass(OBJECT);
    }

    public static void log(char message) {
        flushOtherClass(CHAR);
        write(formatMessage(CHAR_PREFIX, message));
        setLastLogClass(CHAR);
    }

    public static void log(boolean message) {
        flushOtherClass(BOOLEAN);
        write(formatMessage(PRIMITIVE_PREFIX, message));
        setLastLogClass(BOOLEAN);
    }

    //--------------------------------------------
    private static String formatMessage(String prefix, Object message) {
        return prefix + message;
    }

    private static void write(String message) {
        System.out.println(message);
    }

    public static void flush() {
        switch (lastLogClass){
            case BYTE:
                flushByteBuffer();
            case INTEGER:
                flushIntBuffer();
                break;
            case STRING:
                flushStrBuffer();
                break;
            default:
        }
    }

    private static void flushOtherClass(Classes currClass) {
        if (lastLogClass != currClass) {
            flush();
        }
    }

    private static void setLastLogClass(Classes currClass){
        lastLogClass = currClass;
    }

}

