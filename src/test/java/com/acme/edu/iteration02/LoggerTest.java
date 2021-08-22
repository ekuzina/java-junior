package com.acme.edu.iteration02;

import com.acme.edu.Logger;
import com.acme.edu.SysoutCaptureAndAssertionAbility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static com.acme.edu.Logger.*;

public class LoggerTest implements SysoutCaptureAndAssertionAbility {
    //region given
    @BeforeEach
    public void setUpSystemOut() throws IOException {
        resetOut();
        captureSysout();
    }

    @AfterEach
    public void tearDown() {
        resetOut();
    }
    //endregion



//    TODO: implement Logger solution to match specification as tests

    @Test
    public void shouldLogSequentIntegersAsSum() throws IOException {
        //region when
        Logger.log("str 1");
        Logger.log(1);
        Logger.log(2);
        Logger.log("str 2");
        Logger.log(0);
        Logger.flush();
        //endregion

        //region then
        assertSysoutContains("str 1");
        assertSysoutContains("3");
        assertSysoutContains("str 2");
        assertSysoutContains("0");
        //endregion
    }

    @Test
    public void shouldLogCorrectlyIntegerOverflowWhenSequentIntegers() {
        //region when
        Logger.log("str 1");

        Logger.log(0);
        Logger.log(Integer.MIN_VALUE);
        Logger.log(Integer.MAX_VALUE);

        Logger.log("str 2");

        Logger.log(Integer.MIN_VALUE);
        Logger.log(Integer.MAX_VALUE-1);
        Logger.log(Integer.MAX_VALUE);
        Logger.log(5);
        Logger.log(Integer.MAX_VALUE);

        Logger.log((byte)5);
        Logger.log('l');
        Logger.log(6);
        Logger.log('y');

        Logger.log(Integer.MIN_VALUE+8);
        Logger.log(-8);

        Logger.log('z');
        Logger.flush();
        //endregion

        //region then
        assertSysoutContains("str 1");

        assertSysoutContains("-1");

        assertSysoutContains("str 2");

        assertSysoutContains(Integer.toString(Integer.MAX_VALUE));
        assertSysoutContains("3");

        assertSysoutContains("5");
        assertSysoutContains("l");
        assertSysoutContains("6");
        assertSysoutContains("y");
        assertSysoutContains(Integer.toString(Integer.MIN_VALUE));
        assertSysoutContains("z");
        //endregion
    }

    @Test
    public void shouldLogCorrectlyByteOverflowWhenSequentBytes() {
        //region when
        Logger.log("str 1");
        Logger.log((byte)10);
        Logger.log((byte)Byte.MAX_VALUE);
        Logger.log("str 2");
        Logger.log(0);
        Logger.flush();
        //endregion

        //region then
        assertSysoutContains("str 1");
        assertSysoutContains(Byte.toString(Byte.MAX_VALUE));
        assertSysoutContains("10");
        assertSysoutContains("str 2");
        assertSysoutContains("0");
        //endregion
    }

    @Test
    public void shouldLogSameSubsequentStringsWithoutRepeat() throws IOException {
        //region when
        Logger.log("str 1");
        Logger.log("str 2");
        Logger.log("str 2");
        Logger.log(0);
        Logger.log("str 2");
        Logger.log("str 3");
        Logger.log("str 3");
        Logger.log("str 3");
        Logger.flush();
        //endregion

        //region then
        assertSysoutContains("str 1");
        assertSysoutContains("str 2 (x2)");
        assertSysoutContains("0");
        assertSysoutContains("str 2");
        assertSysoutContains("str 3 (x3)");
        //endregion
    }

}