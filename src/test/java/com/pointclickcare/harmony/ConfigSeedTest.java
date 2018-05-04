package com.pointclickcare.harmony;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.cfg4j.source.context.propertiesprovider.YamlBasedPropertiesProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import com.orbitz.consul.Consul;
import com.orbitz.consul.KeyValueClient;

public class ConfigSeedTest
{

  private static final String CONFIG_PATH = "./ServiceConfigurations";
  private static final String CONSUL_PROTOCOL = "http";
  private static final int CONSUL_PORT = 8500;
  private static final String CONSUL_HOST = "192.168.99.100"; // consul in
                                                              // windows docker
                                                              // host
  private static final String CONSUL_GLOBAL_PREFIX = "ServiceConfigurations";
  private static final int FAIL_LIMIT = 2;
  private static final int WAIT_TIME = 0;

  @InjectMocks
  private ConfigSeedApplication seed;

  private Consul consul;
  private KeyValueClient kvClient;

  @Before
  public void setup() throws IllegalAccessException
  {
    MockitoAnnotations.initMocks(this);
    List<String> yamlExtensions = new ArrayList<>();
    yamlExtensions.add(".yaml");
    yamlExtensions.add(".yml");
    FieldUtils.writeField(seed, "yamlExtensions", yamlExtensions, true);
    FieldUtils.writeField(seed, "protocol", CONSUL_PROTOCOL, true);
    FieldUtils.writeField(seed, "host", CONSUL_HOST, true);
    FieldUtils.writeField(seed, "port", CONSUL_PORT, true);
    FieldUtils.writeField(seed, "globalPrefix", CONSUL_GLOBAL_PREFIX, true);
    FieldUtils.writeField(seed, "failLimit", FAIL_LIMIT, true);
    FieldUtils.writeField(seed, "waitTimeBetweenFails", WAIT_TIME, true);
    FieldUtils.writeField(seed, "configPath", CONFIG_PATH, true);
    List<String> acceptableFileExtenstions = new ArrayList<>();
    acceptableFileExtenstions.addAll(yamlExtensions);
    acceptableFileExtenstions.add(".properties");
    FieldUtils.writeField(seed, "acceptablePropertyExtensions",
        acceptableFileExtenstions, true);
    consul = seed.getConsul();
    assertNotNull(
        "Unable to get Consul instance for testing (check that Consul is running).",
        consul);
    kvClient = consul.keyValueClient();
    assertNotNull("Key Value Client is null.  Can't run tests", kvClient);
  }

  @After
  public void cleanup()
  {
    KeyValueClient kvClient = consul.keyValueClient();
    kvClient.deleteKeys(CONSUL_GLOBAL_PREFIX);
  }

  @Test
  public void testSelectPropertiesProviderByExtentionForYaml()
  {
    assertTrue("Yaml property provider not returned for yaml file extenstion",
        seed.selectPropertiesProviderByExtention(
            "test.yaml") instanceof YamlBasedPropertiesProvider);
  }

  @Test
  public void testSelectPropertiesProviderByExtentionForNonYaml()
  {
    assertFalse(
        "Property provider should not be YamlBase for non-yaml file extenstion",
        seed.selectPropertiesProviderByExtention(
            "test.properties") instanceof YamlBasedPropertiesProvider);
  }

  @Test
  public void testIsConfigInitialized()
  {
    assertFalse("Config should not be initialized by default",
        seed.isConfigInitialized(kvClient));
  }

  @Test
  public void testIsConfigInitializedAfterLoad() throws IOException
  {
    seed.loadConfigFromPath(kvClient);
    assertTrue("Config should be initialized after load",
        seed.isConfigInitialized(kvClient));
  }

  @Test
  public void testConsulConnectWithRetry() throws InterruptedException
  {
    assertNotNull("Consul not obtained", seed.consulWithRetry());
  }

  @Test
  public void testConsulConnectWithRetryFailLimitExceeded()
      throws InterruptedException, IllegalAccessException
  {
    FieldUtils.writeField(seed, "failLimit", -1, true);
    assertNull("Consul obtained when fail limit exceeded",
        seed.consulWithRetry());
  }

  @Test
  public void testConsulConnectWithRetryExhaustedTries()
      throws InterruptedException, IllegalAccessException
  {
    FieldUtils.writeField(seed, "host", "1.1.1.1", true);// set bad host
    assertNull("Consul obtained when fail limit exceeded",
        seed.consulWithRetry());
  }

  @Test(expected = IOException.class)
  public void testLoadConfigWithBadPath()
      throws IllegalAccessException, IOException
  {
    FieldUtils.writeField(seed, "configPath", "foobar", true);
    seed.loadConfigFromPath(kvClient);
  }

  @Test
  public void testSuccessfulFindOfOneProperty() throws IOException
  {
    seed.loadConfigFromPath(kvClient);
    assertTrue("Application log level is not present",
        kvClient.getValue(CONFIG_PATH + "dev/empi/logging.level.org.harmony")
            .isPresent());
  }

  @Test
  public void testUnsuccessfulFindOfOneProperty() throws IOException
  {
    seed.loadConfigFromPath(kvClient);
    assertFalse("Foobar is present", kvClient.getValue("foobar").isPresent());
  }

}
