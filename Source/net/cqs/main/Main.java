package net.cqs.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.cqs.i18n.FileStringBundleFactory;
import net.cqs.i18n.ResourceStringBundleFactory;
import net.cqs.i18n.StringBundleFactory;
import net.cqs.main.config.FrontEnd;
import net.cqs.main.i18n.PoFileMerger;
import net.cqs.main.i18n.PotFileCreator;
import net.cqs.main.js.JsLint;
import net.cqs.main.resource.ClasspathResourceManager;
import net.cqs.main.resource.FileResourceManager;
import net.cqs.main.resource.ResourceManager;
import net.cqs.main.setup.GameConfiguration;
import net.cqs.main.setup.GameConfigurationParser;
import net.cqs.main.setup.SetupWizard;
import net.cqs.main.setup.StorageManagerFactory;
import net.cqs.storage.BDBStorageManager;
import net.cqs.storage.StorageManager;

public final class Main implements Application {

  private final ResourceManager resourceManager;
  private final File dataPath;

  private Main(ResourceManager ressourceManager, File dataPath) {
    this.resourceManager = ressourceManager;
    this.dataPath = dataPath;
  }

  @Override
  public void run(String[] args) throws IOException {
    String buildId = readBuildId();
    if (!dataPath.exists()) {
      if ("service".equals(args[0])) {
        new SetupWizard(dataPath).run(args);
      } else {
        System.err.println("Data directory does not exist! Run setup first!");
        return;
      }
    }

    final File logPath = new File(dataPath, "Logs/");
    if (!logPath.exists()) {
      logPath.mkdirs();
    }
    final File datastorePath = new File(dataPath, "DataStore");
    if (!datastorePath.exists()) {
      datastorePath.mkdirs();
    }

    final File configFile = new File(dataPath, "config.xml");
    XmlConversion<GameConfiguration> converter = new GameConfigurationParser();
    GameConfiguration gameConfiguration = converter.fromXml(new FileInputStream(configFile));
    if (!GameConfiguration.CONFIGURATION_VERSION.equals(gameConfiguration.getConfigVersion())) {
      System.err.println("Please update your configuration to version \""
          + GameConfiguration.CONFIGURATION_VERSION + "\"!");
      return;
    }
    StorageManagerFactory factory = new StorageManagerFactory() {
      @Override
      public StorageManager create() {
        return new BDBStorageManager(datastorePath);
      }
    };
    StorageManager storageManager = factory.create();

    FrontEnd frontEnd = new FrontEnd(buildId, resourceManager, storageManager, logPath,
        gameConfiguration);
    frontEnd.start();
  }

  private static final Pattern METADATA_PATTERN = Pattern.compile("([a-zA-Z-]+): (.*)");

  private static String readBuildId() throws IOException {
    File f = new File("manifest.txt");
    if (!f.exists()) {
      return "";
    }
    InputStream in = new FileInputStream(f);
    BufferedReader reader = new BufferedReader(new InputStreamReader(in, Charset.forName("UTF-8")));
    String line;
    while ((line = reader.readLine()) != null) {
      if (line.isEmpty()) {
        break;
      }
      Matcher m = METADATA_PATTERN.matcher(line);
      if (m.matches()) {
        if ("Version".equals(m.group(1))) {
          return m.group(2);
        }
      }
    }
    return "";
  }

  private static Application get(String[] args) {
    final File dataPath = new File("Demo/");
    final ResourceManager resourceManager;

    if (!new File("Html/").exists()) {
      resourceManager = new ClasspathResourceManager();
    } else {
      resourceManager = new FileResourceManager(new File("./"));
    }

    final File i18nDir = new File("I18n");
    final StringBundleFactory factory;
    if (!i18nDir.exists()) {
      factory = new ResourceStringBundleFactory("cqs");
    } else {
      factory = new FileStringBundleFactory(i18nDir, "cqs");
    }
    StringBundleFactory.init(factory);

    if ("setup".equals(args[0]))
      return new SetupWizard(dataPath);
    else if ("start".equals(args[0]) || "service".equals(args[0]))
      return new Main(resourceManager, dataPath);
    else if ("pot".equals(args[0]))
      return new PotFileCreator(resourceManager);
    else if ("mergePot".equals(args[0]))
      return new PoFileMerger(resourceManager);
    else if ("jslint".equals(args[0]))
      return new JsLint();
    else
      return null;
  }

  public static void main(String[] args) throws IOException {
    if (args.length >= 1) {
      Application app = get(args);
      if (app != null) {
        app.run(args);
      } else {
        System.err.println("Unrecognized command line parameter: \"" + args[0] + "\"");
        return;
      }
    } else {
      System.err.println("You must provide a command [start,setup,service,pot,mergePot,jslint]!");
      return;
    }
  }
}
