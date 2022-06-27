package com.ggj;

import com.ggj.annotation.rest.AnonymousGetMapping;
import com.ggj.utils.SpringContextHolder;
import io.swagger.annotations.Api;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.bind.annotation.RestController;

@Api(hidden = true)
@RestController
@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
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
