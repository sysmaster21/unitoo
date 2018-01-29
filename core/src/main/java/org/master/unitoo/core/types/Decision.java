/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.types;

/**
 *
 * @author Andrey
 */
public enum Decision {

    Use,
    Ignore,
    Parent;

    public static boolean Get(boolean parent, Decision current) {
        switch (current == null ? Parent : current) {
            case Use:
                return true;
            case Ignore:
                return false;
            default:
                return parent;
        }
    }

}
