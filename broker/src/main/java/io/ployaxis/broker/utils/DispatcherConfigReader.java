package io.ployaxis.broker.utils;

import io.polyaxis.api.utils.context.EnvironmentUtils;
import io.polyaxis.api.utils.data.Pair;
import io.polyaxis.api.utils.io.IOUtils;
import io.polyaxis.api.utils.io.net.IPUtils;
import io.polyaxis.api.utils.misc.StringUtils;
import jakarta.annotation.Nonnull;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/// Dispatcher Config Reader.
///
/// @author github.com/MoritzArena
/// @date 2025/07/08
/// @since 1.0
public class DispatcherConfigReader {

    private static final String DEFAULT_DISPATCHER_FILE_PATH = "dispatcher.conf";

    private static final String DISPATCHER_LIST_PROPERTY = "io.polyaxis.dispatcher.list";

    private static List<Pair<String, Integer>> dispatcherAddresses;

    @Nonnull
    public static List<Pair<String, Integer>> getDispatcherAddresses() {
        if (dispatcherAddresses != null && !dispatcherAddresses.isEmpty()) {
            return dispatcherAddresses;
        }
        try {
            resolveDispatcherConf();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return dispatcherAddresses;
    }

    /// Read dispatcher.conf to ip list.
    ///
    /// @throws IOException ioexception [IOException]
    private static void resolveDispatcherConf() throws IOException {
        dispatcherAddresses = new ArrayList<>();
        try (final var reader = new InputStreamReader(
                new FileInputStream(getDispatcherConfFilePath()), StandardCharsets.UTF_8)) {
            final List<String> clusterResult = analyzeDispatcherConf(reader);
            dispatcherAddresses.addAll(splitIpAndPort(clusterResult));
        } catch (FileNotFoundException ignored) {
            final var tmp = new ArrayList<String>();
            String clusters = getDispatcherList();
            if (StringUtils.isNotBlank(clusters)) {
                final String[] details = clusters.split(",");
                for (final var item : details) {
                    tmp.add(item.trim());
                }
            }
            dispatcherAddresses.addAll(splitIpAndPort(tmp));
        }
    }

    /// split an ip address to a pair of ip and port.
    private static List<Pair<String, Integer>> splitIpAndPort(
            final List<String> ipList
    ) {
        return ipList.stream().map(ip -> {
            final var ipPortPair = IPUtils.splitIPPortStr(ip.trim());
            if (ipPortPair.length != 2) {
                throw new IllegalArgumentException("cannot resolve ip address: " + ip);
            }
            return Pair.of(ipPortPair[0], Integer.parseInt(ipPortPair[1]));
        }).toList();
    }

    private static String getDispatcherConfFilePath() {
        return Paths.get("D:\\Projects\\poly-axis\\work",
                "conf", DEFAULT_DISPATCHER_FILE_PATH).toString();
    }

    /// read file stream to ip list.
    ///
    /// @param reader reader
    /// @return ip list.
    /// @throws IOException IOException
    private static List<String> analyzeDispatcherConf(Reader reader) throws IOException {
        final var instanceList = new ArrayList<String>();
        final var lines = IOUtils.readLines(reader);
        for (final var line : lines) {
            String instance = line.trim();
            if (instance.startsWith(StringUtils.SHARP)) {
                // # it is ip
                continue;
            }
            if (instance.contains(StringUtils.SHARP)) {
                // 192.168.71.52:8848 # Instance A
                instance = instance.substring(0, instance.indexOf(StringUtils.SHARP));
                instance = instance.trim();
            }
            int multiIndex = instance.indexOf(StringUtils.COMMA);
            if (multiIndex > 0) {
                // support the format: ip1:port,ip2:port  # multi inline
                instanceList.addAll(Arrays.asList(instance.split(StringUtils.COMMA)));
            } else {
                // support the format: 192.168.71.52:7777
                instanceList.add(instance);
            }
        }
        return instanceList;
    }

    private static String getDispatcherList() {
        return EnvironmentUtils.getProperty(DISPATCHER_LIST_PROPERTY);
    }
}
