/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.types;

import java.util.Calendar;
import org.master.unitoo.core.api.ICodedEnum;

/**
 *
 * @author Andrey
 */
public enum Month implements ICodedEnum<String> {

    January("jan", Calendar.JANUARY),
    February("feb", Calendar.FEBRUARY),
    March("mar", Calendar.MARCH),
    April("apr", Calendar.APRIL),
    May("may", Calendar.MAY),
    June("jun", Calendar.JUNE),
    July("jul", Calendar.JULY),
    August("aug", Calendar.AUGUST),
    September("sep", Calendar.SEPTEMBER),
    October("oct", Calendar.OCTOBER),
    November("nov", Calendar.NOVEMBER),
    December("dec", Calendar.DECEMBER);

    private final String code;
    private final int calendar;

    private Month(String code, int calendar) {
        this.code = code;
        this.calendar = calendar;
    }

    @Override
    public String code() {
        return code;
    }

    public int calendar() {
        return calendar;
    }

    @Override
    public Class<String> type() {
        return String.class;
    }

    @Override
    public boolean is(String code) {
        return code != null && code.equals(this.code);
    }

}
