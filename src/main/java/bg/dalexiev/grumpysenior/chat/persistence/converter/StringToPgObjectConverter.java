package bg.dalexiev.grumpysenior.chat.persistence.converter;

import org.postgresql.util.PGobject;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

import java.sql.SQLException;

@WritingConverter
public class StringToPgObjectConverter implements Converter<String, PGobject> {
    @Override
    public PGobject convert(String source) {
        final PGobject pgObject = new PGobject();
        pgObject.setType("jsonb");
        try {
            pgObject.setValue(source);
        } catch (SQLException e) {
            throw new IllegalArgumentException("Unable to convert String to PGobject JSONB", e);
        }
        return pgObject;
    }
}
