package fr.irisa.diverse.Webserver.Servlets;

import javax.servlet.annotation.WebServlet;

import fr.irisa.diverse.Webserver.Servlets.WebsocketOthers.ServerSocket;
import fr.irisa.diverse.Webserver.Servlets.WebsocketOthers.WorkspaceSocketCreator;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

/**
 * Configure the WebsocketServletFactory to use the workspace socket creator.
 *
 * Created by antoine on 26/06/17
 */

@SuppressWarnings("serial")
@WebServlet(name = "Server WebSocket Servlet")
public class WebsocketServlet extends WebSocketServlet
{
    @Override
    public void configure(WebSocketServletFactory factory)
    {
        // Register ServerSocket as the WebSocket to create on Upgrade
        factory.setCreator(new WorkspaceSocketCreator());
    }
}