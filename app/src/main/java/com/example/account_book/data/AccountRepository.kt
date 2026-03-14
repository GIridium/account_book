// app/src/main/java/com/example/account_book/data/AccountRepository.kt
package com.example.account_book.data

import com.example.account_book.model.Account
import java.util.UUID

object AccountRepository {
    private val accounts = mutableListOf<Account>(
        Account(
            id = "default",
            name = "默认账本",
            colorValue = 0xFF5B9BD5,
            isDefault = true
        )
    )
    private var currentAccountId = "default"

    fun getAllAccounts(): List<Account> = accounts.toList()

    fun getCurrentAccount(): Account {
        return accounts.find { it.id == currentAccountId } ?: accounts.first()
    }

    fun setCurrentAccount(accountId: String) {
        if (accounts.any { it.id == accountId }) {
            currentAccountId = accountId
        }
    }

    fun addAccount(name: String, colorValue: Long): Account {
        val newAccount = Account(
            id = UUID.randomUUID().toString(),
            name = name,
            colorValue = colorValue,
            isDefault = false
        )
        accounts.add(newAccount)
        return newAccount
    }

    fun updateAccount(id: String, name: String, colorValue: Long): Boolean {
        val index = accounts.indexOfFirst { it.id == id }
        if (index != -1 && !accounts[index].isDefault) {
            accounts[index] = accounts[index].copy(name = name, colorValue = colorValue)
            return true
        }
        return false
    }

    fun deleteAccount(id: String): Boolean {
        // 不能删除默认账本
        if (accounts.find { it.id == id }?.isDefault == true) {
            return false
        }
        val removed = accounts.removeIf { it.id == id }
        // 如果删除的是当前账本，切换到默认账本
        if (removed && currentAccountId == id) {
            currentAccountId = "default"
        }
        return removed
    }

    fun getAccountById(id: String): Account? = accounts.find { it.id == id }
}