package com.gmail.marcosav2010.repositories

import com.gmail.marcosav2010.model.PreliminaryEntry
import org.kodein.di.DI
import org.litote.kmongo.*

abstract class PreliminaryEntryRepository<T : PreliminaryEntry>(di: DI) : RepositoryBase<T>(di) {

    fun clean() {
        cleanUnmarked()
        unmarkAll()
    }

    fun cleanMarked() = collection.deleteMany(PreliminaryEntry::pre eq true).deletedCount

    private fun cleanUnmarked() = collection.deleteMany(PreliminaryEntry::pre ne true).deletedCount

    private fun unmarkAll() =
        collection.updateMany(PreliminaryEntry::pre.exists(), unset(PreliminaryEntry::pre)).modifiedCount
}