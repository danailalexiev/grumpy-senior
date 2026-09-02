package bg.dalexiev.grumpysenior.config;

import bg.dalexiev.grumpysenior.chat.persistence.converter.MessageTypeToStringConverter;
import bg.dalexiev.grumpysenior.chat.persistence.converter.PgObjectToSerializedPayloadConverter;
import bg.dalexiev.grumpysenior.chat.persistence.converter.StringToMessageTypeConverter;
import bg.dalexiev.grumpysenior.chat.persistence.converter.SerializedPayloadToPgObjectConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionManager;

import javax.sql.DataSource;
import java.util.List;

@Configuration
@EnableJdbcRepositories(basePackages = {"bg.dalexiev.grumpysenior.chat.persistence", "bg.dalexiev.grumpysenior.user.persistence"})
public class PersistenceConfig extends AbstractJdbcConfiguration {

    @SuppressWarnings("NullableProblems")
    @Override
    protected List<?> userConverters() {
        return List.of(new SerializedPayloadToPgObjectConverter(), new PgObjectToSerializedPayloadConverter(), new MessageTypeToStringConverter(), new StringToMessageTypeConverter());
    }

    @Bean
    public NamedParameterJdbcOperations namedParameterJdbcOperations(DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    @Bean
    public TransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
