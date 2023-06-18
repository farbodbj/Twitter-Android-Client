package com.twitter.common.Utils;

import com.google.gson.*;
import com.twitter.common.Annotations.JsonRequired;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AnnotatedDeserializer<T> implements JsonDeserializer<T> {

    public T deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) throws JsonParseException {
        T pojo = new Gson().fromJson(je, type);

        Field[] fields = pojo.getClass().getDeclaredFields();
        for (Field f : fields) {
            if (f.getAnnotation(JsonRequired.class) != null) {
                try {
                    f.setAccessible(true);
                    if (f.get(pojo) == null) {
                        throw new JsonParseException("Missing field in JSON: " + f.getName());
                    }
                } catch (IllegalArgumentException | IllegalAccessException ex) {
                    Logger.getLogger(AnnotatedDeserializer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return pojo;

    }
}
