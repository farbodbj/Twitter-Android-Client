package com.twitter.common.Utils;





import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.time.LocalDateTime;

//singleton
public class GsonUtils {
    public final static String STANDARD_DATE_FORMAT = "MMM d, yyyy, hh:mm:ss a";
    private static final Gson instance = createGson();

    private static Gson createGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();

        //TODO: I'm not sure if I have to register the type adapter for all subclasses or just registering it for a parent class will do
        //gsonBuilder.registerTypeAdapter(Quote.class, new AnnotatedDeserializer<Quote>());
        //gsonBuilder.registerTypeAdapter(Mention.class, new AnnotatedDeserializer<Mention>());
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter());
        gsonBuilder.setDateFormat(STANDARD_DATE_FORMAT);

        return gsonBuilder.create();
    }

    public static Gson getInstance() {
        return instance;
    }
}
