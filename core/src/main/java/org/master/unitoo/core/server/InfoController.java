/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.master.unitoo.core.server;

import org.master.unitoo.core.api.IComponent;
import org.master.unitoo.core.api.IControllerMethod;
import org.master.unitoo.core.api.IInterfacedComponent;
import org.master.unitoo.core.base.BaseController;
import org.master.unitoo.core.types.RequestMethod;
import org.master.unitoo.core.api.annotation.Component;
import org.master.unitoo.core.api.components.IController;
import org.master.unitoo.core.api.annotation.Request;

/**
 *
 * @author Andrey
 */
@Component(value = "server", url = "/server")
public class InfoController extends BaseController {

    @Request(value = "info", type = RequestMethod.GET)
    public StringBuilder main() {
        StringBuilder buf = new StringBuilder();
        buf.append("<html><body>");
        buf.append("<p>Server '").append(app().serverId()).append("' with application '").append(app().appName()).append("' started!</p>");
        buf.append("<p>");

        Iterable<IComponent> components = app().components(IComponent.class);
        for (IComponent component : components) {
            buf.append("<br><b>").append(component.name()).append("</b>");
            if (component.version() != null && !component.version().trim().isEmpty()) {
                buf.append(" (v.").append(component.version()).append(")");
            }
            for (Throwable error : component.boot().errors()) {
                buf.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;").append(error.getMessage());
            }

            if (component instanceof IController) {
                buf.append("<br>");
                IController item = (IController) component;
                buf.append(" ").append(item.url());
                for (IControllerMethod method : item.methods()) {
                    buf.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;").append(method.name()).append(" ").append(method.mapping());
                }
                buf.append("<br>");
            }

            if (component instanceof IInterfacedComponent) {
                IInterfacedComponent item = (IInterfacedComponent) component;
                for (Class iface : item.interfaces()) {
                    buf.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;").append(iface.getName());
                }
                buf.append("<br>");
            }

        }
        buf.append("</p>");
        buf.append("</body></html>");
        return buf;
    }

}
