package kr.co.mathrank.common.lazyconnection;

import javax.sql.DataSource;
import javax.xml.crypto.Data;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
class LazyConnectionDataSourceProxyConfiguration {
	@Bean
	HikariDataSource hikariDataSource(final DataSourceProperties dataSourceProperties) {
		return dataSourceProperties.initializeDataSourceBuilder()
			.type(HikariDataSource.class)
			.build();
	}

	@Bean
	@Primary
	DataSource lazyConnectionDataSource(final HikariDataSource hikariDataSource) {
		return new LazyConnectionDataSourceProxy(hikariDataSource);
	}
}
