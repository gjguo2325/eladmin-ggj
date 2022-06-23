package com.ggj;

import com.ggj.annotation.rest.AnonymousGetMapping;
import com.ggj.util.SpringContextHolder;
import io.swagger.annotations.Api;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RestController;

@Api(hidden = true)
@RestController
@SpringBootApplication
public class EladminSystemApplication {

  public static void main(String[] args) {
    SpringApplication.run(EladminSystemApplication.class, args);
  }

  @Bean
  public SpringContextHolder springContextHolder() {
    return new SpringContextHolder();
  }

  @Bean
  public ServletWebServerFactory webServerFactory() {
    TomcatServletWebServerFactory fa = new TomcatServletWebServerFactory();
    fa.addConnectorCustomizers(connector -> connector.setProperty("relaxedQueryChars", "[]{}"));
    return fa;
  }

  /**
   * 访问首页提示
   *
   * @return /
   */
  @AnonymousGetMapping("/")
  public String index() {
    return "Backend service started successfully";
  }

}
