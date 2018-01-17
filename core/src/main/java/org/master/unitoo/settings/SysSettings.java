/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.settings;

import org.master.unitoo.core.api.annotation.Component;
import org.master.unitoo.core.server.Setting;
import org.master.unitoo.core.api.annotation.Attribute;
import org.master.unitoo.core.base.BaseSettings;

/**
 *
 * @author Andrey
 */
@Component("system")
public class SysSettings extends BaseSettings {

    @Attribute(name = "instance.id", value = "UNITOO")
    public Setting<String> instanceId;

    @Attribute(name = "threads.min", value = "2")
    public Setting<Integer> threadsMin;

    @Attribute(name = "threads.max", value = "5")
    public Setting<Integer> threadsMax;

    @Attribute(name = "threads.keepalive.sec", value = "10")
    public Setting<Integer> threadsKeep;

    @Attribute(name = "home.folder", value = "")
    public Setting<String> home;
}
