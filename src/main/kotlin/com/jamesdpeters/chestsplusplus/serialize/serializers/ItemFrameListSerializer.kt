package com.jamesdpeters.chestsplusplus.serialize.serializers

import com.jamesdpeters.chestsplusplus.serialize.serializers.abstracts.EntityListSerializer
import org.bukkit.entity.ItemFrame
import org.springframework.stereotype.Component

@Component
class ItemFrameListSerializer : EntityListSerializer<ItemFrame>(ItemFrame::class) {}