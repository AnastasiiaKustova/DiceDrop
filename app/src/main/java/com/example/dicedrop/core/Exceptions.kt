package com.example.dicedrop.core

class NotEnoughCoinsException(message: String) : RuntimeException(message)

class NotEnoughBonusException(message: String) : RuntimeException(message)

class DailyRewardReceived(message: String) : RuntimeException(message)

class IllegalException(message: String) : RuntimeException(message)