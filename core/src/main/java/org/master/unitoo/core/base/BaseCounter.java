/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.base;

import java.util.Calendar;
import java.util.concurrent.atomic.AtomicLong;
import org.master.unitoo.core.api.IApplication;
import org.master.unitoo.core.api.IBootInfo;
import org.master.unitoo.core.api.ICodedEnum;
import org.master.unitoo.core.api.components.ICounter;
import org.master.unitoo.core.errors.ComponentNotActive;
import org.master.unitoo.core.types.ComponentContext;
import org.master.unitoo.core.types.ComponentType;
import org.master.unitoo.core.types.CounterStrategy;
import org.master.unitoo.core.types.RunnableState;

/**
 *
 * @author Andrey
 */
public abstract class BaseCounter implements ICounter {

    private ComponentContext context;
    private final CounterStrategy strategy;
    private volatile boolean avail = false;
    private final AtomicLong value = new AtomicLong(0);
    private final Range range;

    public BaseCounter(CounterStrategy strategy) {
        this.strategy = strategy;
        this.range = new Range(strategy);
    }

    @Override
    public RunnableState state() {
        return avail ? RunnableState.Running : RunnableState.Stopped;
    }

    @Override
    public void reject() {
    }

    protected void updateRange(Range range) throws Exception {
        if (range.expired()) {
            range.from(0);
            range.to(cache());
        } else {
            range.from(range.to());
            range.to(range.from() + cache());
        }
        range.updateExpire();
    }

    protected long nextValue(Range range) throws Exception {
        synchronized (this) {
            long v = range.expired() ? 0 : range.from() + 1;
            _flush();
            range.from(v);
            range.to(v);
            range.updateExpire();
            return v;
        }
    }

    @Override
    @SuppressWarnings({"TooBroadCatch", "UseSpecificCatch"})
    public long next() throws ComponentNotActive {
        if (!avail) {
            throw new ComponentNotActive(name());
        }

        try {
            if (cache() < 2) {
                return nextValue(range);
            } else {
                long v = value.incrementAndGet();
                if (!range.check(v)) {
                    v = update();
                }
                return v;
            }
        } catch (Throwable t) {
            throw new ComponentNotActive(name(), t);
        }
    }

    @Override
    public CounterStrategy strategy() {
        return strategy;
    }

    @Override
    public long current() {
        return value.get();
    }

    protected abstract CounterData load() throws Exception;

    @SuppressWarnings({"TooBroadCatch", "UseSpecificCatch"})
    private void _load() {
        try {
            CounterData cd = load();
            range.deactivate();
            if (cd != null) {
                long v = cd.lastValue();
                long limit = cd.cacheSize();
                long reset = cd.resetTime();

                range.from(v);
                range.to(limit);
                range.expire(reset == 0 ? Long.MAX_VALUE : reset);

                if (cd.state() == CounterState.Stored) {
                    context.application().log().info("\t\tloaded: start=" + v);
                    value.set(v);
                } else {
                    context.application().log().info("\t\tupdating range...");
                    updateRange(range);
                    _flush();
                }
            } else {
                context.application().log().info("\t\tcreated");
                updateRange(range);
                _flush();
            }
            range.activate();
            avail = true;
        } catch (Throwable t) {
            context.application().log().error("\t\tfailed to start", t);
        }
    }

    @SuppressWarnings({"TooBroadCatch", "UseSpecificCatch"})
    private void _store() {
        avail = false;
        try {
            context.application().log().info("Counter " + name() + " store: value=" + value.get() + "; limit=" + range.to() + ";");
            flush(new CounterData()
                    .state(CounterState.Stored)
                    .lastValue(value.get())
                    .cacheSize(range.to())
                    .resetTime(range.expire())
            );
        } catch (Throwable t) {
            context.application().log().error("Counter " + name() + " failed to stop", t);
        }
    }

    private synchronized long update() throws Exception {
        long v = value.incrementAndGet();
        if (!range.check(v)) {
            range.deactivate();
            updateRange(range);
            _flush();
            v = range.from();
            value.set(v);
            range.activate();
        }
        return v;
    }

    protected abstract void flush(CounterData data) throws Exception;

    @SuppressWarnings({"TooBroadCatch", "UseSpecificCatch"})
    private void _flush() {
        try {
            context.application().log().info("Counter " + name() + " flush: value=" + range.from() + "; limit=" + range.to() + ";");
            flush(new CounterData()
                    .state(CounterState.Loaded)
                    .lastValue(range.from())
                    .cacheSize(range.to())
                    .resetTime(range.expire())
            );
        } catch (Throwable t) {
            context.application().log().error("Counter " + name() + " failed to flush", t);
        }
    }

    @Override
    public void init(ComponentContext context) {
        this.context = context;
    }

    @Override
    public void prepare() {
        _load();
    }

    @Override
    public void start() {
    }

    @Override
    public void destroy() {
        _store();
    }

    @Override
    public ComponentType type() {
        return ComponentType.Counter;
    }

    @Override
    public String name() {
        return getClass().getName();
    }

    @Override
    public String description() {
        return context.description();
    }

    @Override
    public String version() {
        return context.version();
    }

    @Override
    public IApplication app() {
        return context.application();
    }

    @Override
    public String info() {
        return context.info();
    }

    @Override
    public IBootInfo boot() {
        return context.boot();
    }

    protected class CounterData {

        private long lastValue;
        private long cacheSize;
        private long resetTime;
        private CounterState state;

        public CounterData() {
        }

        public long lastValue() {
            return lastValue;
        }

        public CounterData lastValue(long lastValue) {
            this.lastValue = lastValue;
            return this;
        }

        public long cacheSize() {
            return cacheSize;
        }

        public CounterData cacheSize(long cacheSize) {
            this.cacheSize = cacheSize;
            return this;
        }

        public long resetTime() {
            return resetTime;
        }

        public CounterData resetTime(long resetTime) {
            this.resetTime = resetTime;
            return this;
        }

        public CounterState state() {
            return state;
        }

        public CounterData state(CounterState state) {
            this.state = state;
            return this;
        }

    }

    protected enum CounterState implements ICodedEnum<String> {

        Loaded("L"),
        Stored("S");

        private final String code;

        private CounterState(String code) {
            this.code = code;
        }

        @Override
        public String code() {
            return code;
        }

        @Override
        public Class<String> type() {
            return String.class;
        }

    }

    protected static class Range {

        private volatile boolean actual = false;
        private volatile long from = 0;
        private volatile long to = 0;
        private volatile long expire = Long.MAX_VALUE;
        private final CounterStrategy strategy;

        public Range(CounterStrategy strategy) {
            this.strategy = strategy;
        }

        public long from() {
            return from;
        }

        public void from(long from) {
            this.from = from;
        }

        public long to() {
            return to;
        }

        public void to(long to) {
            this.to = to;
        }

        public long expire() {
            return expire;
        }

        public void expire(long expire) {
            this.expire = expire;
        }

        public boolean check(long value) {
            return actual && value >= from && value < to && System.currentTimeMillis() < expire;
        }

        public void deactivate() {
            this.actual = false;
        }

        public void activate() {
            this.actual = true;
        }

        public boolean expired() {
            return System.currentTimeMillis() >= expire;
        }

        private int toMonday(int day_of_week) {
            if (Calendar.SUNDAY == day_of_week) {
                return 1;
            } else {
                return 8 - day_of_week + 1;
            }
        }

        private int toQuart(int month) {
            switch (month) {
                case Calendar.JANUARY:
                    return 3;
                case Calendar.FEBRUARY:
                    return 2;
                case Calendar.MARCH:
                    return 1;
                case Calendar.APRIL:
                    return 3;
                case Calendar.MAY:
                    return 2;
                case Calendar.JUNE:
                    return 1;
                case Calendar.JULY:
                    return 3;
                case Calendar.AUGUST:
                    return 2;
                case Calendar.SEPTEMBER:
                    return 1;
                case Calendar.OCTOBER:
                    return 3;
                case Calendar.NOVEMBER:
                    return 2;
                case Calendar.DECEMBER:
                    return 1;
            }
            return 0;
        }

        public void updateExpire() {
            if (expired()) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                switch (strategy) {
                    case Daily:
                        calendar.add(Calendar.DAY_OF_MONTH, 1);
                        expire = calendar.getTimeInMillis();
                    case Weekly:
                        calendar.add(Calendar.DAY_OF_MONTH, toMonday(calendar.get(Calendar.DAY_OF_WEEK)));
                        expire = calendar.getTimeInMillis();
                    case Monthly:
                        calendar.set(Calendar.DAY_OF_MONTH, 1);
                        calendar.add(Calendar.MONTH, 1);
                        expire = calendar.getTimeInMillis();
                    case Quarterly:
                        calendar.set(Calendar.DAY_OF_MONTH, 1);
                        calendar.add(Calendar.MONTH, toQuart(calendar.get(Calendar.MONTH)));
                        expire = calendar.getTimeInMillis();
                    case Yearly:
                        calendar.set(Calendar.DAY_OF_MONTH, 1);
                        calendar.set(Calendar.MONTH, 1);
                        calendar.add(Calendar.YEAR, 1);
                        expire = calendar.getTimeInMillis();
                    default:
                        expire = Long.MAX_VALUE;
                }
            }
        }

    }
}
