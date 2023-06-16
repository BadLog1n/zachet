package com.oneseed.zachet.domain.states

sealed class BackPressedState {
    object Success : BackPressedState()
    object Waiting : BackPressedState()
    object Reset : BackPressedState()
}