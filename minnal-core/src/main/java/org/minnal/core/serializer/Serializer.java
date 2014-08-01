/**
 * 
 */
package org.minnal.core.serializer;

import io.netty.buffer.ByteBuf;

import java.util.Collection;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.google.common.net.MediaType;

/**
 * @author ganeshs
 *
 */
@JsonTypeInfo(use=Id.CLASS, include=As.PROPERTY, property="class")
public abstract class Serializer {
	
	private static final Logger logger = LoggerFactory.getLogger(Serializer.class); 
	
	public static final Serializer DEFAULT_JSON_SERIALIZER = new DefaultJsonSerializer();
	
	public static final Serializer DEFAULT_XML_SERIALIZER = new DefaultXmlSerializer();
	
	public static final Serializer DEFAULT_TEXT_SERIALIZER = new DefaultTextSerializer();
	
	public static final Serializer DEFAULT_FORM_SERIALIZER = new DefaultFormSerializer();
	
	public static final Serializer DEFAULT_YAML_SERIALIZER = new DefaultYamlSerializer();
	
	public abstract ByteBuf serialize(Object object);
	
	public ByteBuf serialize(Object object, Set<String> excludes, Set<String> includes) {
		logger.warn("WARNING: {} doesn't support exlcuding and including the fields in the response", this.getClass());
		return serialize(object);
	}
	
	public abstract <T> T deserialize(ByteBuf buffer, Class<T> targetClass);
	
	public abstract <T extends Collection<E>, E> T deserializeCollection(ByteBuf buffer, Class<T> collectionType, Class<E> elementType);
	
	public static Serializer getSerializer(MediaType mediaType) {
		if (mediaType.is(MediaType.JSON_UTF_8)) {
			return DEFAULT_JSON_SERIALIZER;
		}
		if (mediaType.is(MediaType.XML_UTF_8)) {
			return DEFAULT_XML_SERIALIZER;
		}
		if (mediaType.is(MediaType.PLAIN_TEXT_UTF_8)) {
			return DEFAULT_TEXT_SERIALIZER;
		}
		if (mediaType.is(MediaType.FORM_DATA)) {
			return DEFAULT_FORM_SERIALIZER;
		}
		return null;
	}
	
}