package net.cqs.main.config;

import java.io.Serializable;

public final class CoreSettings implements Serializable {

  private static final long serialVersionUID = 1L;

  private final String url;
  private final String email;
  private final boolean debug;

  public CoreSettings(String url, String email, boolean debug) {
    this.url = url;
    this.email = email;
    this.debug = debug;
  }

  public String url() {
    return url;
  }

  public String eMail() {
    return email;
  }

  public boolean debug() {
    return debug;
  }

  public CoreSettings setUrl(String newurl) {
    return new CoreSettings(newurl, email, debug);
  }

  public CoreSettings setEmail(String newemail) {
    return new CoreSettings(url, newemail, debug);
  }

  public CoreSettings setDebug(boolean newdebug) {
    return new CoreSettings(url, email, newdebug);
  }
}
