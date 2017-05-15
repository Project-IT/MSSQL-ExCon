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
public class MyPluginServlet extends HttpServlet {
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
    public MyPluginServlet(UserManager userManager, LoginUriProvider loginUriProvider, TemplateRenderer templateRenderer, PluginSettingsFactory pluginSettingsFactory) {
        this.userManager = userManager;
        this.loginUriProvider = loginUriProvider;
        this.templateRenderer = templateRenderer;
        this.pluginSettingsFactory = pluginSettingsFactory;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
      /*  Map<String, Object> context = new HashMap<String, Object>();

        PluginSettings pluginSettings = pluginSettingsFactory.createGlobalSettings();

        if (pluginSettings.get(PLUGIN_STORAGE_KEY + ".name") == null) {
            String noName = "Enter a name here.";
            pluginSettings.put(PLUGIN_STORAGE_KEY + ".name", noName);
        }

        if (pluginSettings.get(PLUGIN_STORAGE_KEY + ".password") == null) {
            String noAge = "Enter an password here.";
            pluginSettings.put(PLUGIN_STORAGE_KEY + ".password", noAge);
        }

        if (pluginSettings.get(PLUGIN_STORAGE_KEY + ".ParentID") == null) {
            String noParentID = "Enter a Calendar name here.";
            pluginSettings.put(PLUGIN_STORAGE_KEY + ".ParentID", noParentID);
        }

        context.put("name", pluginSettings.get(PLUGIN_STORAGE_KEY + ".name"));
        context.put("password", pluginSettings.get(PLUGIN_STORAGE_KEY + ".password"));
        context.put("ParentID", pluginSettings.get(PLUGIN_STORAGE_KEY + ".ParentID"));*/

        response.setContentType("text/html;charset=utf-8");
        templateRenderer.render("admin.vm", response.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
        PluginSettings pluginSettings = pluginSettingsFactory.createGlobalSettings();
        String url = String.valueOf((pluginSettings.get(PLUGIN_STORAGE_KEY + ".databaseIP")));
        String dataPass = String.valueOf((pluginSettings.get(PLUGIN_STORAGE_KEY + ".dataPass")));
        String dataUser = String.valueOf((pluginSettings.get(PLUGIN_STORAGE_KEY + ".dataUser")));
        int months =  Integer.parseInt(String.valueOf((pluginSettings.get(PLUGIN_STORAGE_KEY + ".months"))));
        ExCon exCon = new ExCon();
        exCon.execute(req.getParameter("name"), req.getParameter("password"), req.getParameter("ParentID"), url, dataPass, dataUser, months);
        //response.sendRedirect("savedSynch");
        response.setContentType("text/html;charset=utf-8");
        templateRenderer.render("savedSynch.vm", response.getWriter());
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

    // This is what your MyPluginServlet.java should look like in its final stages.

}