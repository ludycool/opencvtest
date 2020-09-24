package com.topband.opencvtest.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableSwagger2
@Profile({"dev","test"})//开发，和测试环境 开启，生产环境关闭
public class Swagger2 {

    //访问地址 http://localhost:8082/swagger-ui.html
    //出现Fetch error 浏览器兼容问题，换了chrome 浏览器就可以了

   // public static final String SWAGGER_SCAN_ADMIN_PACKAGE = "com.zhicheng.iot.web.controller.api";
    //public static final String ADMIN_VERSION = "1.0.0";
    public static final String SWAGGER_SCAN_APP_PACKAGE = "com.topband.opencvtest.controller";
    public static final String APP_VERSION = "1.0.0";

    /*
    @Bean
    public Docket createAdminRestApi() {
        ParameterBuilder ticketPar = new ParameterBuilder();
        List<Parameter> pars = new ArrayList<Parameter>();
        ticketPar.name(Constant.HTTP_HEADER_TOKEN_KEY).description("user ticket")
                  .modelRef(new ModelRef("string")).parameterType("header")
                  .required(false).build(); //header中的ticket参数非必填，传空也可以
        pars.add(ticketPar.build());    //根据每个方法名也知道当前方法在设置什么参数
        return new Docket(DocumentationType.SWAGGER_2)
                  .groupName("后台管理接口")
                  .apiInfo(apiAdminInfo())
                  .select()
                  .apis(RequestHandlerSelectors.basePackage(SWAGGER_SCAN_ADMIN_PACKAGE))//api接口包扫描路径
                  .paths(PathSelectors.any())
                  .build().globalOperationParameters(pars);
    }

    private ApiInfo apiAdminInfo() {
        return new ApiInfoBuilder()
                  .title("后台管理接口")//设置文档的标题
                  .description("后台数据管理")//设置文档的描述->1.Overview
                  .version(ADMIN_VERSION)//设置文档的版本信息-> 1.1 Version information
                  .build();
    }
*/
    @Bean
    public Docket createWxRestApi() {
        ParameterBuilder ticketPar = new ParameterBuilder();
        List<Parameter> pars = new ArrayList<Parameter>();
        ticketPar.name("Token").description("user ticket")
                  .modelRef(new ModelRef("string")).parameterType("header")
                  .required(false).build(); //header中的ticket参数非必填，传空也可以
        pars.add(ticketPar.build());    //根据每个方法名也知道当前方法在设置什么参数

        return new Docket(DocumentationType.SWAGGER_2)
                  .groupName("app接口")
                  .apiInfo(apiWxInfo())
                  .select()
                  .apis(RequestHandlerSelectors.basePackage(SWAGGER_SCAN_APP_PACKAGE))//api接口包扫描路径
                  .paths(PathSelectors.any())
                  .build().globalOperationParameters(pars);

    }

    private ApiInfo apiWxInfo() {
        return new ApiInfoBuilder()
                  .title("app接口")//设置文档的标题
                  .description("app开发接口实现的文档")//设置文档的描述->1.Overview
                  .version(APP_VERSION)//设置文档的版本信息-> 1.1 Version information
                  .build();
    }

}