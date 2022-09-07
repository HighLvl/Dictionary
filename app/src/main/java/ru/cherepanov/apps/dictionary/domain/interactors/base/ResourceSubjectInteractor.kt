package ru.cherepanov.apps.dictionary.domain.interactors.base

import io.reactivex.Observable
import ru.cherepanov.apps.dictionary.domain.model.Resource


abstract class ResourceSubjectInteractor<T : Any, R : Any> :
    SubjectInteractor<T, R, Resource<R>>() {

    override fun Observable<R>.transformObservable(): Observable<Resource<R>> {
        return map { data -> Resource.success(data) }
            .onErrorReturn { throwable -> Resource.error(throwable) }
            .startWith(Resource.loading())
    }
}