package kin.sdk.core;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;

import java.io.File;

public class BaseTest {

    private final String PARITY_PROVIDER_URL = "http://207.154.247.11:8545";

    private Context context;
    private ServiceProvider serviceProvider;
    protected KinClient kinClient;

    @Before
    public void setUp() throws Exception {
        context = InstrumentationRegistry.getContext();
        serviceProvider = new ServiceProvider(PARITY_PROVIDER_URL, ServiceProvider.NETWORK_ID_ROPSTEN);
        kinClient = new KinClient(context, serviceProvider);
    }

    @After
    public void tearDown() throws Exception {
        clearKeyStore();
    }

    private void clearKeyStore() {
        // Removes the previews KeyStore if exists
        String networkId = String.valueOf(serviceProvider.getNetworkId());
        String keyStorePath = new StringBuilder(context.getFilesDir().getAbsolutePath())
            .append(File.separator)
            .append("kin")
            .append(File.separator)
            .append("keystore")
            .append(File.separator)
            .append(networkId).toString();

        File keystoreDir = new File(keyStorePath);
        FileUtils.deleteRecursive(keystoreDir);
    }
}
