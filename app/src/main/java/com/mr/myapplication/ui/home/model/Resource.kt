package com.mr.myapplication.ui.home.model

/**
 * Resource containing state and data for the response state
 */
open class Resource<out T> constructor(val status: ResourceState, val data: T?)