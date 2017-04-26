package com.battleshippark.bsp_langpod.data.rss;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 */

public class RssConverterFactory extends Converter.Factory {
    private final RssResponseMapper mapper;

    public static Converter.Factory create(RssResponseMapper mapper) {
        return new RssConverterFactory(mapper);
    }

    private RssConverterFactory(RssResponseMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations,
                                                            Retrofit retrofit) {
        return new RssResponseBodyConverter(mapper);
    }
}
