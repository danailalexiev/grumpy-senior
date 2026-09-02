package bg.dalexiev.grumpysenior.chat.persistence.converter;

import bg.dalexiev.grumpysenior.chat.persistence.MessageEntity;
import org.postgresql.util.PGobject;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.jdbc.core.mapping.JdbcValue;

import java.sql.JDBCType;
import java.sql.SQLException;

@WritingConverter
public class SerializedPayloadToPgObjectConverter implements Converter<MessageEntity.SerializedPayload, JdbcValue> {

    @Override
    public JdbcValue convert(MessageEntity.SerializedPayload source) {
        final PGobject pgObject = new PGobject();
        pgObject.setType("jsonb");
        try {
            pgObject.setValue(source.value());
        } catch (SQLException e) {
            throw new IllegalArgumentException("Unable to convert String to PGobject JSONB", e);
        }
        return JdbcValue.of(pgObject, JDBCType.OTHER);
    }
}
