package gg.rohan.narwhal.util.gson;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.Field;

public class EnumAdapterFactory implements TypeAdapterFactory {

    @Override
    public <T> TypeAdapter<T> create(final Gson gson, final TypeToken<T> type) {
        Class<? super T> rawType = type.getRawType();
        if (rawType.isEnum()) {
            return new EnumTypeAdapter<T>();
        }
        return null;
    }

    public class EnumTypeAdapter<T> extends TypeAdapter<T> {

        public void write(JsonWriter out, T value) throws IOException {
            if (value == null) {
                out.nullValue();
                return;
            }
            Enum<?> realEnums = Enum.valueOf(value.getClass().asSubclass(Enum.class), value.toString());
            Field[] enumFields = realEnums.getClass().getDeclaredFields();
            out.beginObject();
            out.name("name");
            out.value(realEnums.name());
            for (Field enumField : enumFields) {
                if (enumField.isEnumConstant() || enumField.getName().equals("$VALUES")) {
                    continue;
                }
                enumField.setAccessible(true);
                try {
                    out.name(enumField.getName());
                    out.value(enumField.get(realEnums).toString());
                } catch (Throwable th) {
                    out.value("");
                }
            }
            out.endObject();
        }

        public T read(JsonReader in) throws IOException {
            return null;
        }
    }
}