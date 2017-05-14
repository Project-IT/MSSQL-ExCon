package com.atlassian.plugins.tutorial.refapp;

import java.util.HashMap;
import java.util.Map;
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


@Scanned
public class AdminConfig extends HttpServlet {
    private static final String PLUGIN_STORAGE_KEY = "com.atlassian.plugins.tutorial.refapp.adminui";
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

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String username = userManager.getRemoteUsername(request);
        if (username == null || !userManager.isSystemAdmin(username)) {
            redirectToLogin(request, response);
            return;
        }
    /*    Map<String, Object> context = new HashMap<String, Object>();
        PluginSettings pluginSettings = pluginSettingsFactory.createGlobalSettings();
        if (pluginSettings.get(PLUGIN_STORAGE_KEY + ".name") == null) {
            String noName = "Enter a name here.";
            pluginSettings.put(PLUGIN_STORAGE_KEY + ".name", noName);
        }
        if (pluginSettings.get(PLUGIN_STORAGE_KEY + ".password") == null) {
            String noPassword = "Enter an password here.";
            pluginSettings.put(PLUGIN_STORAGE_KEY + ".password", noPassword);
        }
        if (pluginSettings.get(PLUGIN_STORAGE_KEY + ".ParentID") == null) {
            String noParentID = "Enter an age here.";
            pluginSettings.put(PLUGIN_STORAGE_KEY + ".ParentID", noParentID);
        }
        context.put("name", pluginSettings.get(PLUGIN_STORAGE_KEY + ".name"));
        context.put("password", pluginSettings.get(PLUGIN_STORAGE_KEY + ".password"));
        context.put("ParentID", pluginSettings.get(PLUGIN_STORAGE_KEY + ".ParentID"));*/
        response.setContentType("text/html;charset=utf-8");
        templateRenderer.render("config.vm", response.getWriter());

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
        PluginSettings pluginSettings=pluginSettingsFactory.createGlobalSettings();
       /* ExCon exCon = new ExCon();
        exCon.execute(req.getParameter("name"), req.getParameter("password"), req.getParameter("ParentID"));*/


        if (pluginSettings.get(PLUGIN_STORAGE_KEY + ".databaseIP") == null) {
            String noName = "Enter a database IP here.";
            pluginSettings.put(PLUGIN_STORAGE_KEY + ".databaseIP", noName);
        }
        else{
            pluginSettings.put(PLUGIN_STORAGE_KEY + ".databaseIP", req.getParameter("databaseIP"));
        }

        if (pluginSettings.get(PLUGIN_STORAGE_KEY + ".dataPass") == null) {
            String noPassword = "Enter an database password here.";
            pluginSettings.put(PLUGIN_STORAGE_KEY + ".dataPass", noPassword);
        }
        else{
            pluginSettings.put(PLUGIN_STORAGE_KEY + ".dataPass", req.getParameter("dataPass"));
        }

        if (pluginSettings.get(PLUGIN_STORAGE_KEY + ".dataUser") == null) {
            String noParentID = "Enter an database username here.";
            pluginSettings.put(PLUGIN_STORAGE_KEY + ".dataUser", noParentID);
        }
        else{
            pluginSettings.put(PLUGIN_STORAGE_KEY + ".dataUser", req.getParameter("dataUser"));
        }

        response.sendRedirect("http://localhost:8090/plugins/servlet/test");
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