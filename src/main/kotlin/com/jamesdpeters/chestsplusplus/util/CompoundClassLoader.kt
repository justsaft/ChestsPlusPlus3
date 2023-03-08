package com.jamesdpeters.chestsplusplus.util

import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.util.*

class CompoundClassLoader : ClassLoader {

    private var loaders: Collection<ClassLoader>

    /**
     * Constructs a new CompoundClassLoader.
     * @param loaders the loaders to iterate over
     */
    constructor(vararg loaders: ClassLoader) {
        this.loaders = listOf(*loaders)
    }

    /**
     * Constructs a new CompoundClassLoader.
     * @param loaders the loaders to iterate over
     */
    @Suppress("unused")
    constructor(loaders: Collection<ClassLoader>) {
        this.loaders = loaders
    }

    override fun getResource(name: String?): URL? {
        for (loader in loaders) {
            val resource = loader.getResource(name)
            if (resource != null) {
                return resource
            }
        }
        return null
    }

    override fun getResourceAsStream(name: String?): InputStream? {
        for (loader in loaders) {
            val inputStream = loader.getResourceAsStream(name)
            if (inputStream != null) {
                return inputStream
            }
        }
        return null
    }

    @Throws(IOException::class)
    override fun getResources(name: String?): Enumeration<URL>? {
        val urls: MutableList<URL> = ArrayList()
        for (loader in loaders) {
            try {
                val resources = loader.getResources(name)
                while (resources.hasMoreElements()) {
                    val resource = resources.nextElement()
                    if (resource != null && !urls.contains(resource)) {
                        urls.add(resource)
                    }
                }
            } catch (ioe: IOException) {
                // ignoring, but to keep checkstyle happy ("Must have at least one statement."):
                ioe.message
            }
        }
        return Collections.enumeration(urls)
    }

    @Throws(ClassNotFoundException::class)
    override fun loadClass(name: String?): Class<*>? {
        for (loader in loaders) {
            try {
                return loader.loadClass(name)
            } catch (cnfe: ClassNotFoundException) {
                // ignoring, but to keep checkstyle happy ("Must have at least one statement."):
                cnfe.message
            }
        }
        throw ClassNotFoundException()
    }

    @Throws(ClassNotFoundException::class)
    override fun loadClass(name: String?, resolve: Boolean): Class<*>? {
        // loader.loadClass(name, resolve) is not visible!
        return loadClass(name)
    }

    override fun toString(): String {
        return String.format("CompoundClassloader %s", loaders)
    }

}