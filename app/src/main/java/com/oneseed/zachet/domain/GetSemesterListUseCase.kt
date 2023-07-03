package com.oneseed.zachet.domain

import com.oneseed.zachet.domain.repository.Repository

class GetSemesterListUseCase(
    private val repository: Repository.GetSemesterList,
) {

    fun invoke(
        getSemesterListCallback: (result: Array<String>?) -> Unit
    ) {
        repository.getSemesterList(getSemesterListCallback)
    }

}