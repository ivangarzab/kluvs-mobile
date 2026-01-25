package com.ivangarzab.kluvs.database.entities

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class MemberEntityTest {

    @Test
    fun testMemberEntity_creation() {
        // Given
        val memberEntity = MemberEntity(
            id = "member-1",
            userId = "user-1",
            name = "John Doe",
            handle = "@johndoe",
            avatarPath = "/avatars/johndoe.png",
            points = 100,
            booksRead = 5,
            role = "admin",
            lastFetchedAt = 1234567890L
        )

        // Then
        assertEquals("member-1", memberEntity.id)
        assertEquals("user-1", memberEntity.userId)
        assertEquals("John Doe", memberEntity.name)
        assertEquals("@johndoe", memberEntity.handle)
        assertEquals("/avatars/johndoe.png", memberEntity.avatarPath)
        assertEquals(100, memberEntity.points)
        assertEquals(5, memberEntity.booksRead)
        assertEquals("admin", memberEntity.role)
        assertEquals(1234567890L, memberEntity.lastFetchedAt)
    }

    @Test
    fun testMemberEntity_withNullFields() {
        // Given
        val memberEntity = MemberEntity(
            id = "member-1",
            userId = null,
            name = null,
            handle = null,
            avatarPath = null,
            points = 0,
            booksRead = 0,
            role = null,
            lastFetchedAt = 1234567890L
        )

        // Then
        assertEquals("member-1", memberEntity.id)
        assertEquals(null, memberEntity.userId)
        assertEquals(null, memberEntity.name)
        assertEquals(null, memberEntity.handle)
        assertEquals(null, memberEntity.avatarPath)
        assertEquals(null, memberEntity.role)
        assertEquals(0, memberEntity.points)
        assertEquals(0, memberEntity.booksRead)
    }

    @Test
    fun testMemberEntity_copy() {
        // Given
        val original = MemberEntity(
            id = "member-1",
            userId = "user-1",
            name = "John Doe",
            handle = "@johndoe",
            avatarPath = "/avatars/johndoe.png",
            points = 100,
            booksRead = 5,
            role = "member",
            lastFetchedAt = 1234567890L
        )

        // When
        val updated = original.copy(
            points = 150,
            booksRead = 6,
            lastFetchedAt = 9876543210L
        )

        // Then
        assertEquals("member-1", updated.id)
        assertEquals(150, updated.points)
        assertEquals(6, updated.booksRead)
        assertEquals(9876543210L, updated.lastFetchedAt)
    }

    @Test
    fun testMemberEntity_equality() {
        // Given
        val member1 = MemberEntity(
            id = "member-1",
            userId = "user-1",
            name = "John Doe",
            handle = "@johndoe",
            avatarPath = "/avatars/johndoe.png",
            points = 100,
            booksRead = 5,
            role = "member",
            lastFetchedAt = 1234567890L
        )

        val member2 = MemberEntity(
            id = "member-1",
            userId = "user-1",
            name = "John Doe",
            handle = "@johndoe",
            avatarPath = "/avatars/johndoe.png",
            points = 100,
            booksRead = 5,
            role = "member",
            lastFetchedAt = 1234567890L
        )

        val member3 = MemberEntity(
            id = "member-2",
            userId = "user-2",
            name = "Jane Doe",
            handle = "@janedoe",
            avatarPath = "/avatars/janedoe.png",
            points = 50,
            booksRead = 3,
            role = "member",
            lastFetchedAt = 1234567890L
        )

        // Then
        assertEquals(member1, member2)
        assertNotEquals(member1, member3)
    }
}
