package io.finin;

import java.net.URL;

/**
 * TODO: Javadoc
 */
public interface RemoteDataAccessor {

  String[] understands();

  Updater getUpdater(URL url);

  Selector getSelector(URL url);

}
