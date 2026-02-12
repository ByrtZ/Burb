package dev.byrt.burb.util

object ReflectUtil {
    /**
     * Gets the value of a class's static field via reflection.
     */
    inline fun <reified T : Any, R : Any> getStatic(name: String): R {
        val field = T::class.java.getDeclaredField(name)
        field.isAccessible = true
        return field.get(null) as R
    }
}