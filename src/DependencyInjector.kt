package com.gmail.marcosav2010

import com.gmail.marcosav2010.repositories.UserActionRepository
import com.gmail.marcosav2010.services.UserActionService
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.singleton

fun DI.MainBuilder.setupRepositories() {
    bind<UserActionRepository>() with singleton { UserActionRepository(di) }
}

fun DI.MainBuilder.setupServices() {
    bind<UserActionService>() with singleton { UserActionService(di) }
}