package ca.josephroque.bowlingcompanion.core.common.utils

fun <K, V> mapOfNullableValues(vararg pairs: Pair<K, V?>): Map<K, V> =
	pairs.mapNotNull { (k, v) ->
		if (v != null) k to v else null
	}.toMap()

fun <K, V> mutableMapOfNullableValues(vararg pairs: Pair<K, V?>): MutableMap<K, V> =
	pairs.mapNotNull { (k, v) ->
		if (v != null) k to v else null
	}.toMap(mutableMapOf())