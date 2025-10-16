package ru.komap.rebornUtilsVelocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;
import ru.komap.rebornUtilsVelocity.logger.LoggerTerminal;

@Plugin(id = "rebornutilsvelocity", name = "rebornUtilsVelocity", version = "1.0-SNAPSHOT")
public class RebornUtilsVelocity {
    private final ProxyServer proxy;
    private final ConnectionListener connectionListener;
    LoggerTerminal terminal;

    @Inject
    private Logger logger;

    @Inject
    public RebornUtilsVelocity(ProxyServer proxy) {
        this.proxy = proxy;
        this.connectionListener = new ConnectionListener(proxy);
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        this.terminal = new LoggerTerminal(logger);
        proxy.getServer("vanilla").ifPresent(s -> connectionListener.put(772, s));
        proxy.getServer("test").ifPresent(s -> connectionListener.put(763, s));
        proxy.getEventManager().register(this, new ConnectionListener(proxy));
        terminal.Info("[RebornUtilsVelocity] Init plugin");
        terminal.Info("[RebornUtilsVelocity] Servers: " + proxy.getAllServers().toString());
    }
}
