package aaa.abc.dd.k.plain.sources.simple.common;

import org.apache.zookeeper.server.ZooKeeperServer;
import org.apache.zookeeper.server.NIOServerCnxnFactory;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.UUID;

public class ZooKeeperServerService implements AutoCloseable {
    ZooKeeperServer zooKeeperServer;
    NIOServerCnxnFactory factory;
    InetSocketAddress address;
    String host;
    int port;
    File snapshotDir;
    File logDir;

    public ZooKeeperServerService(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() {
        snapshotDir = Utils.tempDirectory(null, UUID.randomUUID().toString());
        logDir = Utils.tempDirectory(null, UUID.randomUUID().toString());
        int tickTime = 500;
        try {
            zooKeeperServer = new ZooKeeperServer(snapshotDir, logDir, tickTime);
            factory = new NIOServerCnxnFactory();
            address = new InetSocketAddress(host, port);
            factory.configure(address, 0);
            factory.startup(zooKeeperServer);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        zooKeeperServer.shutdown(true);
        factory.shutdown();
        Utils.delete(snapshotDir);
        Utils.delete(logDir);
    }
}
