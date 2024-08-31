package gg.rohan.narwhal.util.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

public class TemporalSerializer implements JsonSerializer<TemporalAccessor>, JsonDeserializer<TemporalAccessor> {


    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    private static TemporalAccessor fromString(String string) throws ParseException {
        return FORMATTER.parse(string);
    }

    @Override
    public TemporalAccessor deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try {
            return fromString(json.getAsString());
        } catch (ParseException e) {
            throw new JsonParseException(e);
        }
    }

    @Override
    public JsonElement serialize(TemporalAccessor src, Type typeOfSrc, JsonSerializationContext context) {
        return context.serialize(FORMATTER.format(src));
    }
}
