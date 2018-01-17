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
public enum WeekDay implements ICodedEnum<String> {

    Monday("mo", Calendar.MONDAY),
    Tuesday("tu", Calendar.TUESDAY),
    Wednesday("we", Calendar.WEDNESDAY),
    Thursday("th", Calendar.THURSDAY),
    Friday("fr", Calendar.FRIDAY),
    Saturday("sa", Calendar.SATURDAY),
    Sunday("su", Calendar.SUNDAY);

    private final String code;
    private final int calendar;

    private WeekDay(String code, int calendar) {
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

}
