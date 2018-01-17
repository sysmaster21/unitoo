/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.server;

import java.util.ArrayList;
import org.master.unitoo.core.api.IFlushable;
import org.master.unitoo.core.api.annotation.Component;
import org.master.unitoo.core.base.BaseBackgroundTask;
import org.master.unitoo.core.api.annotation.Attribute;
import org.master.unitoo.core.api.annotation.Logger;

/**
 *
 * @author Andrey
 */
@Component(value = "task-file-check")
@Logger(MainLog.class)
public class FlushCheckTask extends BaseBackgroundTask {

    private final ArrayList<IFlushable> items = new ArrayList<>();

    @Attribute(name = "delay", value = "1000")
    public Setting<Long> delay;

    @Attribute(name = "immediately", value = "true")
    public Setting<Boolean> startImmediately;

    @Override
    protected boolean startImmediately() {
        return startImmediately.val() == null ? true : startImmediately.val();
    }

    @Override
    protected long calcNext() {
        return delay.val() == null ? 1000 : delay.val();
    }

    @Override
    protected void execute() throws Throwable {
        log().debug("Start scan for changes...");
        for (IFlushable item : items) {
            log().debug("Checking for changes: " + item.getClass().getName());
            if (item.isChanged()) {
                log().debug("Item '" + item.getClass().getName() + "' changed, flushing...");
                item.flush();
                log().info("Item '" + item.getClass().getName() + "' stored");
            }
        }
    }

    public void startCheck(IFlushable item) {
        items.add(item);
    }

}
