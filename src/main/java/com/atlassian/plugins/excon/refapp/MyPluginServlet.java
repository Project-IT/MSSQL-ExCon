package com.atlassian.plugins.excon.refapp;

import java.io.IOException;
import java.net.URI;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;

import javax.inject.Inject;

import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;

/**
 * Written by ExCon Group from KTH Sweden - Code is available freely at our Github
 * under the GNU GPL.
 *
 * Most of the code below is retrieved from atlassian
 *
 * https://developer.atlassian.com/docs/getting-started/plugin-modules/servlet-plugin-module
 *
 */
@Scanned
public class MyPluginServlet extends HttpServlet {
    private static final String PLUGIN_STORAGE_KEY = "com.atlassian.plugins.excon.refapp.adminui";
    @ComponentImport
    private final UserManager userManager;
    @ComponentImport
    private final LoginUriProvider loginUriProvider;
    @ComponentImport
    private final TemplateRenderer templateRenderer;
    @ComponentImport
    private final PluginSettingsFactory pluginSettingsFactory;

    @Inject
    public MyPluginServlet(UserManager userManager, LoginUriProvider loginUriProvider, TemplateRenderer templateRenderer, PluginSettingsFactory pluginSettingsFactory) {
        this.userManager = userManager;
        this.loginUriProvider = loginUriProvider;
        this.templateRenderer = templateRenderer;
        this.pluginSettingsFactory = pluginSettingsFactory;
    }

    /**
     * Handles the GET request when the user clicks on the button on top of the dashboard.
     *
     * Redirects to loginPage where the user enter their credentials
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("text/html;charset=utf-8");
        templateRenderer.render("loginPage.vm", response.getWriter());
    }


    /**
     * Handles the PUT request when the user has submitted their credentials on loginPage.
     *
     * Retrieves the database credentials and forwards them & login credentials to the main class ExCon which performs the synch
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
        PluginSettings pluginSettings = pluginSettingsFactory.createGlobalSettings();
        String url = String.valueOf((pluginSettings.get(PLUGIN_STORAGE_KEY + ".databaseIP")));
        String dataPass = String.valueOf((pluginSettings.get(PLUGIN_STORAGE_KEY + ".dataPass")));
        String dataUser = String.valueOf((pluginSettings.get(PLUGIN_STORAGE_KEY + ".dataUser")));
        int months = Integer.parseInt(String.valueOf((pluginSettings.get(PLUGIN_STORAGE_KEY + ".months"))));

        ExCon exCon = new ExCon();
        exCon.execute(req.getParameter("name"), req.getParameter("password"), req.getParameter("ParentID"), url, dataPass, dataUser, months);

        response.setContentType("text/html;charset=utf-8");
        templateRenderer.render("savedLogin.vm", response.getWriter());
    }

    private URI getUri(HttpServletRequest request) {
        StringBuffer builder = request.getRequestURL();
        if (request.getQueryString() != null) {
            builder.append("?");
            builder.append(request.getQueryString());
        }
        return URI.create(builder.toString());
    }
}