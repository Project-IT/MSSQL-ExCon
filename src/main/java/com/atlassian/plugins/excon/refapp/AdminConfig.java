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
public class AdminConfig extends HttpServlet {
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
    public AdminConfig(UserManager userManager, LoginUriProvider loginUriProvider, TemplateRenderer templateRenderer, PluginSettingsFactory pluginSettingsFactory) {
        this.userManager = userManager;
        this.loginUriProvider = loginUriProvider;
        this.templateRenderer = templateRenderer;
        this.pluginSettingsFactory = pluginSettingsFactory;
    }

    /**
     * Handles the GET request when the user clicks on the ExCon Synch Configuration tab.
     *
     * Denies whoever that does not have administrative rights
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String username = userManager.getRemoteUsername(request);
        if (username == null || !userManager.isSystemAdmin(username)) {
            redirectToLogin(request, response);
            return;
        }

        response.setContentType("text/html;charset=utf-8");
        templateRenderer.render("configPage.vm", response.getWriter());

    }

        /**
         * Handles the PUT request when the user saves their database credentials.
         *
         * Retrieves the input given from the user and stores it permanently until someone uninstalls the plugin or redos the configuration
         */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {

        PluginSettings pluginSettings = pluginSettingsFactory.createGlobalSettings();

        if (pluginSettings.get(PLUGIN_STORAGE_KEY + ".databaseIP") == null) {
            String databaseIP = "Enter a database IP here.";
            pluginSettings.put(PLUGIN_STORAGE_KEY + ".databaseIP", databaseIP);
        } else {
            pluginSettings.put(PLUGIN_STORAGE_KEY + ".databaseIP", req.getParameter("databaseIP"));
        }

        if (pluginSettings.get(PLUGIN_STORAGE_KEY + ".dataPass") == null) {
            String databasePass = "Enter an database password here.";
            pluginSettings.put(PLUGIN_STORAGE_KEY + ".dataPass", databasePass);
        } else {
            pluginSettings.put(PLUGIN_STORAGE_KEY + ".dataPass", req.getParameter("dataPass"));
        }

        if (pluginSettings.get(PLUGIN_STORAGE_KEY + ".dataUser") == null) {
            String databaseUser = "Enter an database username here.";
            pluginSettings.put(PLUGIN_STORAGE_KEY + ".dataUser", databaseUser);
        } else {
            pluginSettings.put(PLUGIN_STORAGE_KEY + ".dataUser", req.getParameter("dataUser"));
        }

        if (pluginSettings.get(PLUGIN_STORAGE_KEY + ".months") == null) {
            String months = "Enter an database username here.";
            pluginSettings.put(PLUGIN_STORAGE_KEY + ".months", months);
        } else {
            pluginSettings.put(PLUGIN_STORAGE_KEY + ".months", req.getParameter("months"));
        }
            templateRenderer.render("savedConfig.vm", response.getWriter());
    }

    private void redirectToLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect(loginUriProvider.getLoginUri(getUri(request)).toASCIIString());
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