package com.atomicanalyst.data.db.mapper

import com.atomicanalyst.data.db.entity.TagEntity
import com.atomicanalyst.domain.model.Tag

fun TagEntity.toDomain(): Tag = Tag(
    id = id,
    name = name,
    createdAtEpochMs = createdAtEpochMs
)

fun Tag.toEntity(): TagEntity = TagEntity(
    id = id,
    name = name,
    createdAtEpochMs = createdAtEpochMs
)
