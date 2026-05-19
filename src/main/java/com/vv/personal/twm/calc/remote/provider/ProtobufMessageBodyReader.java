package com.vv.personal.twm.calc.remote.provider;

import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

@Provider
@Consumes(MediaType.APPLICATION_JSON)
public class ProtobufMessageBodyReader implements MessageBodyReader<Message> {

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return Message.class.isAssignableFrom(type);
    }

    @Override
    public Message readFrom(Class<Message> type, Type genericType, Annotation[] annotations, MediaType mediaType,
                            MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
            throws IOException, WebApplicationException {

        try (InputStreamReader reader = new InputStreamReader(entityStream, StandardCharsets.UTF_8)) {
            // Need to get the newBuilder() method from the exact Protobuf class
            Method newBuilderMethod = type.getMethod("newBuilder");
            Message.Builder builder = (Message.Builder) newBuilderMethod.invoke(null);
            
            JsonFormat.parser().ignoringUnknownFields().merge(reader, builder);
            return builder.build();
        } catch (Exception e) {
            throw new WebApplicationException("Failed to read Protobuf JSON", e);
        }
    }
}
