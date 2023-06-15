package com.oneseed.zachet.domain.models

sealed class BackPressedState {
    object Success : BackPressedState()
    object Waiting : BackPressedState()
    object Reset : BackPressedState()
}