package com.sonatype.blametest.dropwizard;

import com.sonatype.blametest.resources.BlameTestResource;
import com.sonatype.blametest.resources.HelloWorldResource;

import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;

public class HelloWorldApplication extends Application<HelloWorldConfiguration> {
  public static void main(String[] args) throws Exception {
    new HelloWorldApplication().run(args);
  }

  @Override
  public String getName() {
    return "hello-world";
  }

  @Override
  public void initialize(Bootstrap<HelloWorldConfiguration> bootstrap) {
    bootstrap.addBundle(new ViewBundle<>());
    bootstrap.addBundle(new AssetsBundle("/assets/", "/assets"));
  }

  @Override
  public void run(HelloWorldConfiguration configuration,
                  Environment environment) {
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
