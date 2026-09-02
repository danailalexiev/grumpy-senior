package bg.dalexiev.grumpysenior.chat.persistence.converter;

import bg.dalexiev.grumpysenior.chat.persistence.MessageEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

@WritingConverter
public class MessageTypeToStringConverter implements Converter<MessageEntity.Type, String> {
    @Override
    public String convert(MessageEntity.Type source) {
        return source.name();
    }
}
