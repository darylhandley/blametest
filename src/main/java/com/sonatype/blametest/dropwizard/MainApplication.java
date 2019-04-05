package com.sonatype.blametest.dropwizard;

import com.sonatype.blametest.Config;
import com.sonatype.blametest.resources.BlameTestResource;
import com.sonatype.blametest.resources.HelloWorldResource;

import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainApplication extends Application<MainConfiguration> {

  private static final Logger LOG = LoggerFactory.getLogger(MainApplication.class);


  public static void main(String[] args) throws Exception {
    new MainApplication().run(args);
  }

  @Override
  public String getName() {
    return "hello-world";
  }

  @Override
  public void initialize(Bootstrap<MainConfiguration> bootstrap) {
    bootstrap.addBundle(new ViewBundle<>());
    bootstrap.addBundle(new AssetsBundle("/assets/", "/assets"));
  }

  @Override
  public void run(MainConfiguration configuration,
                  Environment environment) {

    // check we have an APK key
    if (Config.getGithubApiKey() == null) {
      LOG.error("GITHUB_API_KEY not found, exiting");
      throw new RuntimeException("GITHUB_API_KEY not found, exiting");
    }


    final HelloWorldResource resource = new HelloWorldResource(
        configuration.getTemplate(),
        configuration.getDefaultName()
    );
    final TemplateHealthCheck healthCheck =
        new TemplateHealthCheck(configuration.getTemplate());
    environment.healthChecks().register("template", healthCheck);

    environment.jersey().register(resource);
    environment.jersey().register(new BlameTestResource());


  }

}
