package io.finin;

import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;

public class Referentials {

	static Map<String, Source> sources = new HashMap<>();
	static Map<String, Sink> sinks = new HashMap<>();

	static {
		{
			ServiceLoader<Source> srcl = ServiceLoader.load(Source.class);
			Iterator<Source> iterator = srcl.iterator();
			while (iterator.hasNext()) {
				Source next = iterator.next();
				sources.put(next.name(), next);
			}
		}

		{
			ServiceLoader<Sink> snkl = ServiceLoader.load(Sink.class);
			Iterator<Sink> iterator = snkl.iterator();
			while (iterator.hasNext()) {
				Sink next = iterator.next();
				sinks.put(next.name(), next);
			}
		}
	}

}
