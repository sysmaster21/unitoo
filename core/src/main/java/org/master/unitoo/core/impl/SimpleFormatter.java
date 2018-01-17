/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.impl;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import org.master.unitoo.core.base.BaseFormatter;

/**
 *
 * @author Andrey
 */
public abstract class SimpleFormatter extends BaseFormatter {

    @Override
    protected DateFormat createDateFormat() {
        return new SimpleDateFormat(dateFormat());
    }

    @Override
    protected DateFormat createTimeFormat() {
        return new SimpleDateFormat(timeFormat());
    }

    @Override
    protected DateFormat createDateTimeFormat() {
        return new SimpleDateFormat(dateTimeFormat());
    }

    @Override
    protected NumberFormat createDecimalFormat() {
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
        symbols.setDecimalSeparator(delimiter());
        symbols.setGroupingSeparator(group());
        return new DecimalFormat(decFormat(), symbols);
    }

    @Override
    protected NumberFormat createIntegerFormat() {
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
        symbols.setDecimalSeparator(delimiter());
        symbols.setGroupingSeparator(group());
        return new DecimalFormat(intFormat(), symbols);
    }

    public abstract char group();

    public abstract char delimiter();

    public abstract String intFormat();

    public abstract String decFormat();

    public abstract String dateFormat();

    public abstract String timeFormat();

    public abstract String dateTimeFormat();

}
