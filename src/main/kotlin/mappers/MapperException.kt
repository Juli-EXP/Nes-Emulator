package mappers

import kotlin.Exception

class MapperException(msg: String = "No mapper was found") : Exception(msg)