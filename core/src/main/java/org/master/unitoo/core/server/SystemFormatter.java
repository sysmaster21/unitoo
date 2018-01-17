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
import org.master.unitoo.core.api.annotation.Component;
import org.master.unitoo.core.base.BaseFormatter;
import org.master.unitoo.core.api.annotation.Attribute;

/**
 *
 * @author Andrey
 */
@Component("core.format")
public class SystemFormatter extends BaseFormatter {

    @Attribute(name = "encoding", value = "UTF-8")
    public Setting<String> encoding;

    @Attribute(name = "list.delimiter", value = "|")
    public Setting<String> list;

    @Attribute(name = "number.delimiter", value = ".")
    public Setting<Character> delimiter;

    @Attribute(name = "number.group", value = " ")
    public Setting<Character> group;

    @Attribute(name = "number.integer", value = "###################0")
    public Setting<String> integer;

    @Attribute(name = "number.decimal", value = "###############0.00########")
    public Setting<String> decimal;

    @Attribute(name = "date", value = "dd/MM/yyyy")
    public Setting<String> date;

    @Attribute(name = "time", value = "HH:mm")
    public Setting<String> time;

    @Attribute(name = "datetime", value = "dd/MM/yyyy HH:mm:ss")
    public Setting<String> datetime;

    @Override
    protected Charset createCharset() {
        return Charset.forName(encoding.val());

    }

    @Override
    protected DateFormat createDateFormat() {
        return new SimpleDateFormat(date.val());
    }

    @Override
    protected DateFormat createTimeFormat() {
        return new SimpleDateFormat(time.val());
    }

    @Override
    protected DateFormat createDateTimeFormat() {
        return new SimpleDateFormat(datetime.val());
    }

    @Override
    protected NumberFormat createDecimalFormat() {
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
        symbols.setDecimalSeparator(delimiter.val());
        symbols.setGroupingSeparator(group.val());
        return new DecimalFormat(decimal.val(), symbols);
    }

    @Override
    protected NumberFormat createIntegerFormat() {
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
        symbols.setDecimalSeparator(delimiter.val());
        symbols.setGroupingSeparator(group.val());
        return new DecimalFormat(integer.val(), symbols);
    }

    @Override
    public String list() {
        return list.val();
    }

    @Override
    public Setting[] settings() {
        return new Setting[]{
            encoding,
            list,
            delimiter,
            group,
            integer,
            decimal,
            date,
            time,
            datetime
        };
    }

}
