package com.jamesdpeters.chestsplusplus.serialize;

import com.jamesdpeters.chestsplusplus.serialize.serializers.DefaultSerializer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigSerialize {

	Class<? extends CppSerializer> value() default DefaultSerializer.class;

}
