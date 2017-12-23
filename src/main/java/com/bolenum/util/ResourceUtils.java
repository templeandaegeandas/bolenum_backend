/**
 * 
 */
package com.bolenum.util;

import java.util.Properties;

import javax.annotation.PostConstruct;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.neemre.btcdcli4j.core.BitcoindException;
import com.neemre.btcdcli4j.core.CommunicationException;
import com.neemre.btcdcli4j.core.client.BtcdClient;
import com.neemre.btcdcli4j.core.client.BtcdClientImpl;

/**
 * @author chandan kumar singh
 * @date 20-Dec-2017
 */
@Component
public class ResourceUtils {

	@Value("${bitcoind.rpc.protocol}")
	private String protocol;
	@Value("${bitcoind.rpc.host}")
	private String host;
	@Value("${bitcoind.rpc.port}")
	private String port;
	@Value("${bitcoind.rpc.user}")
	private String user;
	@Value("${bitcoind.rpc.password}")
	private String password;
	@Value("${bitcoind.http.authScheme}")
	private String authScheme;

	private static Properties nodeConfig;

	@PostConstruct
	void init() {
		nodeConfig = new Properties();
		nodeConfig.setProperty("node.bitcoind.rpc.protocol", protocol);
		nodeConfig.setProperty("node.bitcoind.rpc.host", host);
		nodeConfig.setProperty("node.bitcoind.rpc.port", port);
		nodeConfig.setProperty("node.bitcoind.rpc.user", user);
		nodeConfig.setProperty("node.bitcoind.rpc.password", password);
		nodeConfig.setProperty("node.bitcoind.http.auth_scheme", authScheme);
	}

	private ResourceUtils() {

	}

	public static CloseableHttpClient getHttpProvider() {
		PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
		return HttpClients.custom().setConnectionManager(connManager).build();

	}

	public static BtcdClient getBtcdProvider() throws BitcoindException, CommunicationException {
		return new BtcdClientImpl(getHttpProvider(), getNodeConfig());
	}

	public static Properties getNodeConfig() {
		return nodeConfig;
	}
}
