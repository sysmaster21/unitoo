/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.server;

import java.nio.charset.Charset;
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
public class BootFormatter extends BaseFormatter {

    private final String encoding = "UTF-8";
    private final String list = "|";
    private final Character delimiter = '.';
    private final Character group = ' ';
    private final String integer = "###################0";
    private final String decimal = "###############0.00########";
    private final String date = "dd/MM/yyyy";
    private final String time = "HH:mm";
    private final String datetime = "dd/MM/yyyy HH:mm:ss";

    @Override
    protected Charset createCharset() {
        return Charset.forName(encoding);

    }

    @Override
    protected DateFormat createDateFormat() {
        return new SimpleDateFormat(date);
    }

    @Override
    protected DateFormat createTimeFormat() {
        return new SimpleDateFormat(time);
    }

    @Override
    protected DateFormat createDateTimeFormat() {
        return new SimpleDateFormat(datetime);
    }

    @Override
    protected NumberFormat createDecimalFormat() {
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
        symbols.setDecimalSeparator(delimiter);
        symbols.setGroupingSeparator(group);
        return new DecimalFormat(decimal, symbols);
    }

    @Override
    protected NumberFormat createIntegerFormat() {
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
        symbols.setDecimalSeparator(delimiter);
        symbols.setGroupingSeparator(group);
        return new DecimalFormat(integer, symbols);
    }

    @Override
    public String list() {
        return list;
    }

    @Override
    public Setting[] settings() {
        return new Setting[0];
    }

}
