package com.oneseed.zachet.domain

import com.oneseed.zachet.domain.repository.Repository

class ChangeGradesListUseCase(private val repository: Repository.ChangeGradList) {
    fun invoke(currentSemester: String) {
        repository.changeGradList(currentSemester)
    }
}