package com.joseph.foamadminjava.generator;

import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.Scanner;

/**
 * 执行 main 方法控制台输入表名回车自动生成对应项目目录中
 * 参考官方文档：https://github.com/baomidou/generator
 * @author Joseph.Liu
 */
public class CodeGenerator {

    /**
     * <p>
     * 读取控制台内容
     * </p>
     */
    public static String scanner(String tip) {
        Scanner scanner = new Scanner(System.in);
        StringBuilder help = new StringBuilder();
        help.append("请输入" + tip + "：");
        System.out.println(help.toString());
        if (scanner.hasNext()) {
            String ipt = scanner.next();
            if (StringUtils.isNotBlank(ipt)) {
                return ipt;
            }
        }
        throw new MybatisPlusException("请输入正确的" + tip + "！");
    }

    public static void main(String[] args) {
        // 数据源配置
        DataSourceConfig dsc = new DataSourceConfig
                .Builder("jdbc:mysql://10.1.1.73:3306/taobao_admin?useUnicode=true&useSSL=false&characterEncoding=utf8&serverTimezone=Australia/Sydney","fluxadmin","8888")
                .build();
        // 代码生成器
        AutoGenerator mpg = new AutoGenerator(dsc);

        // 全局配置
        String projectPath = System.getProperty("user.dir");
        GlobalConfig gc = new GlobalConfig
                .Builder()
                .outputDir(projectPath + "/src/main/java")
                .author("Joseph.Liu")
                .openDir(false)
                .build();

        // 包配置
        PackageConfig pc = new PackageConfig.Builder()
                .parent("com.joseph.foamadminjava")
//                .moduleName(scanner("模块名"))
                .build();

        // 配置模板 : 激活所有默认模板
        TemplateConfig templateConfig = new TemplateConfig.Builder().build();

        // 策略配置
        StrategyConfig strategy = new StrategyConfig.Builder()
                .addInclude(scanner("表名，多个英文逗号分割").split(","))
                .entityBuilder()
                .superClass("BaseEntity")
                .addSuperEntityColumns("id", "created", "updated", "statu")
                .naming(NamingStrategy.underline_to_camel)
                .columnNaming(NamingStrategy.underline_to_camel)
                .enableLombok()
                .controllerBuilder()
                .superClass("BaseController")
                .enableRestStyle()
                .enableHyphenStyle()
                .build();
        mpg.global(gc);
        mpg.packageInfo(pc);
        mpg.template(templateConfig);
        mpg.strategy(strategy);
        mpg.execute(new FreemarkerTemplateEngine());
    }

}
