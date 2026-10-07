package com.hsfg.collector.core.utils.exception

class IdCollisionException(id: String) : Exception("An entity with ID '$id' already exists.")