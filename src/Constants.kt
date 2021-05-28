package com.gmail.marcosav2010

object Constants {

    /* Environment vars */
    const val JWT_SECRET_ENV = "JWT_SECRET"
    const val ALLOWED_HOSTS_ENV = "ALLOWED_HOSTS"
    const val DATABASE_HOST = "DATABASE_HOST"
    const val DATABASE_NAME = "DATABASE_NAME"
    const val DATABASE_USERNAME = "DATABASE_USERNAME"
    const val DATABASE_PASSWORD = "DATABASE_PASSWORD"

    const val POPULATE = "POPULATE"

    const val CLIENT_FEEDBACK_PORT = "CLIENT_FEEDBACK_PORT"
    const val ENGINE_PORT = "ENGINE_PORT"

    const val DEFAULT_HOST = "localhost"

    const val RECOMMENDER_UPDATE_DELAY = "RECOMMENDER_UPDATE_DELAY"

    const val ENGINE_API_AUTH_PASSWORD = "ENGINE_API_AUTH_PASSWORD"
    const val ENGINE_API_AUTH_USER = "ENGINE_API_AUTH_USER"

    /* Session claims */
    const val USER_ID_CLAIM = "uid"

    /* API Versions */
    const val FEEDBACK_API_VERSION = 1
    const val RECOMMENDER_API_VERSION = 1

    /* Recommender update delay */
    const val DEFAULT_RECOMMENDER_UPDATE_DELAY = 1 * 60 * 60 * 1000L // 1h
}