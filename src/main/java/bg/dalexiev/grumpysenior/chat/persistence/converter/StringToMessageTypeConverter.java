package bg.dalexiev.grumpysenior.chat.persistence.converter;

import bg.dalexiev.grumpysenior.chat.persistence.MessageEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class StringToMessageTypeConverter implements Converter<String, MessageEntity.Type> {
    @Override
    public MessageEntity.Type convert(String source) {
        return MessageEntity.Type.valueOf(source);
    }
}
