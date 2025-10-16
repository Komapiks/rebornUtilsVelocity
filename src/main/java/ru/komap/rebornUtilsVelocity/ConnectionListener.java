package ru.komap.rebornUtilsVelocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.komap.rebornUtilsVelocity.logger.LoggerTerminal;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class ConnectionListener {
    private final Map<Integer, RegisteredServer> map = new ConcurrentHashMap<>();
    LoggerTerminal terminal;
    private final ProxyServer proxy;

    public ConnectionListener(ProxyServer proxy) {
        this.proxy = proxy;
    }

    public void put(int protocol, RegisteredServer server) {
        map.put(protocol, server);
    }

    @Inject
    private Logger logger =  LoggerFactory.getLogger(ConnectionListener.class);

    //@Subscribe
    /*public void onChooseInitialServer(PlayerChooseInitialServerEvent event) {
        this.terminal = new LoggerTerminal(logger);
        Player player = event.getPlayer();
        int protoId = player.getProtocolVersion().getProtocol();


        //RegisteredServer target = map.get(protoId);
        //if (target != null) {
            //terminal.Info("[RebornUtilsVelocity] User " + player.getUsername() + " connected with protocol " + protoId);
            //event.setInitialServer(target);
        //} else {
            //terminal.Info("[RebornUtilsVelocity] User " + player.getUsername() + " connected with protocol " + protoId);
            //terminal.Info("[RebornUtilsVelocity] No registered server found for " + protoId);
            //player.disconnect(Component.text("Используйте версии 1.21.7 или 1.20.1!" + protoId));
        //}

        switch(protoId) {
            case 772:
                player.createConnectionRequest(proxy.getServer("vanilla").get()).connect();
                //event.setInitialServer(proxy.getServer("vanilla").get());
            case 763:
                player.createConnectionRequest(proxy.getServer("test").get()).connect();
                //event.setInitialServer(proxy.getServer("test").get());
                terminal.Info("Connected to server");
        }
    }*/

    @Subscribe
    public void onServerPreConnect(ServerPreConnectEvent event) {
        Player player = event.getPlayer();
        RegisteredServer original = event.getOriginalServer(); // куда пытался пойти игрок
        int proto = player.getProtocolVersion().getProtocol();

        logger.info("ServerPreConnect: {} wants {} (proto={})", player.getUsername(),
                original.getServerInfo().getName(), proto);

        // Пример: если сервер "modded" требует протокол >= 772, а у игрока ниже -> перенаправляем
        if (original.getServerInfo().getName().equals("vanilla") && proto < 772) {
            RegisteredServer fallback = proxy.getServer("test").orElse(null);
            if (fallback != null) {
                event.setResult(ServerPreConnectEvent.ServerResult.allowed(fallback));
                logger.info("Redirecting {} -> test because proto {}", player.getUsername(), proto);
            } else {
                // запретить подключение (можно вернуть denied с сообщением)
                event.setResult(ServerPreConnectEvent.ServerResult.denied());
            }
        }
    }

    @Subscribe
    public void onKickedFromServer(KickedFromServerEvent event) {
        Component reason = event.getServerKickReason().orElse(Component.text("no reason component"));
        boolean duringConnect = event.kickedDuringServerConnect();
        String serverName = event.getServer().getServerInfo().getName();

        logger.info("KickedFromServerEvent: player={}, server={}, duringConnect={}, reason={}",
                event.getPlayer().getUsername(), serverName, duringConnect, reason);
        // Также можно временно перенаправить на другой сервер:
        // if (duringConnect) event.setResult(KickedFromServerEvent.RedirectPlayer.create(anotherServer, Component.text("Redirecting...")));
    }
}
