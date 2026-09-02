package bg.dalexiev.grumpysenior.chat.persistence.converter;

import bg.dalexiev.grumpysenior.chat.persistence.MessageEntity;
import org.postgresql.util.PGobject;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class PgObjectToSerializedPayloadConverter implements Converter<PGobject, MessageEntity.SerializedPayload> {
    @Override
    public MessageEntity.SerializedPayload convert(PGobject source) {
        return new MessageEntity.SerializedPayload(source.getValue());
    }
}
