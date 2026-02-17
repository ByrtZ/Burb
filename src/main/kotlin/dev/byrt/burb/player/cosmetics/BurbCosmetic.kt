package dev.byrt.burb.player.cosmetics

import dev.byrt.burb.item.rarity.ItemRarity
import dev.byrt.burb.item.type.ItemType
import dev.byrt.burb.text.Formatting
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TranslatableComponent

enum class BurbCosmetic(val cosmeticName: String, val cosmeticId: String, val cosmeticLore: Component, val cosmeticObtainment: TranslatableComponent, val cosmeticType: ItemType, val cosmeticModel: String, val cosmeticRarity: ItemRarity, val isColorable: Boolean = false, val isHidden: Boolean = false) {
    INVALID_COSMETIC("Invalid", "burb.cosmetic.invalid", Formatting.allTags.deserialize("<i><gray>How did you get this?"), BurbCosmeticObtainmentSource.ERROR.obtainmentSourceComponent, ItemType.ACCESSORY, "barrier", ItemRarity.SPECIAL),
    // Common
    HAT_GRAVESTONE("Gravestone", "burb.cosmetic.hat.gravestone", Formatting.allTags.deserialize("<i><gray>You are die."), BurbCosmeticObtainmentSource.STICKER_SHOP.obtainmentSourceComponent, ItemType.HAT, "gravestone_point", ItemRarity.COMMON),
    HAT_CONDIMENTS("Condiments", "burb.cosmetic.hat.condiments", Formatting.allTags.deserialize("<i><gray>Pick your favourite!"), BurbCosmeticObtainmentSource.NPC_SHOP.obtainmentSourceComponent, ItemType.HAT, "condiments", ItemRarity.COMMON),
    HAT_MAIL_BOX("Mail Box", "burb.cosmetic.hat.mail_box", Formatting.allTags.deserialize("<i><gray>You've got mail."), BurbCosmeticObtainmentSource.STICKER_SHOP.obtainmentSourceComponent, ItemType.HAT, "mail_box", ItemRarity.COMMON),
    HAT_SOLDIER_HELMET("Soldier Helmet", "burb.cosmetic.hat.soldier_helmet", Formatting.allTags.deserialize("<i><gray>Safety first!"), BurbCosmeticObtainmentSource.STICKER_SHOP.obtainmentSourceComponent, ItemType.HAT, "soldier_helmet", ItemRarity.COMMON),
    HAT_CONE("Traffic Cone", "burb.cosmetic.hat.cone", Formatting.allTags.deserialize("<i><gray>Stay back."), BurbCosmeticObtainmentSource.STICKER_SHOP.obtainmentSourceComponent, ItemType.HAT, "cone", ItemRarity.COMMON),
    // Uncommon
    HAT_PLUSH_ALYSSA("Plushie (Alyssa)", "burb.cosmetic.hat.plushie_alyssa", Formatting.allTags.deserialize("<i><gray>Help! I'm a marketable plushie."), BurbCosmeticObtainmentSource.STICKER_SHOP.obtainmentSourceComponent, ItemType.HAT, "alyssa", ItemRarity.UNCOMMON),
    HAT_PLUSH_AUSTIN("Plushie (Austin)", "burb.cosmetic.hat.plushie_austin", Formatting.allTags.deserialize("<i><gray>Help! I'm a marketable plushie."), BurbCosmeticObtainmentSource.STICKER_SHOP.obtainmentSourceComponent, ItemType.HAT, "austin", ItemRarity.UNCOMMON),
    HAT_PLUSH_BYRT("Plushie (Byrt)", "burb.cosmetic.hat.plushie_byrt", Formatting.allTags.deserialize("<i><gray>Help! I'm a marketable plushie."), BurbCosmeticObtainmentSource.STICKER_SHOP.obtainmentSourceComponent, ItemType.HAT, "byrt", ItemRarity.UNCOMMON),
    HAT_PLUSH_FLAMEY("Plushie (Flamey)", "burb.cosmetic.hat.plushie_flamey", Formatting.allTags.deserialize("<i><gray>Help! I'm a marketable plushie."), BurbCosmeticObtainmentSource.STICKER_SHOP.obtainmentSourceComponent, ItemType.HAT, "flamey", ItemRarity.UNCOMMON),
    HAT_PLUSH_MASKY("Plushie (Masky)", "burb.cosmetic.hat.plushie_masky", Formatting.allTags.deserialize("<i><gray>Help! I'm a marketable plushie."), BurbCosmeticObtainmentSource.STICKER_SHOP.obtainmentSourceComponent, ItemType.HAT, "masky", ItemRarity.UNCOMMON),
    HAT_PLUSH_LUCY("Plushie (Lucy)", "burb.cosmetic.hat.plushie_lucy", Formatting.allTags.deserialize("<i><gray>Help! I'm a marketable plushie."), BurbCosmeticObtainmentSource.STICKER_SHOP.obtainmentSourceComponent, ItemType.HAT, "lucy", ItemRarity.UNCOMMON),
    HAT_PLUSH_OBBISEUS("Plushie (Obbiseus)", "burb.cosmetic.hat.plushie_obbiseus", Formatting.allTags.deserialize("<i><gray>Help! I'm a marketable plushie."), BurbCosmeticObtainmentSource.STICKER_SHOP.obtainmentSourceComponent, ItemType.HAT, "obbi", ItemRarity.UNCOMMON),
    HAT_MOUSTACHE("Stylish Stache", "burb.cosmetic.hat.moustache", Formatting.allTags.deserialize("<i><gray>How sophisticated of you!"), BurbCosmeticObtainmentSource.STICKER_SHOP.obtainmentSourceComponent, ItemType.HAT, "moustache", ItemRarity.UNCOMMON),
    HAT_TOP_HAT("Top Hat", "burb.cosmetic.hat.top_hat", Formatting.allTags.deserialize("<i><gray>*tips top hat*"), BurbCosmeticObtainmentSource.STICKER_SHOP.obtainmentSourceComponent, ItemType.HAT, "tophat", ItemRarity.UNCOMMON),
    // Rare
    HAT_EMOJI_EXPLODE("Emoji (Explode)", "burb.cosmetic.hat.emoji_explode", Formatting.allTags.deserialize("<i><gray>MIND. BLOWN."), BurbCosmeticObtainmentSource.FOOTBALL_GAME.obtainmentSourceComponent, ItemType.HAT, "explode_emoji", ItemRarity.RARE),
    HAT_EMOJI_FLUSHED("Emoji (Flushed)", "burb.cosmetic.hat.emoji_flushed", Formatting.allTags.deserialize("<i><gray>:point_right: :point_left:"), BurbCosmeticObtainmentSource.FOOTBALL_GAME.obtainmentSourceComponent, ItemType.HAT, "flooshed_emoji", ItemRarity.RARE),
    HAT_EMOJI_NERD("Emoji (Nerd)", "burb.cosmetic.hat.emoji_nerd", Formatting.allTags.deserialize("<i><gray>Erm, actually..."), BurbCosmeticObtainmentSource.FOOTBALL_GAME.obtainmentSourceComponent, ItemType.HAT, "nerd_emoji", ItemRarity.RARE),
    HAT_EMOJI_THINKING("Emoji (Thinking)", "burb.cosmetic.hat.emoji_thinking", Formatting.allTags.deserialize("<i><gray>Let me get back to you on that."), BurbCosmeticObtainmentSource.FOOTBALL_GAME.obtainmentSourceComponent, ItemType.HAT, "thinking_emoji", ItemRarity.RARE),
    HAT_DEMON_HORNS("Demon Horns", "burb.cosmetic.hat.demon_horns", Formatting.allTags.deserialize("<i><gray>You can call me... <red>Mephisto<gray>."), BurbCosmeticObtainmentSource.STICKER_SHOP.obtainmentSourceComponent, ItemType.HAT, "demon_horns", ItemRarity.RARE),
    HAT_HEISENBERG("Heisenberg", "burb.cosmetic.hat.heisenberg", Formatting.allTags.deserialize("<i><gray>I am the one who Burbs."), BurbCosmeticObtainmentSource.STICKER_SHOP.obtainmentSourceComponent, ItemType.HAT, "heisenberg_hat", ItemRarity.RARE),
    HAT_AVALON("Goggles of Science", "burb.cosmetic.hat.avalon", Formatting.allTags.deserialize("<i><gray>One man, devilishly handsome, impossibly statuesque... The only hope."), BurbCosmeticObtainmentSource.STICKER_SHOP.obtainmentSourceComponent, ItemType.HAT, "avalon_headpiece", ItemRarity.RARE),
    HAT_TURTLE("Timmy", "burb.cosmetic.hat.turtle", Formatting.allTags.deserialize("<i><gray>Save the turtles!"), BurbCosmeticObtainmentSource.STICKER_SHOP.obtainmentSourceComponent, ItemType.HAT, "turtle_hat", ItemRarity.RARE),
    // Epic
    HAT_TURTLE_SURFING("Timmy (Surfer)", "burb.cosmetic.hat.turtle.surfing", Formatting.allTags.deserialize("<i><gray>Catch a wave."), BurbCosmeticObtainmentSource.STICKER_SHOP.obtainmentSourceComponent, ItemType.HAT, "turtle_hat_surfing", ItemRarity.EPIC),
    HAT_TURTLE_WINGED("Timmy (Wingman)", "burb.cosmetic.hat.turtle.winged", Formatting.allTags.deserialize("<i><gray>Feeling the breeze on its shell."), BurbCosmeticObtainmentSource.STICKER_SHOP.obtainmentSourceComponent, ItemType.HAT, "turtle_hat_wingman", ItemRarity.EPIC),
    HAT_POTATO_MINE("Potato Mine", "burb.cosmetic.hat.potato_mine", Formatting.allTags.deserialize("<i><gray>SPUD-OW!"), BurbCosmeticObtainmentSource.STICKER_SHOP.obtainmentSourceComponent, ItemType.HAT, "potato_mine", ItemRarity.EPIC),
    HAT_TRAFFIC_LIGHT("Traffic Light", "burb.cosmetic.hat.traffic_light", Formatting.allTags.deserialize("<i><gray>Don't run it, when it's red."), BurbCosmeticObtainmentSource.STICKER_SHOP.obtainmentSourceComponent, ItemType.HAT, "traffic_light", ItemRarity.EPIC),
    ACCESSORY_CHILLI_BEAN("Chilli Bean", "burb.cosmetic.accessory.chilli_bean", Formatting.allTags.deserialize("<i><gray>UNDERLAY!"), BurbCosmeticObtainmentSource.STICKER_SHOP.obtainmentSourceComponent, ItemType.ACCESSORY, "chilli_bean_bomb", ItemRarity.EPIC),
    HAT_FOOTBALL_HELMET("Football Helmet", "burb.cosmetic.hat.football_helmet", Formatting.allTags.deserialize("<i><gray>GAME ON!"), BurbCosmeticObtainmentSource.STICKER_SHOP.obtainmentSourceComponent, ItemType.HAT, "football_helmet", ItemRarity.EPIC),
    ACCESSORY_GOO_BACKPACK("Goo Backpack", "burb.cosmetic.accessory.goo_backpack", Formatting.allTags.deserialize("<i><gray>You can never have enough goo."), BurbCosmeticObtainmentSource.STICKER_SHOP.obtainmentSourceComponent, ItemType.ACCESSORY, "goo_backpack", ItemRarity.EPIC),
    // Legendary
    HAT_PENNY_RV("Penny", "burb.cosmetic.hat.penny_rv", Formatting.allTags.deserialize("<i><gray>Crazy Dave's best friend."), BurbCosmeticObtainmentSource.STICKER_SHOP.obtainmentSourceComponent, ItemType.HAT, "penny_rv", ItemRarity.LEGENDARY),
    HAT_ROCKET_SHUTTLE("Rocket Shuttle", "burb.cosmetic.hat.rocket_shuttle", Formatting.allTags.deserialize("<i><gray>Take off in, T-minus 3 seconds."), BurbCosmeticObtainmentSource.STICKER_SHOP.obtainmentSourceComponent, ItemType.HAT, "rocket_shuttle", ItemRarity.LEGENDARY),
    HAT_TREASURE_YETI("Treasure Yeti", "burb.cosmetic.hat.treasure_yeti", Formatting.allTags.deserialize("<i><gray>Bigfoot's frosty brother."), BurbCosmeticObtainmentSource.STICKER_SHOP.obtainmentSourceComponent, ItemType.HAT, "yeti_hat", ItemRarity.LEGENDARY, isColorable = true),
    HAT_TURTLE_ROCKET("Timmy (Rocket)", "burb.cosmetic.hat.turtle.rocket", Formatting.allTags.deserialize("<i><gray>I'm a rocket man!"), BurbCosmeticObtainmentSource.STICKER_SHOP.obtainmentSourceComponent, ItemType.HAT, "turtle_hat_rocket", ItemRarity.LEGENDARY),
    // Mythic
    HAT_CRAZY_DAVE("Crazy Dave", "burb.cosmetic.hat.crazy_dave", Formatting.allTags.deserialize("<i><gray>*unintelligible rambling noises*."), BurbCosmeticObtainmentSource.HIDDEN.obtainmentSourceComponent, ItemType.HAT, "crazy_dave", ItemRarity.MYTHIC),
    // Special
    ACCESSORY_GOLDEN_GNOME("Golden Gnome", "burb.cosmetic.accessory.golden_gnome", Formatting.allTags.deserialize("<i><gray>Fa La La!"), BurbCosmeticObtainmentSource.GNOME_TASK.obtainmentSourceComponent, ItemType.ACCESSORY, "golden_gnome", ItemRarity.SPECIAL, isHidden = true);
}