package com.tikal.lr;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arangodb.ArangoDB;
import com.arangodb.ArangoDatabase;
import com.arangodb.entity.CollectionType;
import com.arangodb.model.CollectionCreateOptions;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;

@ApplicationScoped
public class ApplicationLifecycle {
	private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationLifecycle.class);

	@ConfigProperty(name = "db.name", defaultValue = "lire")
	String dbName;

	@Produces
	public ArangoDatabase database() {
		return new ArangoDB.Builder().build().db(dbName);
	}

	void onStart(@Observes StartupEvent event) {
		LOGGER.info("The application is starting...");

		if (!database().exists()) {
			database().create();
		}

		if (!database().collection("templates").exists()) {
			database().collection("templates").create(new CollectionCreateOptions().type(CollectionType.DOCUMENT));
		}

		if (!database().collection("notes").exists()) {
			database().collection("notes").create(new CollectionCreateOptions().type(CollectionType.DOCUMENT));
		}

		if (!database().collection("permissions").exists()) {
			database().collection("permissions").create(new CollectionCreateOptions().type(CollectionType.DOCUMENT));
		}

	}

	void onStop(@Observes ShutdownEvent event) {
		LOGGER.info("The application is stopping...");
	}
}
