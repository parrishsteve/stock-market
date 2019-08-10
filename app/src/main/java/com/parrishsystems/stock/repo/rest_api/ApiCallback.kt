package com.parrishsystems.stock.repo.rest_api

interface ApiCallback<T> {
    fun onComplete(desc: String, resp: T)
    fun onError(errMsg: String)
}