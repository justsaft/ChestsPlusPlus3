package com.jamesdpeters.chestsplusplus.serialize

import com.jamesdpeters.chestsplusplus.ChestsPlusPlus
import com.jamesdpeters.chestsplusplus.serialize.serializers.DefaultSerializer
import com.jamesdpeters.chestsplusplus.storage.serializable.SerializableObject
import org.springframework.beans.factory.NoSuchBeanDefinitionException
import org.springframework.context.ApplicationContext
import org.springframework.core.GenericTypeResolver
import org.springframework.core.ResolvableType
import org.springframework.stereotype.Component
import java.lang.reflect.Field
import java.util.*
import kotlin.reflect.jvm.kotlinProperty

@Component
class SerializeFieldCache(private val context: ApplicationContext) {

    private val log = ChestsPlusPlus.logger()

    private val cache = HashMap<Class<out SerializableObject?>, Map<String, Field>>()
    private var fieldSerializers = HashMap<Field, CppSerializer<*, *>>()

    init {
        log.info {"Initialising SerializeFieldCache" }

        SerializableObject::class.sealedSubclasses.forEach {
            @Suppress("UNCHECKED_CAST")
            register(it.java)
        }
    }

    private fun register(clazz: Class<out SerializableObject>) {
        if (cache.containsKey(clazz))
            throw RuntimeException("Class $clazz already registered")

        val map = HashMap<String, Field>()

        for (declaredField in clazz.declaredFields) {
            if (declaredField.isAnnotationPresent(ConfigSerialize::class.java)) {
                val annotation = declaredField.getAnnotation(ConfigSerialize::class.java)

                // ? Store serializer if provided
                val serializer = annotation.value
                if (serializer != DefaultSerializer::class) {
                    val instance: CppSerializer<*, *> = getBean(serializer.java, declaredField)
                    validateSerializer(declaredField, instance)
                    fieldSerializers[declaredField] = instance
                }

                declaredField.trySetAccessible()
                map[declaredField.name] = declaredField
            }
        }

        cache[clazz] = map
        log.info {"Registered class ${clazz.canonicalName} for serialization" }
    }

    fun serialize(obj: SerializableObject): MutableMap<String?, Any?> {
        val returnMap = HashMap<String?, Any?>()
        val map = cache[obj::class.java]
        map?.forEach { (name: String, field: Field) ->
            try {
                var value = field[obj]
                val serializer = fieldSerializers[field] as CppSerializer<Any?, Any?>?
                if (serializer != null) {
                    value = serializer.serialize(value)
                }
                if (value != null) returnMap[name] = value
            } catch (e: IllegalAccessException) {
                throw RuntimeException(e)
            }
        }
        return returnMap
    }

    fun deserialize(obj: SerializableObject, mapToDeserialize: MutableMap<String?, Any?>) {
        val map = cache[obj::class.java]
        map?.forEach { (name: String?, field: Field) ->
            var value: Any? = mapToDeserialize[name]
            try {
                @Suppress("UNCHECKED_CAST")
                val serializer = fieldSerializers[field] as CppSerializer<Any, Any?>?
                if (serializer != null) {
                    value = serializer.deserialize(value)
                }
                if (value == null || field.type.isAssignableFrom(value.javaClass))
                    field[obj] = value

            } catch (e: IllegalAccessException) {
                throw RuntimeException(e)
            }
        }
    }

    private fun validateSerializer(field: Field, serializer: CppSerializer<*, *>) {
        val type = GenericTypeResolver.resolveTypeArguments(
            serializer.javaClass,
            CppSerializer::class.java
        )
        if (type == null || type[0] == null || !type[0]!!.isAssignableFrom(field.type)) {
            val typeName = if (type != null) if (type[0] != null) type[0]!!.canonicalName else null else null
            throw RuntimeException("Serializer for field " + field + " is not valid. Expected type: " + field.type + " but got " + typeName)
        }
    }

    private fun <T> getBean(beanClass: Class<T>, field: Field): T {
        return try {
            context.getBean(beanClass)
        } catch (e: NoSuchBeanDefinitionException) {
            // If there is no bean found try and find a generic type instead.
            getGenericBean(beanClass, field.type)
        }
    }

    private fun <T, C> getGenericBean(parent: Class<T>, child: Class<C>): T {
        val resolvableType = ResolvableType.forClassWithGenerics(parent, child)
        val beanNames = context.getBeanNamesForType(resolvableType)
        return if (beanNames.isNotEmpty()) {
            val beanName = beanNames[0]
            context.getBean(beanName, parent)
        } else {
            val constructor = parent.getDeclaredConstructor(child.javaClass)
            val instance = constructor.newInstance(child)
            context.autowireCapableBeanFactory.initializeBean(instance, parent.name + "<" + child.name + ">")
            instance
        }
    }
}