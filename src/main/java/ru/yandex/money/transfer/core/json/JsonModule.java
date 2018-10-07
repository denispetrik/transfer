package ru.yandex.money.transfer.core.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import ru.yandex.money.transfer.common.domain.Currency;
import ru.yandex.money.transfer.common.domain.MonetaryAmount;
import ru.yandex.money.transfer.transaction.domain.TransactionType;
import ru.yandex.money.transfer.transfer.domain.TransferOperationErrorCode;
import ru.yandex.money.transfer.transfer.domain.TransferOperationState;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

/**
 * @author petrique
 */
public class JsonModule extends SimpleModule {

    public JsonModule() {
        addSerializer(MonetaryAmount.class, JsonModule::serializeMonetaryAmount);
        addDeserializer(MonetaryAmount.class, JsonModule::deserializeMonetaryAmount);
        addSerializer(TransactionType.class, (value, json) -> json.writeString(value.getExternalCode()));
        addSerializer(TransferOperationState.class, (value, json) -> json.writeString(value.getExternalCode()));
        addSerializer(TransferOperationErrorCode.class, (value, json) -> json.writeString(value.getExternalCode()));
    }

    private static void serializeMonetaryAmount(MonetaryAmount value, JsonGenerator json) throws IOException {
        json.writeStartObject();
        json.writeObjectField("value", value.getValue().toPlainString()); //to prevent float/double rounding
        json.writeObjectField("currency", value.getCurrency().getIsoCode());
        json.writeEndObject();
    }

    private static MonetaryAmount deserializeMonetaryAmount(JsonParser json) throws IOException {
        JsonNode node = json.readValueAsTree();
        String value = node.get("value").asText();
        String currency = node.get("currency").asText();
        return MonetaryAmount.of(value, Currency.byIsoCode(currency));
    }

    private <T> void addSerializer(Class<T> typeClass, Serializer<T> serializer) {
        addSerializer(typeClass, new SerializerWrapper<>(typeClass, serializer));
    }

    private <T> void addDeserializer(Class<T> typeClass, Deserializer<T> deserializer) {
        addDeserializer(typeClass, new DeserializerWrapper<>(typeClass, deserializer));
    }


    @FunctionalInterface
    public interface Serializer<T> {
        void serialize(T value, JsonGenerator json) throws IOException;
    }

    @FunctionalInterface
    public interface Deserializer<T> {
        T deserialize(JsonParser json) throws IOException;
    }

    private static class SerializerWrapper<T> extends StdSerializer<T> {

        private final Serializer<T> serializer;

        private SerializerWrapper(Class<T> typeClass, Serializer<T> serializer) {
            super(typeClass);
            this.serializer = requireNonNull(serializer, "serializer is required");
        }

        @Override
        public void serialize(T value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            serializer.serialize(value, gen);
        }
    }

    private static class DeserializerWrapper<T> extends StdDeserializer<T> {

        private final Deserializer<T> deserializer;

        private DeserializerWrapper(Class<T> typeClass, Deserializer<T> deserializer) {
            super(typeClass);
            this.deserializer = requireNonNull(deserializer, "deserializer is required");
        }

        @Override
        public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            return deserializer.deserialize(p);
        }
    }
}
