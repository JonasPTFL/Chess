/**
 *
 * @author Jonas Pollpeter
 */

fun <T> List<T?>.filterNotNull(predicate: (T) -> Boolean) : List<T> {
    return this.filterNotNull().filter { predicate(it) }
}